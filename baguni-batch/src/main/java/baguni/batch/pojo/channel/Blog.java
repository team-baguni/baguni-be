package baguni.batch.pojo.channel;

import baguni.batch.pojo.resource.WebLink;
import lombok.Getter;

@Getter
public class Blog {

	private final WebLink blogLink;

	public Blog(WebLink blogLink) {
		this.blogLink = blogLink;
	}
}
