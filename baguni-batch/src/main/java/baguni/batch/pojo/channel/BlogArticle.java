package baguni.batch.pojo.channel;

import java.time.LocalDateTime;

import baguni.batch.pojo.resource.WebLink;
import baguni.batch.pojo.information.Information;
import baguni.batch.pojo.resource.WebImage;
import lombok.NonNull;

public class BlogArticle extends Information {

	private final WebLink link; // 블로그 글 링크
	private final Writer writer; // 글 작성자
	private final Blog sourceBlog; // 작성된 블로그
	private final WebImage coverImage; // 커버 사진
	private final LocalDateTime publishDate; // 글 작성 날짜

	public BlogArticle(
		WebLink link,
		Writer writer,
		Blog sourceBlog,
		LocalDateTime publishDate,
		WebImage coverImage,
		Information information
	) {
		super(information);
		this.link = link;
		this.writer = writer;
		this.sourceBlog = sourceBlog;
		this.coverImage = coverImage;
		this.publishDate = publishDate;
	}

	public record Writer(@NonNull String name) {
	}
}
