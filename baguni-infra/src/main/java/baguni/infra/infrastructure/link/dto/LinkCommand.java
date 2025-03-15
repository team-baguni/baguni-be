package baguni.infra.infrastructure.link.dto;

import java.util.List;

public class LinkCommand {

	public record UpdateImage(
		Long userId,
		String imageUrl,
		String updateImageUrl
	) {
	}

	public record UpdateWithCrawledData(
		String linkUrl,
		String title,
		String description,
		String imageUrl,
		String content
	) {
	}

	public record UpdateSummary(
		String linkUrl,
		String summary
	) {
	}

	public record UpdateCategories(
		String linkUrl,
		List<String> categories
	) {
	}
}
