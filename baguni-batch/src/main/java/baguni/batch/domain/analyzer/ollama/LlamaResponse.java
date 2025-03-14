package baguni.batch.domain.analyzer.ollama;

public record LlamaResponse(
	String model,
	String createdAt,
	String response
) {
}
