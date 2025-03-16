package baguni.batch.domain.ai.ollama;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.service.annotation.PostExchange;

/**
 * Ollama 로컬 서버와 통신하기 위한 Api Client
 */
public interface OllamaApi {

	@PostExchange(url = "/api/generate")
	LlamaResponse sendRequest(@RequestBody LlamaRequest request);
}

