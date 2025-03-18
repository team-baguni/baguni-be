package baguni.batch.domain.ai.ollama;

import org.springframework.stereotype.Component;

import baguni.batch.domain.ai.AiAgent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component("llama3.2-korean")
@RequiredArgsConstructor
public class OllamaAgent implements AiAgent {

	private final String MODEL = "llama3.2-korean";
	private final OllamaApi ollamaApi;

	@Override
	public String ask(String prompt) {
		log.info("length: {}, prompt: {}", prompt.length(), prompt);

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
