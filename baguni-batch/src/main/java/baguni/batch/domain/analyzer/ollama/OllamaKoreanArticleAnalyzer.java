package baguni.batch.domain.analyzer.ollama;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import baguni.batch.domain.analyzer.ArticleAnalyzer;
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
				.prompt(String.format(
					"""
						내용을 2문장으로 요약하시오.
						###
						내용 : %s
						""", content))
				.build()
		).response();
	}

	@WithSpan
	@Override
	public List<String> categorize(String summary) {
		// TODO: 추후 구현 예정
		return new ArrayList<>();
	}
}
