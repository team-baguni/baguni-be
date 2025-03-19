package baguni.batch.domain.link.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import baguni.BaguniBatchApplication;
import baguni.batch.domain.link.LinkService;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ActiveProfiles("local")
@SpringBootTest(classes = BaguniBatchApplication.class)
class LinkServiceTest {

	@Autowired
	LinkService linkService;

	/**
	 * DB에 블로그 목록이 반드시 존재해야 한다.
	 */
	@Test
	@DisplayName("링크 분석 후 결과를 DB에 잘 저장하는지 테스트")
	public void test() {
		var url = "https://techblog.lycorp.co.jp/ko/2024-frontend-global-workshop-recap";
		linkService.updateLink(url);
	}

}