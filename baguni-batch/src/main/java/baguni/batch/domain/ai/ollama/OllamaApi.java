package baguni.batch.domain.ai.ollama;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.service.annotation.PostExchange;

import baguni.batch.domain.ai.ollama.dto.OllamaChatResponse;
import baguni.batch.domain.ai.ollama.dto.OllamaGenerateRequest;
import baguni.batch.domain.ai.ollama.dto.OllamaGenerateResponse;
import baguni.batch.domain.ai.ollama.dto.OllamaChatRequest;

/**
 * Ollama 로컬 서버와 통신하기 위한 Api Client
 */
public interface OllamaApi {

	@PostExchange(url = "/api/generate")
	OllamaGenerateResponse generate(@RequestBody OllamaGenerateRequest request);

	@PostExchange(url = "/api/chat")
	OllamaChatResponse chat(@RequestBody OllamaChatRequest request);
}

