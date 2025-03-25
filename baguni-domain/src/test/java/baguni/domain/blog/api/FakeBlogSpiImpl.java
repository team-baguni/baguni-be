package baguni.domain.blog.api;

import java.net.URL;
import java.util.List;

import baguni.domain.blog.Blog;
import baguni.domain.blog.FeedSupportingBlog;
import baguni.spi.BlogSpi;

public class FakeBlogSpiImpl implements BlogSpi {

	@Override
	public List<Blog> getBlogs() {
		try {
			return List.of(
				new Blog(
					new URL("https://www.example1.com/15cm")
				),
				new FeedSupportingBlog(
					new URL("https://www.example2.com/29cm"),
					new URL("https://www.example2.com/feed/29cm")
				));
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public Blog getByUrl(URL url) {
		try {
			return new Blog(url);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public FeedSupportingBlog getFeedBlog() {
		try {
			return new FeedSupportingBlog(
				new URL("https://www.example2.com/29cm"),
				new URL("https://www.example2.com/feed/29cm")
			);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}
