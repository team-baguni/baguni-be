package baguni.batch.domain.feed.util;

import org.springframework.stereotype.Component;

import baguni.batch.domain.feed.dto.LlamaRequest;
import baguni.batch.domain.feed.dto.LlamaResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * TODO: 영어 글은 영어 모델로, 한국어 글은 한국어 모델로 처리.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class Llama32KoreanAgent {

	private final OllamaApi ollamaApi;

	private String generateSummaryPrompt(String data) {
		return String.format("""
			내용을 2문장으로 요약하시오.
			###
			내용 : %s
			""", data
		);
	}

	public LlamaResponse summarize(String dataToSummarize) {
		return ollamaApi.sendRequest(
			LlamaRequest
				.builder()
				.model("llama3.2-korean")
				.stream(false) // 나눠 받지 않고 한번에 응답 받기
				.prompt(generateSummaryPrompt(dataToSummarize))
				.build()
		);
	}
}
