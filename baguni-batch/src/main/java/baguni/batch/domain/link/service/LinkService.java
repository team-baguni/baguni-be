package baguni.batch.domain.link.service;

import java.util.concurrent.TimeUnit;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import baguni.batch.domain.analyzer.AiArticleAnalyzer;
import baguni.batch.lib.Task;
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

		if (!link.isBlogFeed()) // 일반 링크는 테스트 후 수행 예정
			return;

		var crawled = linkCrawler.crawl(url);
		var updatedLink = linkDataHandler.updateLink(
			new LinkCommand.UpdateWithCrawledData(
				url,
				crawled.title(),
				crawled.description(), // 삭제 예정
				crawled.imageUrl(),
				crawled.content()
			)
		);
		// LinkAnalyzeTask(updatedLink).run(); // 분석까지 원큐에 할 경우 주석 해제
	}

	/**
	 * (1) 기존 구현 버전
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

	/**
	 * (2) FluentAPI 버전
	 *     - 실패 단위 == 링크 1개
	 *     - 1. 메시지큐를 이용할 경우, 메시지별로 Task 실행
	 *          ex. LinkAnalyzeTask(sourceLink).run();
	 *     - 2. 새 쓰레드에서 처리하고 싶은 경우
	 *          ex. new Thread( LinkAnalyzeTask(sourceLink) ).start();
	 */
	private Runnable LinkAnalyzeTask(LinkResult link) {
		return new Task()
			.using(
				link.content()
			).then((content) -> {
				var summary = articleAnalyzer.summarize(content);
				linkDataHandler.updateLink(new LinkCommand.UpdateSummary(link.url(), summary));
				return summary;
			}).andThen((summary) -> {
				var category = articleAnalyzer.categorize(summary);
				linkDataHandler.updateLink(new LinkCommand.UpdateCategories(link.url(), category));
			}).onFailure((ex -> log.error("분석 실패 url:{}", link.url(), ex)));
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

	private int getCharacterCount(String data) {
		return data.codePointCount(0, data.length());
	}
}
