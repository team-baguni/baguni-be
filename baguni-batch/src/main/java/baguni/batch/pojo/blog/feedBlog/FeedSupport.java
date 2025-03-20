package baguni.batch.pojo.blog.feedBlog;

import java.net.URL;
import java.util.List;

import baguni.batch.domain.feed.dto.Article;
import baguni.batch.pojo.common.WebResource;

public class FeedSupport extends WebResource {

	public FeedSupport(URL url) {
		super(url);
	}

	public List<Article> readWith(/* WebAgent agent */) {
		// agent.read(url) ➜ 읽은 결과를 파싱. (FeedService 참고)
		throw new UnsupportedOperationException("구현 예정");
	}
}
