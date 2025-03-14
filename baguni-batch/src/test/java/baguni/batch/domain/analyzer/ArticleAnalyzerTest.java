package baguni.batch.domain.analyzer;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import baguni.BaguniBatchApplication;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@SpringBootTest(classes = BaguniBatchApplication.class)
@ActiveProfiles("local")
@DisplayName("글 분석 기능 테스트")
class ArticleAnalyzerTest {

	@Autowired
	@Qualifier("local-ollama3.2-korean")
	ArticleAnalyzer articleAnalyzer;

	@Test
	void summarize() {
		var result = articleAnalyzer.summarize(BlogExamples.AWS_REINVENT);
		log.info("{}", result);
	}

	@Test
	void categorize() {
		var result = articleAnalyzer.categorize("");
		log.info("{}", result);
	}
}