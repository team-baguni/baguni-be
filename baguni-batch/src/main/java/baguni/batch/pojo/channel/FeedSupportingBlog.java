package baguni.batch.pojo.channel;

import java.util.List;

import baguni.batch.pojo.resource.WebLink;
import lombok.Getter;

@Getter
public class FeedSupportingBlog extends Blog {

	private final WebLink feedLink;

	public FeedSupportingBlog(
		WebLink blogLink,
		WebLink feedLink
	) {
		super(blogLink);
		this.feedLink = feedLink;
	}

	@Override
	public List<BlogArticle> getArticles() {
		return List.of();
	}
}
