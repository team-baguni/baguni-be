package baguni.batch.domain.feed.dto;

public record LlamaResponse(
	String model,
	String createdAt,
	String response
) {
}
