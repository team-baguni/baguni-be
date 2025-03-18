package baguni.batch.domain.ai.ollama;

public record OllamaResponse(
	String model,
	String createdAt,
	String response
) {
}
