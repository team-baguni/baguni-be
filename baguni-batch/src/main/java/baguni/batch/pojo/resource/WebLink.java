package baguni.batch.pojo.resource;

import java.net.URL;
import java.util.UUID;

import lombok.Getter;

@Getter
public abstract class WebLink {

	private final URL url;

	private final UUID hash;

	public WebLink(URL url, UUID hash) {
		this.url = url;
		this.hash = hash;
	}
}
