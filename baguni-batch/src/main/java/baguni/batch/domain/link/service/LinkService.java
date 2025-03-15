package baguni.batch.domain.link.service;

import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import baguni.batch.domain.analyzer.ArticleAnalyzer;
import baguni.batch.domain.crawler.LinkCrawler;
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

	private ArticleAnalyzer articleAnalyzer;

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
	}

	/**
	 * 이전 작업 종료 후 5초마다 1번씩 실행
	 */
	@Scheduled(fixedDelay = 5, timeUnit = TimeUnit.SECONDS)
	public void analyzeSummaryAndUpdate() {
		for (var link : linkDataHandler.getLinksForSummary()) {
			log.info("Scheduled: 요약 작업 시작 url={}", link.url());
			linkDataHandler.updateLink(
				new LinkCommand.UpdateSummary(
					link.url(),
					articleAnalyzer.summarize(link.content())
				)
			);
		}
	}

	/**
	 * 이전 작업 종료 후 5초마다 1번씩 실행
	 */
	@Scheduled(fixedDelay = 5, timeUnit = TimeUnit.SECONDS)
	public void analyzeCategoriesAndUpdate() {
		for (var link : linkDataHandler.getLinksForCategories()) {
			log.info("Scheduled: 카테고리 작업 시작 url={}", link.url());
			linkDataHandler.updateLink(
				new LinkCommand.UpdateCategories(
					link.url(),
					articleAnalyzer.categorize(link.summary())
				)
			);
		}
	}
}
