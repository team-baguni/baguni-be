package baguni.batch.domain.analyzer.ollama;

import java.util.ArrayList;
import java.util.Arrays;
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
						내용을 최대 400자로 요약하시오.
						###
						내용 : %s
						""", content))
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
				.prompt(String.format(
					"""
						하단에 주어진 내용을 보고, 대분류를 1개 골라 그 결과를 출력하시오.
						반드시 고른 내용만 출력해야 하며, 그외의 말은 하지 마시오
						대분류는 반드시 다음 중 1개를 골라야 합니다.
						- 개발, 디자인, 마케팅, 교양, 음악, 기타
						아래 데이터 패턴을 참고하여 적절한 대분류를 추론하세요.
						- AWS, 클라우드 = 개발
						- UX, Figma, Adobe = 디자인
						###
						내용 : %s
						""", content))
				.build()
		).response();
		categories.add(mainCategory);

		var subCategories = Arrays.stream(ollamaApi.sendRequest(
			LlamaRequest
				.builder()
				.model("llama3.2-korean")
				.stream(false)
				.prompt(String.format(
					"""
						내용을 보고 핵심 키워드를 최대 5개 추론하시오.
						**반드시 키워드 값만, 개행 없이 쉼표(",")로 나열하시오.**
						키워드는 반드시 다음이 제외되어야 합니다.
						- 개발, 디자인, 마케팅, 교양, 음악, 기타
						###
						내용 : %s
						""", content))
				.build()
		).response().split(", ")).toList();
		categories.addAll(subCategories);

		return categories;
	}
}
