package baguni.batch.domain.ai.ollama.dto;

import java.util.List;
import java.util.Map;

import lombok.Builder;

// Reference: https://github.com/ollama/ollama/blob/main/docs/api.md#parameters-1
@Builder
public record OllamaChatRequest(
	String model,
	Boolean stream,
	List<OllamaMessage> messages,
	Map<?, ?> format
) {
}
