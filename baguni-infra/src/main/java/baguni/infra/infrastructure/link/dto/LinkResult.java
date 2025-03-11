package baguni.infra.infrastructure.link.dto;

import java.time.LocalDateTime;

public record LinkResult(
	Long id,
	String url,
	String title,
	String description,
	String summary,
	String imageUrl,
	Boolean isRss,
	LocalDateTime updatedAt
) {
}
