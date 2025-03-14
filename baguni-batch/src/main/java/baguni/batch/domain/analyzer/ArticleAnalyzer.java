package baguni.batch.domain.analyzer;

import java.util.List;

/**
 * 글 상세 분석기 (요약, 카테고리 추출)
 */
public interface ArticleAnalyzer {

	/**
	 *
	 * @param content 요약할 내용
	 * @return 요약된 결과
	 */
	String summarize(String content);

	/**
	 *
	 * @param content 분류할 내용
	 * @return 분류 결과 배열 (ex. ["개발", "백엔드", "..."])
	 */
	List<String> categorize(String content);
}
