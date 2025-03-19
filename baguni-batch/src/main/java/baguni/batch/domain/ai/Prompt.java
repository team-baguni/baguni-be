package baguni.batch.domain.ai;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class Prompt {
	private final String prompt;

	public int length() {
		return prompt.length();
	}

	@Override
	public String toString() {
		return prompt;
	}
}
