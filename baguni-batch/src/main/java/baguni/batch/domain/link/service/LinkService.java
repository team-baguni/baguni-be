package baguni.batch.domain.link.service;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import baguni.batch.domain.analyzer.ArticleAnalyzer;
import baguni.batch.domain.crawler.LinkCrawler;
import baguni.batch.domain.crawler.LinkCrawlResult;
import baguni.infra.model.link.Link;
import baguni.infra.infrastructure.link.LinkDataHandler;
import baguni.infra.infrastructure.link.dto.LinkMapper;
import baguni.infra.infrastructure.link.dto.LinkResult;
import io.opentelemetry.instrumentation.annotations.SpanAttribute;
import io.opentelemetry.instrumentation.annotations.WithSpan;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class LinkService {

	private final LinkDataHandler linkDataHandler;
	private final LinkCrawler linkCrawler;
	private final LinkMapper linkMapper;

	private ArticleAnalyzer articleAnalyzer;

	@Autowired
	@Qualifier("local-ollama3.2-korean")
	public void setArticleAnalyzer(ArticleAnalyzer articleAnalyzer) {
		this.articleAnalyzer = articleAnalyzer;
	}

	@WithSpan
	@Transactional(readOnly = true)
	public LinkResult getLinkResultByUrl(String url) {
		Link link = linkDataHandler.getLink(url);
		return linkMapper.toLinkResult(link);
	}

	@WithSpan
	@Transactional
	public void analyzeAndUpdateLink(@SpanAttribute("url") String url) {
		Link link = linkDataHandler.getLink(url);
		LinkCrawlResult crawlResult = linkCrawler.crawl(url);

		if (StringUtils.isNotEmpty(crawlResult.title())) {
			link.updateMetadata(crawlResult.title(), crawlResult.description(), crawlResult.imageUrl());
		}

		// TODO: youtube 링크는 어떻게 작동할지 확실하지 않아서, 일단 Feed 블로그만 하도록 처리
		if (link.isBlogFeed()) {
			log.info("본문 내용 = {}", crawlResult.content());
			String summary = articleAnalyzer.summarize(crawlResult.content());
			log.info("요약 결과 = {}", summary);
			link.updateSummary(summary);
		}
	}
}
