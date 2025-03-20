package baguni.batch.pojo.article.blogArticle;

import java.net.URL;
import java.util.List;

import baguni.batch.pojo.article.webArticle.WebArticle;
import baguni.batch.pojo.article.webArticle.Category;
import baguni.batch.pojo.blog.Blog;
import baguni.batch.pojo.image.ExternalImage;
import lombok.Builder;

public class BlogArticle extends WebArticle {

	private final Blog blog; // 글이 작성된 블로그

	@Builder
	protected BlogArticle(
		URL url, Title title,
		Category category, List<Keyword> keywords,
		ExternalImage coverImage,
		Blog blog
	) {
		super(url, title, category, keywords, coverImage);
		this.blog = blog;
	}

	public BlogArticle(Blog blog, WebArticle article) {
		super(article);
		this.blog = blog;
	}
}
