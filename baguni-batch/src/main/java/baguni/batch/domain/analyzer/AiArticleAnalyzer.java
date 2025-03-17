package baguni.batch.domain.analyzer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import baguni.batch.domain.ai.AiAgent;
import baguni.batch.domain.ai.PromptBuilder;
import baguni.batch.lib.SplitText;
import io.opentelemetry.instrumentation.annotations.WithSpan;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class AiArticleAnalyzer implements ArticleAnalyzer {

	private AiAgent aiAgent;

	@Autowired
	@Qualifier("llama3.2-korean")
	public void setAiAgent(AiAgent aiAgent) {
		this.aiAgent = aiAgent;
	}

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
