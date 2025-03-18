package baguni.batch.domain.link.service;

import java.net.URI;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.ResourceAccessException;

import baguni.batch.domain.analyzer.AiArticleAnalyzer;
import baguni.batch.domain.crawler.LinkCrawler;
import baguni.batch.domain.link.util.LinkApi;
import baguni.common.event.EventMessenger;
import baguni.common.event.LinkCheckEvent;
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
	private final LinkApi linkApi;
	private final EventMessenger eventMessenger;

	@Value("${basic.image-url}")
	private String basicImageUrl;

	@WithSpan
	public void updateLink(String url) {
		var link = linkDataHandler.getLink(url);

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
				crawled.imageUrl(),
				crawled.content()
			)
		);
		eventMessenger.send(new LinkCheckEvent(crawled.imageUrl())); // image_url 검사
	}

	@WithSpan
	public void updateImageUrl(String imageUrl) {
		try {
			linkApi.checkImageUrl(URI.create(imageUrl));
		} catch (ResourceAccessException e) {
			log.info("image_url 타임 아웃 발생 url : {}, {},", imageUrl, e.getMessage());
			linkDataHandler.updateLink(new LinkCommand.UpdateImage(imageUrl, basicImageUrl)); // 타임아웃 발생 시 접근할 수 없는 링크
		}
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

	private int getCharacterCount(String data) {
		return data.codePointCount(0, data.length());
	}
}
