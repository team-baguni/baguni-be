package baguni.common.lib.opengraph;

import java.time.Duration;

import lombok.Getter;

@Getter
public class SeleniumOption {
	private final String userAgent;
	private final Duration httpRequestTimeoutyDuration;
	private final String httpResponseDefaultCharsetName;

	public SeleniumOption() {
		httpRequestTimeoutyDuration = Duration.ofSeconds(5 * 60);
		userAgent = "facebookexternalhit/1.1";
		httpResponseDefaultCharsetName = "UTF-8";
	}
}
