package baguni.batch.config;

import java.util.List;

import org.springframework.boot.autoconfigure.http.HttpMessageConverters;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;

@Configuration
public class MessageConverterConfig {

	@Bean
	public XmlMapper xmlMapper() {
		XmlMapper xmlMapper = new XmlMapper();
		xmlMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false); // 명시되지 않은 속성 무시
		return xmlMapper;
	}

	/**
	 * json으로 content-type, accept를 설정해도 `ollamaApi.sendRequest()`가 body를 Xml로 내보내서
	 * 뭐가 강제로 설정하고 있는건가...? 왜 안되는지 이유를 모르던 찰나에
	 * 혹시 xmlMapper bean이 기본 설정으로 먼저 적용되서 그런건가 싶어서 아래 추가해봤는데
	 * 바로 변환 됨.... 이거 아직 이유 잘 파악 안되서 주석으로 남김...
	 */
	@Bean
	public HttpMessageConverters httpMessageConverters() {
		return new HttpMessageConverters(false, List.of(new MappingJackson2HttpMessageConverter()));
	}
}
