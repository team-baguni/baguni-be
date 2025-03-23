package baguni.batch.domain.ai.ollama.dto;

import java.util.List;

public record OllamaGetKeywordsResult(
	List<String> keywords
) {
}
