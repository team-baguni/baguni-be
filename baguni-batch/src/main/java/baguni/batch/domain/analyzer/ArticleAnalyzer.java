package baguni.batch.domain.analyzer;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import baguni.batch.domain.ai.AiAgent;
import baguni.batch.lib.SplitText;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class ArticleAnalyzer {

	private final AiAgent aiAgent;

	public ArticleAnalyzer(@Qualifier("ollama-local") AiAgent aiAgent) {
		this.aiAgent = aiAgent;
	}

	public AnalyzeResult analyze(String text) {
		long start = System.currentTimeMillis();
		log.info("Analyzer 분석 시작. 데이터={}", text);

		var summary = new SplitText(text)
			.byCharacterCount(1000).stream()
			.map(subtext -> subtext.replaceAll("\n", ""))
			.map(subText -> aiAgent.summarize(subText))
			.collect(Collectors.joining());

		var category = aiAgent.categorize(summary);

		var keywords = aiAgent.getKeywords(summary);

		long end = System.currentTimeMillis();
		log.info("Analyzer 분석 종료. 총 소요 시간:{}ms", end - start);

		return AnalyzeResult
			.builder()
			.summary(summary)
			.category(category)
			.keywords(keywords)
			.build();
	}

	/**
	 * @deprecated 삭제 예정
	 */
	public String summarize(String content) {
		return "";
	}

	/**
	 * @deprecated 삭제 예정
	 */
	public List<String> categorize(String content) {
		return List.of();
	}
}
