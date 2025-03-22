package baguni.batch.domain.analyzer;

import java.util.List;
import java.util.stream.Collectors;

// TODO: 이 Enum을 DB 값으로 연결할 것.
public enum ArticleCategory {

	/**
	 * 개발
	 * TODO: 분류를 더 추가 할 것.
	 */
	IT_FRONTEND
		("프론트엔드 개발", List.of("UI/UX", "리액트", "컴포넌트", "렌더링")),
	IT_BACKEND
		("백엔드 개발", List.of("AWS", "서버", "Spring", "Node.js")),
	IT_DEVOPS
		("데브옵스", List.of("Docker", "Kubernetes", "CI/CD", "Jenkins")),
	IT_AI
		("인공지능", List.of("머신러닝", "RAG", "파인튜닝", "LLM")),

	/**
	 * 디자인
	 * TODO: 위처럼 상세 분류화 할 것.
	 */
	DESIGN
		("디자인", List.of("UI", "Figma", "Adobe")),

	/**
	 * 마케팅
	 * TODO: 위처럼 상세 분류화 할 것.
	 */
	MARKETING
		("마케팅", List.of()),

	/**
	 * 음악
	 * TODO: 위처럼 상세 분류화 할 것.
	 */
	PM
		("프로덕트 매니징", List.of()),

	/**
	 * TODO: 위처럼 상세 분류화 할 것.
	 */
	MUSIC
		("음악", List.of()),

	/**
	 * TODO: 위처럼 상세 분류화 할 것.
	 */
	UNKNOWN
		("기타", List.of()),
	;

	public final String value;
	public final List<String> associates;

	ArticleCategory(String value, List<String> associates) {
		this.value = value;
		this.associates = associates;
	}

	public static ArticleCategory fromValue(String value) throws IllegalArgumentException {
		for (ArticleCategory category : ArticleCategory.values()) {
			if (category.value.equals(value)) {
				return category;
			}
		}
		throw new IllegalArgumentException();
	}

	/**
	 * String Format 예시
	 * [ 개발, 디자인, 마케팅, 음악, 기타 ]
	 */
	public static String toSelectionOption() {
		return "[ "
			+ List.of(ArticleCategory.values()).stream()
				  .map(category -> category.value)
				  .collect(Collectors.joining(", "))
			+ " ]";

	}

	/**
	 * String Format 예시
	 * "- A1, A2, A3 = Value" --> 이렇게 변환합니다.
	 */
	public static String toDataPattern() {
		return List.of(ArticleCategory.values()).stream()
				   .filter(category -> !category.associates.isEmpty())
				   .map(category -> "- " + String.join(", ", category.associates) + " = " + category.value)
				   .collect(Collectors.joining("\n"));
	}
}
