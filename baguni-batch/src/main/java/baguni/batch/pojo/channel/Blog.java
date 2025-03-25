package baguni.batch.pojo.channel;

import java.util.List;

import baguni.batch.pojo.resource.WebLink;
import lombok.Getter;

@Getter
public abstract class Blog {

	private final WebLink blogLink;

	public abstract List<BlogArticle> getArticles();

	public Blog(
		WebLink blogLink
	) {
		this.blogLink = blogLink;
	}
}
