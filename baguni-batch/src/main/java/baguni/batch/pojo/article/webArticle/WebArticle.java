package baguni.batch.pojo.article.webArticle;

import java.net.URL;
import java.util.List;

import baguni.batch.pojo.common.WebResource;
import baguni.batch.pojo.image.ExternalImage;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;

@Getter
public abstract class WebArticle extends WebResource implements Categorizable {

	private final Title title;

	private final Category category;

	private final List<Keyword> keywords;

	private final ExternalImage coverImage;

	@Builder
	protected WebArticle(
		URL url, Title title, Category category, List<Keyword> keywords,
		ExternalImage coverImage
	) {
		super(url);
		this.title = title;
		this.category = category;
		this.keywords = keywords;
		this.coverImage = coverImage;
	}

	public WebArticle(WebArticle other) {
		super(other.getUrl());
		this.title = other.title;
		this.category = other.category;
		this.keywords = other.keywords;
		this.coverImage = other.coverImage;
	}

	public record Title(@NonNull String value) {
	}

	public record Keyword(@NonNull String value) {
	}
}
