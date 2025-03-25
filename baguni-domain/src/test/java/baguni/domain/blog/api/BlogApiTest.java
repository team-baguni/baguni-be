package baguni.domain.blog.api;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.Test;

import baguni.api.BlogApi;
import baguni.domain.blog.BlogApiImpl;
import baguni.domain.blog.Blog;
import lombok.extern.slf4j.Slf4j;

@Slf4j
class BlogApiTest {

	@Test
	void should_load_blog() {
		// Given
		BlogApi blogApi = new BlogApiImpl(new FakeBlogSpiImpl());

		// When
		List<Blog> blogs = blogApi.getAll();

		// Then
		System.out.println(blogs);
		assertThat(blogs)
			.extracting(Blog::getUrl)
			.doesNotContainNull()
			.doesNotHaveDuplicates()
		;
	}

	@Test
	void should_load_Feed_Supporting_blog() {

	}
}