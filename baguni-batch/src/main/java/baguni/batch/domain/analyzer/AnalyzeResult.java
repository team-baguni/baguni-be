package baguni.batch.domain.analyzer;

import java.util.List;

import lombok.Builder;

@Builder
public record AnalyzeResult(
	String summary,
	String category,
	List<String> keywords
) {
}
