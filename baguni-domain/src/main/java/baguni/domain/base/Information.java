package baguni.domain.base;

import java.util.List;

import lombok.Getter;
import lombok.NonNull;

@Getter
public abstract class Information {

	private final Title title;
	private final Summary summary;
	private final InformationCategory informationCategory;
	private final List<Keyword> keywords;

	public Information(
		Title title,
		Summary summary,
		InformationCategory informationCategory,
		List<Keyword> keywords
	) {
		this.title = title;
		this.summary = summary;
		this.informationCategory = informationCategory;
		this.keywords = keywords;
	}

	public Information(Information other) {
		this.title = other.title;
		this.summary = other.summary;
		this.informationCategory = other.informationCategory;
		this.keywords = other.keywords;
	}

	public record Title(@NonNull String value) {
	}

	public record Keyword(@NonNull String value) {
	}

	public record Summary(@NonNull String summary) {
	}
}
