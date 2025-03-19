package baguni.batch.domain.link;

import java.net.URI;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.ResourceAccessException;

import baguni.batch.domain.analyzer.AiArticleAnalyzer;
import baguni.batch.domain.crawler.LinkCrawler;
import baguni.batch.lib.VisitWebsiteApi;
import baguni.infra.infrastructure.link.dto.LinkCommand;
import baguni.infra.infrastructure.link.LinkDataHandler;
import io.opentelemetry.instrumentation.annotations.WithSpan;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class LinkService {

	private final LinkDataHandler linkDataHandler;
	private final LinkCrawler linkCrawler;
	private final AiArticleAnalyzer articleAnalyzer;
	private final VisitWebsiteApi linkApi;

	@Value("${basic.image-url}")
	private String basicImageUrl;

	@WithSpan
	public void updateLink(String url) {
		var link = linkDataHandler.getLink(url);

		if (!isValidUrl(url)) { // 링크 유효성 검사 (유효하지 않은 경우, isValid = false)
			linkDataHandler.updateLink(new LinkCommand.UpdateIsValid(url, Boolean.FALSE));
			return; // 유효하지 않으면 다음 작업을 수행할 이유가 없음.
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
				isValidImageUrl(crawled.imageUrl()) ? crawled.imageUrl() : basicImageUrl, // image_url 유효성 검사
				crawled.content()
			)
		);
	}

	/**
	 *     이전 작업 종료 후 30분마다 1번씩 실행한다.
	 *     - 실패 단위 == 작업 성격 (카테고리/분석)
	 *     - 단점 == 로직 전체가 DB 특정 칼럼의 Null 여부에 의존한다.
	 *       참고: {@link baguni.infra.infrastructure.link.LinkRepository}
	 */
	@Scheduled(fixedDelay = 30, timeUnit = TimeUnit.MINUTES)
	public void analyzeEveryPossibleLink() {
		analyzeSummaryAndUpdate();
		analyzeCategoriesAndUpdate();
	}

	// Internal functions ---------------------------------------------
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

	private boolean isValidImageUrl(String imageUrl) {
		try {
			linkApi.checkUrl(URI.create(imageUrl));
		} catch (ResourceAccessException e) {
			log.info("image_url 타임 아웃 발생, url : {}, {},", imageUrl, e.getMessage());
			return false;
		}
		return true;
	}

	private boolean isValidUrl(String url) {
		try {
			linkApi.checkUrl(URI.create(url));
		} catch (HttpStatusCodeException e) { // 4xx, 5xx 응답 시 발생하는 예외
			HttpStatusCode statusCode = e.getStatusCode();
			if (statusCode == HttpStatus.UNAUTHORIZED || statusCode == HttpStatus.FORBIDDEN) {
				log.error("[에러x 확인 전용] 401 또는 403 응답 : {}", statusCode); // 401, 403 응답하는 상황 확인을 위한 로그
				return true; // 401, 403의 경우 유효하다고 판단
			}
			return false;
		}
		return true;
	}

	private int getCharacterCount(String data) {
		return data.codePointCount(0, data.length());
	}
}
