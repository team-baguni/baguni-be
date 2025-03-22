package baguni.batch.domain.analyzer;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import baguni.batch.domain.ai.AiAgent;
import baguni.batch.lib.SplitText;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class ArticleAnalyzer {

	private AiAgent aiAgent;

	@Autowired
	@Qualifier("ollama-local")
	public void setAiAgent(AiAgent aiAgent) {
		this.aiAgent = aiAgent;
	}

	// TODO: change param to Article (title, content, author...)
	public AnalyzeResult analyze(String content) {

		var summary = new SplitText(content)
			.byCharacterCount(1000).stream()
			.map(subtext -> subtext.replaceAll("\n", ""))
			.map(subText -> aiAgent.summarize(subText))
			.collect(Collectors.joining());

		var category = aiAgent.categorize(summary);

		var keywords = aiAgent.getKeywords(summary);

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
