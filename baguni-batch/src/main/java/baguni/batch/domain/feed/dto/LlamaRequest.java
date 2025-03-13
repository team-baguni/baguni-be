package baguni.batch.domain.feed.dto;

import lombok.Builder;

@Builder
public record LlamaRequest(
	String model,
	Boolean stream,
	String prompt
) {
}
