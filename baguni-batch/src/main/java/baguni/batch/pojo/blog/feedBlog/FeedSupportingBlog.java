package baguni.batch.pojo.blog.feedBlog;

import java.net.URL;
import java.util.List;

import baguni.batch.domain.feed.dto.Article;
import baguni.batch.pojo.blog.Blog;
import baguni.batch.pojo.image.ExternalImage;
import lombok.Builder;
import lombok.Getter;

@Getter
public final class FeedSupportingBlog extends Blog {

	private final FeedSupport feed;

	@Builder
	private FeedSupportingBlog(URL blogUrl, URL feedUrl, Title title, ExternalImage coverImage) {
		super(blogUrl, title, coverImage);
		this.feed = new FeedSupport(feedUrl);
	}

	public FeedSupportingBlog(Blog blog, FeedSupport feed) {
		super(blog);
		this.feed = feed;
	}

	public List<Article> getArticleList(/* WebAgent agent */) {
		return feed.readWith(/* agent */);
	}
}
