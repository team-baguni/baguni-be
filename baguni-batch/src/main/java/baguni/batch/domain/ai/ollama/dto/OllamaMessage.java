package baguni.batch.domain.ai.ollama.dto;

import lombok.Builder;

// Ref : https://github.com/ollama/ollama/blob/main/docs/api.md#examples-1
@Builder
public record OllamaMessage(
	String role,
	String content
) {
}
