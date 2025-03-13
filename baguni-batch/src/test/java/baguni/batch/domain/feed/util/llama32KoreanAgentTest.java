package baguni.batch.domain.feed.util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import baguni.BaguniBatchApplication;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@SpringBootTest(classes = BaguniBatchApplication.class)
@ActiveProfiles("local")
@DisplayName("OllamaAgent 작동 테스트")
class llama32KoreanAgentTest {

	@Autowired
	Llama32KoreanAgent llama32KoreanAgent;

	@Test
	@DisplayName("정상 작동 테스트")
	public void sendPrompt() {
		var result = llama32KoreanAgent.summarize(BlogExamples.AWS_REINVENT);
		log.info("{}", result);
	}
}