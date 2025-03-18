package baguni.batch.config;

import java.time.Duration;

import org.springframework.boot.web.client.ClientHttpRequestFactories;
import org.springframework.boot.web.client.ClientHttpRequestFactorySettings;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.support.RestClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

import baguni.batch.domain.ai.ollama.OllamaApi;

@Configuration
public class OllamaApiConfig {

	private static final Duration CONNECTION_TIMEOUT = Duration.ofMinutes(1);
	private static final Duration READ_TIMEOUT = Duration.ofMinutes(2);
	private static final String OLLAMA_SERVER_URL = "http://ollama:11434";
	private static final String OLLAMA_LOCAL_URL = "http://localhost:11434"; // local에서 docker로 띄운 llama 사용 시 필요

	@Bean
	public OllamaApi ollamaApi(RestClient.Builder restClientBuilder) {
		var restClient = restClientBuilder.baseUrl(OLLAMA_SERVER_URL)
										  .defaultHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
										  .defaultHeader("Accept", MediaType.APPLICATION_JSON_VALUE)
										  .requestFactory(clientHttpRequestFactory())
										  .build();
		var adapter = RestClientAdapter.create(restClient);
		var proxy = HttpServiceProxyFactory.builderFor(adapter).build();
		return proxy.createClient(OllamaApi.class);
	}

	public ClientHttpRequestFactory clientHttpRequestFactory() {
		ClientHttpRequestFactorySettings settings = ClientHttpRequestFactorySettings.DEFAULTS
			.withConnectTimeout(CONNECTION_TIMEOUT)
			.withReadTimeout(READ_TIMEOUT);
		return ClientHttpRequestFactories.get(settings);
	}
}
