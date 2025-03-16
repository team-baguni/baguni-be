package baguni.batch.domain.analyzer.ollama;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.stereotype.Component;

import baguni.batch.domain.analyzer.ArticleAnalyzer;
import baguni.batch.domain.analyzer.prompt.LlmPrompt;
import io.opentelemetry.instrumentation.annotations.WithSpan;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Component("local-ollama3.2-korean")
public class OllamaKoreanArticleAnalyzer implements ArticleAnalyzer {

	private final OllamaApi ollamaApi;

	@WithSpan
	@Override
	public String summarize(String content) {
		return ollamaApi.sendRequest(
			LlamaRequest
				.builder()
				.model("llama3.2-korean")
				.stream(false)
				.prompt(String.format(LlmPrompt.SUMMARY, content))
				.build()
		).response();
	}

	@WithSpan
	@Override
	public List<String> categorize(String content) {
		var categories = new ArrayList<String>();

		var mainCategory = ollamaApi.sendRequest(
			LlamaRequest
				.builder()
				.model("llama3.2-korean")
				.stream(false)
				.prompt(String.format(LlmPrompt.CATEGORY, content))
				.build()
		).response();
		categories.add(mainCategory);

		var subCategories = Arrays.stream(ollamaApi.sendRequest(
			LlamaRequest
				.builder()
				.model("llama3.2-korean")
				.stream(false)
				.prompt(String.format(LlmPrompt.SUB_CATEGORY, content))
				.build()
		).response().split(", ")).toList();
		categories.addAll(subCategories);

		return categories;
	}
}
