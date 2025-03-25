package baguni.domain.blog;

import java.net.URL;

import lombok.Getter;

@Getter
public class FeedSupportingBlog extends Blog {

	// Ex. https://medium.com/feed/29cm
	private final URL feedUrl;

	public FeedSupportingBlog(URL url, URL feedUrl) {
		super(url);
		this.feedUrl = feedUrl;
	}
}
