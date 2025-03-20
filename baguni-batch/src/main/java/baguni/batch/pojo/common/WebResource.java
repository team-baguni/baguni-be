package baguni.batch.pojo.common;

import java.net.URL;

import lombok.Getter;

@Getter
public abstract class WebResource {

	private final URL url;

	public boolean isAccessible(/* WebAgent agent */) {
		// ex. agent.visit(url) ➜ 4XX or readTimeout 일 경우 false 반환
		throw new UnsupportedOperationException("구현 예정");
	}

	public WebResource(URL url) {
		this.url = url;
	}
}
