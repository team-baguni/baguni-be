package baguni.batch.domain.ai.ollama;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.function.ThrowingSupplier;

import com.fasterxml.jackson.databind.ObjectMapper;

import baguni.batch.domain.ai.AiAgent;
import baguni.batch.domain.ai.ollama.dto.OllamaCategorizeResult;
import baguni.batch.domain.ai.ollama.dto.OllamaGetKeywordsResult;
import baguni.batch.domain.ai.ollama.dto.OllamaSubCategorizeResult;
import baguni.batch.domain.ai.ollama.dto.OllamaSummarizeResult;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component("ollama-local")
public class OllamaAgent extends AiAgent {

	private final OllamaApi ollamaApi;
	private final String MODEL = "exaone3.5";

	@Autowired
	public OllamaAgent(OllamaApi ollamaApi) {
		super("Ollama");
		this.ollamaApi = ollamaApi;
	}

	@Override
	public String summarize(String text) {
		return tryCatchWrapper("Ollama 요약", () -> {
			var request = OllamaChatRequestBuilder.Summarize(MODEL, text);
			var response = ollamaApi.chat(request);
			var resultJsonString = response.message().content();
			var result = new ObjectMapper().readValue(resultJsonString, OllamaSummarizeResult.class);
			return result.summary();
		});
	}

	@Override
	public String categorize(String text) {
		return tryCatchWrapper("Ollama 카테고리 추출", () -> {
			var request = OllamaChatRequestBuilder.Categorize(MODEL, text);
			var response = ollamaApi.chat(request);
			var resultJsonString = response.message().content();
			var result = new ObjectMapper().readValue(resultJsonString, OllamaCategorizeResult.class);
			return result.category();
		});
	}

	@Override
	public List<String> getKeywords(String text) {
		return tryCatchWrapper("Ollama 키워드 목록 추출", () -> {
			var keywords = new ArrayList<String>();

			// TODO: (개발 --> 개발 하위 카테고리 선택) 처럼 하위 카테고리 별로 다른 프롬프트 실행할 것
			var request1 = OllamaChatRequestBuilder.SubCategorize(MODEL, text);
			var response1 = ollamaApi.chat(request1);
			var result1JsonString = response1.message().content();
			var result1 = new ObjectMapper().readValue(result1JsonString, OllamaSubCategorizeResult.class);
			var result1Cleaned = cleanWords(result1.subcategory());
			keywords.add(result1Cleaned);

			var request2 = OllamaChatRequestBuilder.GetKeywords(MODEL, text);
			var response2 = ollamaApi.chat(request2);
			var result2JsonString = response2.message().content();
			var result2 = new ObjectMapper().readValue(result2JsonString, OllamaGetKeywordsResult.class);
			var result2Cleaned = result2.keywords().stream().map(this::cleanWords).toList();
			keywords.addAll(result2Cleaned);

			return keywords;
		});
	}

	// 강조 표시 특수 문자 등의 프롬프트 꾸미기 문자 제거
	// 간혹 프롬프트 결과에서 **Backend** 처럼 강조가 붙는 경우 있음.
	private String cleanWords(String word) {
		return word.replaceAll("\\*", "");
	}

	private <T> T tryCatchWrapper(String action, ThrowingSupplier<T> supplier) {
		long start = System.currentTimeMillis();
		try {
			return supplier.get();
		} catch (Exception e) {
			log.error("{} 에러 발생: {}", action, e.getMessage(), e);
			throw new RuntimeException(e);
		} finally {
			long end = System.currentTimeMillis();
			log.info("{} 소요 시간: {}ms", action, end - start);
		}
	}
}
