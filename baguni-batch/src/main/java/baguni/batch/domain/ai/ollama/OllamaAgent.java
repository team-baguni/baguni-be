package baguni.batch.domain.ai.ollama;

import org.springframework.stereotype.Component;

import baguni.batch.domain.ai.AiAgent;
import lombok.RequiredArgsConstructor;

@Component("llama3.2-korean")
@RequiredArgsConstructor
public class OllamaAgent implements AiAgent {

	private final String MODEL = "llama3.2-korean";
	private final OllamaApi ollamaApi;

	@Override
	public String ask(String prompt) {
		return ollamaApi
			.sendRequest(OllamaRequest
				.builder()
				.model(MODEL)
				.stream(false)
				.prompt(prompt)
				.build()
			).response();
	}
}
