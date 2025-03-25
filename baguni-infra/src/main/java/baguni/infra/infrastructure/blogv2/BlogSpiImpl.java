package baguni.infra.infrastructure.blogv2;

import java.net.URL;
import java.util.List;

import baguni.annotation.DomainSpi;
import baguni.domain.blog.Blog;
import baguni.domain.blog.FeedSupportingBlog;
import baguni.spi.BlogSpi;

/**
 * TODO: 기존 DataHandler와 유사한 로직 수행
 */
@DomainSpi
public class BlogSpiImpl implements BlogSpi {

	@Override
	public List<Blog> getBlogs() {
		throw new UnsupportedOperationException("Not implemented yet");
	}

	@Override
	public Blog getByUrl(URL url) {
		throw new UnsupportedOperationException("Not implemented yet");
	}

	@Override
	public FeedSupportingBlog getFeedBlog() {
		throw new UnsupportedOperationException("Not implemented yet");
	}
}
