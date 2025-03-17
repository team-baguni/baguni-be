package baguni.batch.domain.link.service;

import java.util.concurrent.TimeUnit;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import baguni.batch.domain.analyzer.AiArticleAnalyzer;
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
	private final AiArticleAnalyzer articleAnalyzer;

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
	}

	/**
	 * 이전 작업 종료 후 5분마다 1번씩 실행
	 */
	@Scheduled(fixedDelay = 5, timeUnit = TimeUnit.MINUTES)
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
				log.info("요약 성공: {} 글자수: {}", link.url(), getCharacterCount(link.content()));
			} catch (Exception e) {
				log.error("요약 실패: {} 글자수: {}", link.url(), getCharacterCount(link.content()), e);
			}
		}
	}

	/**
	 * 이전 작업 종료 후 5분마다 1번씩 실행
	 */
	@Scheduled(fixedDelay = 5, timeUnit = TimeUnit.MINUTES)
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
