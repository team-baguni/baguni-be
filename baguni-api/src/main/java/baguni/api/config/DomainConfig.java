package baguni.api.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;

import baguni.annotation.DomainApi;
import baguni.annotation.DomainSpi;
import baguni.domain.blog.Blog;
import baguni.domain.blog.BlogArticle;

@Configuration
@ComponentScan(
	basePackageClasses = {
		Blog.class, BlogArticle.class
	},
	includeFilters = @ComponentScan.Filter(
		type = FilterType.ANNOTATION,
		classes = {
			DomainApi.class,
			DomainSpi.class
		}
	)
)
public class DomainConfig {
}
