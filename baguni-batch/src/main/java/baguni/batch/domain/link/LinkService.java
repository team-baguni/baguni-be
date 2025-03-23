package baguni.batch.domain.link;

import java.util.ArrayList;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import baguni.batch.domain.analyzer.ArticleAnalyzer;
import baguni.batch.domain.crawler.LinkCrawler;
import baguni.batch.domain.validator.LinkValidator;
import baguni.infra.infrastructure.link.dto.LinkCommand;
import baguni.infra.infrastructure.link.LinkDataHandler;
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

	@WithSpan
	public void updateLink(String url) {
		var link = linkDataHandler.getLink(url);

		if (!linkValidator.isAccessible(url)) {
			linkDataHandler.updateLink(new LinkCommand.UpdateIsValid(url, Boolean.FALSE));
			return;
		}
		if (link.getDaysPassed() < 90)
			return;
		if (!link.isBlogFeed()) // 일반 링크는 테스트 후 수행 예정
			return;

		var crawled = linkCrawler.crawl(url);

		linkDataHandler.updateLink(
			new LinkCommand.UpdateWithCrawledData(
				url,
				crawled.title(),
				crawled.description(), // 삭제 예정
				linkValidator.isAccessible(crawled.imageUrl()) ? crawled.imageUrl() : basicImageUrl,
				crawled.content()
			)
		);

		/* (대체 안) 비동기로 분석 시작 예시.
		 * By default, this runs with fork/join pool.
		 * https://medium.com/@reetesh043/a-deep-dive-into-javas-forkjoinpool-mechanics-556f82d160fb
		 */
		CompletableFuture
			.supplyAsync(() -> articleAnalyzer.analyze(crawled.content()))
			.thenAccept((analyzeResult) -> { /* 분석 결과를 저장 */
				// 1. 요약 업데이트 (테스트용)
				linkDataHandler.updateLink(new LinkCommand.UpdateSummary(link.url(), analyzeResult.summary()));
				// 2. 카테고리 업데이트 (테스트용)
				var arr = new ArrayList<String>();
				arr.add(analyzeResult.category());
				arr.addAll(analyzeResult.keywords());
				linkDataHandler.updateLink(new LinkCommand.UpdateCategories(link.url(), arr));
			}).whenComplete((__, error) -> {
				if (Objects.nonNull(error)) {
					log.error("분석 실패: {}", url, error);
				} else {
					log.info("분석 성공: {}", url);
				}
			});
	}

	/**
	 * @deprecated
	 *     이전 작업 종료 후 30분마다 1번씩 실행한다.
	 *     - 실패 단위 == 작업 성격 (카테고리/분석)
	 *     - 단점 == 로직 전체가 DB 특정 칼럼의 Null 여부에 의존한다.
	 *       참고: {@link baguni.infra.infrastructure.link.LinkRepository}
	 */
	// @Scheduled(fixedDelay = 30, timeUnit = TimeUnit.MINUTES)
	public void analyzeEveryPossibleLink() {
		analyzeSummaryAndUpdate();
		analyzeCategoriesAndUpdate();
	}

	// Internal functions ---------------------------------------------

	/**
	 * @deprecated
	 */
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

	/**
	 * @deprecated
	 */
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

	/**
	 * @deprecated
	 */
	private int getCharacterCount(String data) {
		return data.codePointCount(0, data.length());
	}
}
