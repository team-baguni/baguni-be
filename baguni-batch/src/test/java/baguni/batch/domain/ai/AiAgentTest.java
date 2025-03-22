package baguni.batch.domain.ai;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import baguni.BaguniBatchApplication;
import baguni.batch.domain.ai.ollama.OllamaAgent;
import baguni.batch.domain.analyzer.BlogExamples;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@SpringBootTest(classes = BaguniBatchApplication.class)
@ActiveProfiles("local")
@DisplayName("글 분석 기능 테스트")
class AiAgentTest {

	@Autowired
	OllamaAgent aiAgent;

	@Test
	public void summary() {
		aiAgent.summarize(BlogExamples.NAVER);
	}

	@Test
	public void category() {
		String summary = """
			카카오페이 프론트엔드 팀은 React의 Suspense와 Error Boundary를 활용해 Concurrent UI 패턴을 적극 적용 중이며, 이를 통해 데이터 불러오기 효율성과 사용자 경험 향상에 집중하고 있습니다. 또한, 개발자로서는 사용자 경험 개선이 핵심으로, 오류 관리부터 선제적인 이슈 파악까지 포괄적으로 고려해야 합니다. React의 새로운 기능인 Concurrent Mode 덕분에 다양한 환경의 사용자들에게 최적화된 성능과 반응성을 제공하며, 이를 활용해 'Concurrent UI Pattern'으로 쾌적한 사용자 경험을 구현할 수 있습니다. 이는 React 18 버전에서 지원될 예정입니다. 카카오페이 프론트엔드 팀은 정식 릴리즈 전 실험적 기능을 활용해 사용자 경험 향상을 위한 Concurrent UI Pattern을 도입하고 있으며, 이를 통해 '선언형 컴포넌트'로서 상태 변화에 따른 화면 동적으로 업데이트하는 방식으로 개발 접근법이 달라지고 있습니다. 명령형 컴포넌트는 데이터 불러오기와 같은 동작 과정을 코드로 직접 제어하며 어떻게 화면이 업데이트 될지를 명시합니다. 반면 선언형 컴포넌트는 현재 상태에 따라 자동으로 적절한 UI를 렌더링하여 무엇을 보여줄지를 정의합니다. React에서 동적인 상태 변화에 맞춰 사용자 인터페이스(UI)를 효과적으로 조정하려면, 컴포넌트 기반 설계와 조건부 렌더링 기법을 활용하면 좋습니다. 특히 `useState`와 함께 `if`, `switch`, 또는 JSX 조건문을 사용해 상황에 맞는 UI 요소만 표시되도록 구성할 수 있습니다.
			""";
		var result = aiAgent.categorize(summary);
		log.info(result);
	}

	@Test
	public void getKeywords() {
		String summary = """
			카카오페이 프론트엔드 팀은 React의 Suspense와 Error Boundary를 활용해 Concurrent UI 패턴을 적극 적용 중이며, 이를 통해 데이터 불러오기 효율성과 사용자 경험 향상에 집중하고 있습니다. 또한, 개발자로서는 사용자 경험 개선이 핵심으로, 오류 관리부터 선제적인 이슈 파악까지 포괄적으로 고려해야 합니다. React의 새로운 기능인 Concurrent Mode 덕분에 다양한 환경의 사용자들에게 최적화된 성능과 반응성을 제공하며, 이를 활용해 'Concurrent UI Pattern'으로 쾌적한 사용자 경험을 구현할 수 있습니다. 이는 React 18 버전에서 지원될 예정입니다. 카카오페이 프론트엔드 팀은 정식 릴리즈 전 실험적 기능을 활용해 사용자 경험 향상을 위한 Concurrent UI Pattern을 도입하고 있으며, 이를 통해 '선언형 컴포넌트'로서 상태 변화에 따른 화면 동적으로 업데이트하는 방식으로 개발 접근법이 달라지고 있습니다. 명령형 컴포넌트는 데이터 불러오기와 같은 동작 과정을 코드로 직접 제어하며 어떻게 화면이 업데이트 될지를 명시합니다. 반면 선언형 컴포넌트는 현재 상태에 따라 자동으로 적절한 UI를 렌더링하여 무엇을 보여줄지를 정의합니다. React에서 동적인 상태 변화에 맞춰 사용자 인터페이스(UI)를 효과적으로 조정하려면, 컴포넌트 기반 설계와 조건부 렌더링 기법을 활용하면 좋습니다. 특히 `useState`와 함께 `if`, `switch`, 또는 JSX 조건문을 사용해 상황에 맞는 UI 요소만 표시되도록 구성할 수 있습니다.
			""";
		var result = aiAgent.getKeywords(summary);
		log.info(result.toString());
	}
}