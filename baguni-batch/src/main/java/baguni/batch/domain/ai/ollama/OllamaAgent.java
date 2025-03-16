package baguni.batch.domain.ai.ollama;

import org.springframework.stereotype.Component;

import baguni.batch.domain.ai.AiAgent;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class OllamaAgent implements AiAgent {

	private final String MODEL = "llama3.2-korean";
	private final OllamaApi ollamaApi;

	@Override
	public String ask(String prompt) {
		return ollamaApi
			.sendRequest(LlamaRequest
				.builder()
				.model(MODEL)
				.stream(false)
				.prompt(prompt)
				.build()
			).response();
	}
}
