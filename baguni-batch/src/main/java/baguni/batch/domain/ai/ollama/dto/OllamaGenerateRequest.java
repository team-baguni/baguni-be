package baguni.batch.domain.ai.ollama.dto;

import lombok.Builder;

@Builder
public record OllamaGenerateRequest(
	String model,
	Boolean stream,
	String prompt
) {
}
