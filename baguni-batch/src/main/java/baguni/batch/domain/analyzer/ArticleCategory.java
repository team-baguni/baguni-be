package baguni.batch.domain.analyzer;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

// TODO: 이 Enum을 DB 값으로 연결할 것.
public enum ArticleCategory {

	/**
	 * 개발
	 */
	IT_FRONTEND("프론트엔드 개발",
		__CATEGORIZE_HINT__(
			"UI/UX", "리액트", "컴포넌트", "렌더링"
		)),

	IT_BACKEND("백엔드 개발",
		__CATEGORIZE_HINT__(
			"AWS", "서버", "Spring", "Node.js", "Docker", "Kubernetes"
		)),

	IT_AI("인공지능",
		__CATEGORIZE_HINT__(
			"머신러닝", "RAG", "파인튜닝", "LLM"
		)),
	/**
	 * 디자인
	 */
	DESIGN("디자인",
		__CATEGORIZE_HINT__(
			"UI", "Figma", "Adobe"
		)),
	/**
	 * 마케팅
	 */
	MARKETING("마케팅",
		__CATEGORIZE_HINT__(
			/* 카테고리 분류에 참고할 키워드를 작성하세요 */
		)),
	/**
	 * 프로덕트 매니징
	 */
	PM("프로덕트 매니징",
		__CATEGORIZE_HINT__(
			/* 카테고리 분류에 참고할 키워드를 작성하세요 */
		)),
	/**
	 * 음악
	 */
	MUSIC("음악",
		__CATEGORIZE_HINT__(
			/* 카테고리 분류에 참고할 키워드를 작성하세요 */
		)),
	/**
	 * 분류할 수 없음
	 */
	UNKNOWN("기타",
		__CATEGORIZE_HINT__(
			/* 카테고리 분류에 참고할 키워드를 작성하세요 */
		)),
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

	@SafeVarargs
	static <E> List<E> __CATEGORIZE_HINT__(E... Elements) {
		return Arrays.stream(Elements).toList();
	}
}
