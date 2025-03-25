package baguni.batch.domain.link;

import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import baguni.batch.domain.analyzer.ArticleAnalyzer;
import baguni.batch.domain.crawler.LinkCrawler;
import baguni.batch.domain.validator.LinkValidator;
import baguni.infra.infrastructure.link.dto.LinkCommand;
import baguni.infra.infrastructure.link.LinkDataHandler;
import baguni.infra.infrastructure.link.dto.LinkResult;
import io.opentelemetry.instrumentation.annotations.WithSpan;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class LinkService {

	private final LinkCrawler linkCrawler;
	private final LinkValidator linkValidator;
	private final LinkDataHandler linkDataHandler;
	private final ArticleAnalyzer articleAnalyzer;

	@Value("${basic.image-url}")
	private String basicImageUrl;

	/**
	 * Version 3 (2025.03.24)
	 * 웹사이트에 방문해서 컨텐츠를 분석하고 저장한다.
	 *
	 *   크롤링 + 분석을 어떻게 더 효율적으로 처리할 수 있을까?
	 *
	 *   만약 크롤러가 유투브, 기타 사이트에 대해서도 <본문>을 수집할 수 있다면?
	 *   Ex. 영상 대본 / 영상 설명 등을 추출
	 *   추가 상상인데... 만약 영상/음성 데이터를 해석해서 본문을 생성해서 분석기에게 넘긴다면?
	 *
	 *     - 참고 : https://www.veed.io/ko-KR/tools/auto-subtitle-generator-online
	 *     - 기타 아이디어 : 영상 후처리 솔루션도 만들어 볼 수 있겠다.
	 *                     작업 끝난 영상 마지막에 자막을 영상 자체에 임베드하는 것.
	 *                     유투브 자막이 아닌 영상 자체 자막을 굳이 쓰는 이유는 디자인 때문이다.
	 *                     원하는 디자인, 원하는 크기, 모션을 만들어주면 편집자가 참 편하지 않을까.
	 */
	@WithSpan
	public void updateLink(String url) {

		var link = linkDataHandler.getLink(url);

		/* 접근 불가한 주소는 생략 */
		if (!isAccessible(link)) {
			return;
		}

		/* 최근 업데이트된 글은 생략
		 * MEMO. 2025.03.23 (일) --- 개선 필요성
		 *   링크 갱신 날짜는 DB 필드 1개라도 변경되면 업데이트된다.
		 *   분석 예외 발생에 따른 부분적 업데이트시 갱신 날짜는 최신화되기 때문에
		 *   재시도시 아래 조건에 걸리게 된다.
		 */
		if (link.getDaysPassed() < 90) {
			return;
		}

		/* 크롤링 시작 + DB 저장 */
		var crawled = linkCrawler.crawl(url);
		linkDataHandler.updateLink(
			new LinkCommand.UpdateWithCrawledData(
				url,
				crawled.title(),
				null, // crawled.description() // 삭제 예정
				linkValidator.isAccessible(crawled.imageUrl()) ? crawled.imageUrl() : basicImageUrl,
				null // crawled.content() // 삭제 예정
			)
		);

		/* 게시글 타입만 분석 (Article)
		 * 현재 구현 상 게시글 타입을 단언할 수 있는건 우리가 수집한 블로그 Feed 뿐이라... */
		if (!link.isBlogFeed()) {
			return;
		}

		/* 내용 분석 시작 + DB 저장 */
		var analyzed = articleAnalyzer.analyze(crawled.content());
		var arr = new ArrayList<String>();
		arr.add(analyzed.category());
		arr.addAll(analyzed.keywords());
		linkDataHandler.updateLink(new LinkCommand.UpdateSummary(link.url(), analyzed.summary()));
		linkDataHandler.updateLink(new LinkCommand.UpdateCategories(link.url(), arr));
	}

	public Boolean isAccessible(LinkResult link) {
		var isLinkAccessible = linkValidator.isAccessible(link.url());
		if (!isLinkAccessible && link.isValid()) { /* 원래 유효했으나 유효하지 않게 된 경우 */
			linkDataHandler.updateLink(new LinkCommand.UpdateIsValid(link.url(), Boolean.FALSE));
		} else if (isLinkAccessible && !link.isValid()) { /* 원래 유효하지 않았으나 유효해진 경우 */
			linkDataHandler.updateLink(new LinkCommand.UpdateIsValid(link.url(), Boolean.TRUE));
		}
		return isLinkAccessible;
	}

	// -------------------------------------------------------
	//
	//  아래부터는 과거 버전입니다.
	//  참고용으로 남겨둡니다.
	//
	// -------------------------------------------------------

	/*
	 * Version 2 (2025.03.23)
	 * @deprecated 과거 버전. (기록을 위해 남김)
	 * 비동기 분석에 반드시 SingleThreadExecutor 를 사용할 것.
	 * --> 멀티 쓰레드 처리시 Ollama 서버에 요청을 병렬로 날려서,
	 *     LLM 부하로 인한 timeout 100% 발생
	 */
	/*
	CompletableFuture
		.supplyAsync(() -> articleAnalyzer.analyze(crawled.content()), executor)
		.thenAccept((analyzeResult) -> {
			var arr = new ArrayList<String>();
			arr.add(analyzeResult.category());
			arr.addAll(analyzeResult.keywords());
			linkDataHandler.updateLink(new LinkCommand.UpdateSummary(link.url(), analyzeResult.summary()));
			linkDataHandler.updateLink(new LinkCommand.UpdateCategories(link.url(), arr));
		}).whenComplete((__, error) -> {
			if (Objects.nonNull(error)) {
				log.error("분석 실패: {}", url, error);
			} else {
				log.info("분석 성공: {}", url);
			}
		});
	*/

	/*
	 * Version 1 (2025.03.14)
	 *     이전 작업 종료 후 30분마다 1번씩 실행한다.
	 *     - 실패 단위 == 작업 성격 (카테고리/분석)
	 *     - 단점 == 로직 전체가 DB 특정 칼럼의 Null 여부에 의존한다.
	 *       참고: {@link baguni.infra.infrastructure.link.LinkRepository}
	 */
	/*
	@Scheduled(fixedDelay = 30, timeUnit = TimeUnit.MINUTES)
	public void analyzeEveryPossibleLink() {
		analyzeSummaryAndUpdate();
		analyzeCategoriesAndUpdate();
	}

	private void analyzeSummaryAndUpdate() {
		for (var link : linkDataHandler.getLinksForSummary()) {
			try {
				log.info("요약 시작: {}", link.url());
				linkDataHandler.updateLink(
					new LinkCommand.UpdateSummary(
						link.url(),
						articleAnalyzer.summarize(link.content())
					)
				);
				log.info("요약 성공: {} 글자수: {}", link.url(), getCharacterCount(link.content()));
			} catch (Exception e) {
				log.error("요약 실패: {} 글자수: {}", link.url(), getCharacterCount(link.content()), e);
			}
		}
	}

	private void analyzeCategoriesAndUpdate() {
		for (var link : linkDataHandler.getLinksForCategories()) {
			try {
				log.info("카테고리 추출 시작: {}", link.url());
				linkDataHandler.updateLink(
					new LinkCommand.UpdateCategories(
						link.url(),
						articleAnalyzer.categorize(link.summary())
					)
				);
				log.info("카테고리 추출 성공: {} 글자수: {}", link.url(), getCharacterCount(link.content()));
			} catch (Exception e) {
				log.error("카테고리 추출 실패: {} 글자수: {}", link.url(), getCharacterCount(link.content()), e);
			}
		}
	}
	*/
}
