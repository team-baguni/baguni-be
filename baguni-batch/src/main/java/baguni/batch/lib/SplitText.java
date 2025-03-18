package baguni.batch.lib;

import java.util.ArrayList;
import java.util.List;

import lombok.extern.slf4j.Slf4j;

@Slf4j
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
		List<String> result = new ArrayList<>();
		int length = text.length();
		int start = 0;
		int count = 0;

		while (start < length && count < limit) {
			// 1) 이번에 자를 최대 코드 포인트 수
			int charCount = Math.min(size, text.codePointCount(start, length));
			// 2) 실제 end 인덱스
			int end = text.offsetByCodePoints(start, charCount);

			// 3) 우선 잘라보기
			String chunk = text.substring(start, end);

			// 4) 다음 문자가 공백이 아니라면, 공백 전 지점으로 backtrack
			if (end < length && !Character.isWhitespace(text.charAt(end))) {
				int lastSpace = chunk.lastIndexOf(' ');
				if (lastSpace >= 0) {
					chunk = chunk.substring(0, lastSpace);
					end = start + chunk.length();
				}
			}

			result.add(chunk);
			start = end;  // 다음 덩어리 시작점
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
