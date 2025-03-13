package baguni.batch.domain.link.service;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import baguni.batch.domain.feed.util.Llama32KoreanAgent;
import baguni.batch.domain.link.dto.LinkAnalyzeResult;
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
	private final LinkAnalyzer linkAnalyzer;
	private final LinkMapper linkMapper;

	private final Llama32KoreanAgent llamaAgent;

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
		LinkAnalyzeResult result = linkAnalyzer.analyze(url);

		if (StringUtils.isNotEmpty(result.title())) {
			link.updateMetadata(result.title(), result.description(), result.imageUrl());
		}

		// 본문 크롤링 데이터 꺼내서 처리하기
		// TODO: 1. youtube 링크는 어떻게 작동할지 확실하지 않아서, 일단 Feed 블로그만 하도록 처리
		//       2. 링크 테이블 isRss를 isFeed로 변경 필요
		String content = result.content();
		if (link.isBlogFeed()) {
			String summary = llamaAgent.summarize(content).response(); // 요약 결과
			link.updateSummary(summary);
		}
	}
}
