package baguni.batch.domain.feed.util;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.service.annotation.PostExchange;

import baguni.batch.domain.feed.dto.LlamaRequest;
import baguni.batch.domain.feed.dto.LlamaResponse;

/**
 * Ollama 로컬 서버와 통신하기 위한 Api Client
 */
public interface OllamaApi {

	@PostExchange(url = "/api/generate")
	LlamaResponse sendRequest(@RequestBody LlamaRequest request);
}

