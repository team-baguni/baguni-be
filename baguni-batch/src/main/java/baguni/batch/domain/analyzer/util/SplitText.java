package baguni.batch.domain.analyzer.util;

import java.util.ArrayList;
import java.util.List;

public class SplitText {

	private final String text;

	/**
	 * @param text 자를 Text 원본
	 */
	public SplitText(String text) {
		this.text = text;
	}

	/**
	 *
	 * @param size 자를 문자 개수
	 * @param limit 잘라질 그룹의 최대 개수. limit=1 이면 주어진 size 만큼 1번만 자른다.
	 * @return 잘린 문자열 배열
	 */
	public List<String> byCharacterCount(int size, int limit) {
		var result = new ArrayList<String>();
		var start = 0;
		var count = 0;
		while (start < text.length() && count < limit) {
			// limit 개의 코드 포인트를 포함하는 마지막 인덱스 찾기 (이모티콘도 1개의 글자수로 세기 위함)
			int end = text.offsetByCodePoints(
				start,
				Math.min(size, text.codePointCount(start, text.length()))
			);
			result.add(text.substring(start, end));
			start = end;
			count++;
		}
		return result;
	}

	/**
	 * @param size 자를 문자 개수
	 * @return 잘린 문자열 배열
	 */
	public List<String> byCharacterCount(int size) {
		var result = new ArrayList<String>();
		var start = 0;
		while (start < text.length()) {
			// limit 개의 코드 포인트를 포함하는 마지막 인덱스 찾기 (이모티콘도 1개의 글자수로 세기 위함)
			int end = text.offsetByCodePoints(
				start,
				Math.min(size, text.codePointCount(start, text.length()))
			);
			result.add(text.substring(start, end));
			start = end;
		}
		return result;
	}
}
