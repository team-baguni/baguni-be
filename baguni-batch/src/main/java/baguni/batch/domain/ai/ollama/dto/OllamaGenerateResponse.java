package baguni.batch.domain.ai.ollama.dto;

public record OllamaGenerateResponse(
	String model,
	String createdAt,
	String response
) {
}
