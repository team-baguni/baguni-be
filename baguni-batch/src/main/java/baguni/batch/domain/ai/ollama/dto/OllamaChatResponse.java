package baguni.batch.domain.ai.ollama.dto;

public record OllamaChatResponse(
	String model,
	OllamaMessage message
) {
}
