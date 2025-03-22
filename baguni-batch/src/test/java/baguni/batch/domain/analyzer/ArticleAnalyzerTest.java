package baguni.batch.domain.analyzer;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import baguni.BaguniBatchApplication;
import baguni.infra.infrastructure.link.dto.LinkResult;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@SpringBootTest(classes = BaguniBatchApplication.class)
@ActiveProfiles("local")
@DisplayName("글 분석 기능 테스트")
class ArticleAnalyzerTest {

	@Autowired
	ArticleAnalyzer articleAnalyzer;

	@Test
	void analyze_test() {
		var content = BlogExamples.NAVER;
		var result = articleAnalyzer.analyze(content);
		log.info(result.toString());
	}
}