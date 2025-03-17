package baguni.batch.domain.ai.ollama;

import lombok.Builder;

@Builder
public record OllamaRequest(
	String model,
	Boolean stream,
	String prompt
) {
}
