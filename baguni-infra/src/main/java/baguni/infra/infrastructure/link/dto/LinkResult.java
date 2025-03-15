package baguni.infra.infrastructure.link.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

public record LinkResult(
	Long id,
	String url,
	String title,
	String description,
	String content,
	String summary,
	String categories,
	String imageUrl,
	Boolean isRss, // isFeed로 변경 예정
	LocalDateTime updatedAt
) {
	public long getDaysPassed() {
		return ChronoUnit.DAYS.between(
			updatedAt.toLocalDate(),
			LocalDate.now()
		);
	}

	public boolean isBlogFeed() {
		return this.isRss;
	}
}
