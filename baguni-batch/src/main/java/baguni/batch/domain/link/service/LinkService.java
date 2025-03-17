package baguni.batch.domain.link.service;

import java.util.concurrent.TimeUnit;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import baguni.batch.domain.analyzer.AiArticleAnalyzer;
import baguni.batch.domain.analyzer.CreatePipeline;
import baguni.batch.domain.crawler.LinkCrawler;
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
	 * (1) 기존 구현 버전
	 *     - 이전 작업 종료 후 30분마다 1번씩 실행
	 *     - 실패 단위 == 작업 성격 (카테고리/분석)
	 */
	@Scheduled(fixedDelay = 30, timeUnit = TimeUnit.MINUTES)
	public void analyzeEveryPossibleLink() {
		analyzeSummaryAndUpdate();
		analyzeCategoriesAndUpdate();
	}

	/**
	 * (2) FluentAPI 버전
	 *     - 실패 단위 == 링크 1개
	 *     - 메시지큐를 이용할 경우, 메시지별로 아래 메서드 순차 실행하면 될 듯!
	 */
	public void analyzeSingleLink(LinkResult link) {
		CreatePipeline
			.using(
				link.content()
			)
			.then((content) -> {
				var summary = articleAnalyzer.summarize(content);
				linkDataHandler.updateLink(new LinkCommand.UpdateSummary(link.url(), summary));
				return summary;
			})
			.andThen((summary) -> {
				var category = articleAnalyzer.categorize(summary);
				linkDataHandler.updateLink(new LinkCommand.UpdateCategories(link.url(), category));
			})
			.onFailure((exception -> {
				log.error("분석 실패 url:{}", link.url(), exception);
			}))
			.runPipeline();
	}

	// Internal helper functions ------------------------------

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
