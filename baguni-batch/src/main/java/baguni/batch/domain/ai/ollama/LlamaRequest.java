package baguni.batch.domain.ai.ollama;

import lombok.Builder;

@Builder
public record LlamaRequest(
	String model,
	Boolean stream,
	String prompt
) {
}
