package baguni.batch.domain.analyzer;

import java.util.List;

public interface TextAnalyzable {

	/**
	 * @param text 요약할 내용
	 * @return 요약된 결과
	 */
	String summarize(String text);

	/**
	 * @param text 분류할 내용
	 * @return 분류 결과
	 */
	String categorize(String text);

	/**
	 * @param text 키워드를 추출할 내용
	 * @return 추출 결과
	 */
	List<String> getKeywords(String text);
}
