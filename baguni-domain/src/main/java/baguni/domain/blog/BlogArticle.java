package baguni.domain.blog;

import java.net.URL;
import java.time.LocalDateTime;

import baguni.domain.base.Information;
import baguni.domain.base.WebImage;

public class BlogArticle extends Information {

	private final URL url; // 블로그 글 링크
	private final String urlHash; // url 해시값
	private final Blog sourceBlog; // 작성된 블로그
	private final WebImage coverImage; // 커버 사진
	private final LocalDateTime publishDate; // 글 작성 날짜

	public BlogArticle(
		URL url,
		String urlHash,
		Blog sourceBlog,
		LocalDateTime publishDate,
		WebImage coverImage,
		Information information
	) {
		super(information);
		this.url = url;
		this.urlHash = urlHash;
		this.sourceBlog = sourceBlog;
		this.coverImage = coverImage;
		this.publishDate = publishDate;
	}
}
