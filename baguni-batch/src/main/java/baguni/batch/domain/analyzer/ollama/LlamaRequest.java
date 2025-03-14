package baguni.batch.domain.analyzer.ollama;

import lombok.Builder;

@Builder
public record LlamaRequest(
	String model,
	Boolean stream,
	String prompt
) {
}
