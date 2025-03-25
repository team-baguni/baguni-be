package baguni.spi;

import java.net.URL;
import java.util.List;

import baguni.domain.blog.Blog;
import baguni.domain.blog.FeedSupportingBlog;

/**
 *
 */
public interface BlogSpi {

	List<Blog> getBlogs();

	Blog getByUrl(URL url);

	FeedSupportingBlog getFeedBlog();
}
