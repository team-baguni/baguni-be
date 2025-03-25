package baguni.domain.blog;

import java.net.URL;

import lombok.Getter;

@Getter
public class Blog {

	private final URL url;

	public Blog(URL url) {
		this.url = url;
	}
}
