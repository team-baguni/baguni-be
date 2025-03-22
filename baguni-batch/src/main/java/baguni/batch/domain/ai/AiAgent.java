package baguni.batch.domain.ai;

import baguni.batch.domain.analyzer.TextAnalyzable;

/*
 *  Ai Agent 구현체들에게 강제할 메서드를
 *  아래에 추가해주세요.
 */
public abstract class AiAgent implements TextAnalyzable {

	protected String name;

	public AiAgent(String name) {
		this.name = name;
	}
}
