package baguni.common.lib.opengraph;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;
import java.util.Optional;

/**
 * @author minkyeu kim
 * OpenGraph Metadata Utility Class
 */
public class CrawlResult {

	private final Map<String, String> htmlTags;

	public CrawlResult(String uri, SeleniumCrawler seleniumCrawler) throws SeleniumException {
		try {
			var parsedUri = new URI(uri);
			this.htmlTags = seleniumCrawler.crawl(parsedUri);
		} catch (URISyntaxException e) {
			throw new SeleniumException("Invalid URI: " + uri, e);
		}
	}

	public Optional<String> getTag(Metadata.MetadataTag metadataTag) {
		var key = metadataTag.key();
		if (htmlTags.containsKey(key)) {
			return Optional.of(htmlTags.get(key));
		}
		return Optional.empty();
	}
}
