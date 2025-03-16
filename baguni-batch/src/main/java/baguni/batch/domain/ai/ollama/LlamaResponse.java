package baguni.batch.domain.ai.ollama;

public record LlamaResponse(
	String model,
	String createdAt,
	String response
) {
}
