package baguni.batch.pojo.blog;

import java.net.URL;

import baguni.batch.pojo.common.WebResource;
import baguni.batch.pojo.image.ExternalImage;
import lombok.Builder;
import lombok.Getter;

@Getter
public class Blog extends WebResource {

	private final Title name;

	private final ExternalImage coverImage;

	@Builder
	protected Blog(URL url, Title title, ExternalImage coverImage) {
		super(url);
		this.name = title;
		this.coverImage = coverImage;
	}

	public Blog(Blog other) {
		super(other.getUrl());
		this.name = other.name;
		this.coverImage = other.coverImage;
	}

	public record Title(String title) {
	}
}
