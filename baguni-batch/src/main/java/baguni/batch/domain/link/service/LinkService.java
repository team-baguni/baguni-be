package baguni.batch.domain.link.service;

import java.net.URI;
import java.util.StringTokenizer;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.ResourceAccessException;

import baguni.batch.domain.analyzer.ArticleAnalyzer;
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
	private final LinkApi linkApi;
	private final EventMessenger eventMessenger;

	private ArticleAnalyzer articleAnalyzer;

	@Value("${basic.image-url}")
	private String basicImageUrl;

	@Autowired
	@Qualifier("local-ollama3.2-korean")
	public void setArticleAnalyzer(ArticleAnalyzer articleAnalyzer) {
		this.articleAnalyzer = articleAnalyzer;
	}

	@WithSpan
	public void updateLink(String url) {
		var link = linkDataHandler.getLink(url);

		if (link.getDaysPassed() < 90)
			return;

		// TODO: 요약 끝나고 삭제해야 함. 그렇지 않으면, 개발 블로그만 링크 업데이트됨.
		if (!link.isBlogFeed())
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
	 * 이전 작업 종료 후 1분마다 1번씩 실행
	 */
	@Scheduled(fixedDelay = 1, timeUnit = TimeUnit.MINUTES)
	public void analyzeSummaryAndUpdate() {
		for (var link : linkDataHandler.getLinksForSummary()) {
			try {
				log.info("요약 시작: {}", link.url());
				linkDataHandler.updateLink(
					new LinkCommand.UpdateSummary(
						link.url(),
						articleAnalyzer.summarize(link.content())
					)
				);
				log.info("요약 성공: {} Token 수: {}", link.url(), getTokenCount(link.content()));
			} catch (Exception e) {
				log.error("요약 실패: {} Token 수: {}", link.url(), getTokenCount(link.content()), e);
			}
		}
	}

	/**
	 * 이전 작업 종료 후 1분마다 1번씩 실행
	 */
	@Scheduled(fixedDelay = 1, timeUnit = TimeUnit.MINUTES)
	public void analyzeCategoriesAndUpdate() {
		for (var link : linkDataHandler.getLinksForCategories()) {
			try {
				log.info("카테고리 추출 시작: {}", link.url());
				linkDataHandler.updateLink(
					new LinkCommand.UpdateCategories(
						link.url(),
						articleAnalyzer.categorize(link.summary())
					)
				);
				log.info("카테고리 추출 성공: {} Token 수: {}", link.url(), getTokenCount(link.content()));
			} catch (Exception e) {
				log.error("카테고리 추출 실패: {} Token 수: {}", link.url(), getTokenCount(link.content()), e);
			}
		}
	}

	private int getTokenCount(String data) {
		return new StringTokenizer(data).countTokens();
	}
}
