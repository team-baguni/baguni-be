package baguni.domain.blog;

import baguni.annotation.DomainApi;
import baguni.api.BlogApi;
import baguni.spi.BlogSpi;

@DomainApi
public class BlogApiImpl implements BlogApi {

	private final BlogSpi blogSpi;

	public BlogApiImpl(BlogSpi blogSpi) {
		this.blogSpi = blogSpi;
	}
}
