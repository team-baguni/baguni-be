package baguni.common.lib.opengraph;

public class SeleniumException extends Exception {
	public SeleniumException(String message) {
		super(message);
	}

	public SeleniumException(String message, Exception exception) {
		super(message, exception);
	}
}
