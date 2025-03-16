package baguni.batch.domain.analyzer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import baguni.batch.domain.ai.ollama.OllamaAgent;
import baguni.batch.domain.ai.PromptBuilder;
import baguni.batch.domain.analyzer.util.SplitText;
import io.opentelemetry.instrumentation.annotations.WithSpan;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class AiArticleAnalyzer implements ArticleAnalyzer {

	private final OllamaAgent aiAgent;

	@WithSpan
	@Override
	public String summarize(String content) {
		return new SplitText(content)
			.byCharacterCount(400, 4).stream()
			.map(subText -> aiAgent.ask(PromptBuilder.summarize(subText)))
			.collect(Collectors.joining());
	}

	@WithSpan
	@Override
	public List<String> categorize(String content) {
		var categories = new ArrayList<String>();

		var mainCategory = aiAgent.ask(PromptBuilder.getMainCategory(content));
		categories.add(mainCategory);

		var subCategory = aiAgent.ask(PromptBuilder.getSubCategory(content));
		categories.addAll(Arrays.stream(subCategory.split(", ")).toList());

		return categories;
	}
}
