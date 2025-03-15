package baguni.batch.domain.crawler;

import java.net.MalformedURLException;
import java.net.URL;

import org.springframework.stereotype.Component;

import baguni.common.exception.base.ServiceException;

import baguni.common.lib.opengraph.Metadata;
import baguni.common.lib.opengraph.CrawlResult;
import baguni.common.lib.opengraph.SeleniumCrawler;
import baguni.common.lib.opengraph.SeleniumException;
import baguni.common.exception.error_code.LinkErrorCode;
import io.opentelemetry.instrumentation.annotations.WithSpan;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 *    @author sangwon
 * 	링크 분석 클래스 (크롤링, 링크 유효성 검사)
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class LinkCrawler {

	private final SeleniumCrawler seleniumCrawler;

	@WithSpan
	public LinkCrawlResult crawl(String url) {
		try {
			var crawlResult = new CrawlResult(url, seleniumCrawler);

			// 한컴 테크 블로그의 Meta Title이 "한컴테크"로 고정되어 있어서 <title> 우선적으로 적용
			var title = crawlResult.getTag(Metadata.TITLE)
								   .orElse(crawlResult.getTag(Metadata.OG_TITLE)
													  .orElse(""));

			var description = crawlResult.getTag(Metadata.DESCRIPTION)
										 .orElse(crawlResult.getTag(Metadata.OG_DESCRIPTION)
															.orElse(""));

			var imageUrl = correctImageUrl(url, crawlResult.getTag(Metadata.OG_IMAGE)
														   .orElse(crawlResult.getTag(Metadata.IMAGE)
																			  .orElse(crawlResult.getTag(Metadata.ICON)
																								 .orElse(""))));
			var content = crawlResult.getTag(Metadata.CONTENT)
									 .orElse("");

			if (title.isBlank() || description.isBlank() || imageUrl.isBlank() || content.isBlank()) {
				throw new ServiceException(LinkErrorCode.LINK_CRAWLING_FAILURE, "필수 필드 획득 실패");
			}

			return LinkCrawlResult
				.builder()
				.title(title)
				.description(description)
				.content(content)
				.imageUrl(imageUrl)
				.build();

		} catch (SeleniumException e) {
			log.error(e.getMessage(), e);
			throw new ServiceException(LinkErrorCode.LINK_CRAWLING_FAILURE, "셀레니움 에러");
		}
	}

	/**
	 * 	og:image 가 완전한 url 형식이 아닐 수 있어 보정
	 * 	추론 불가능한 image url 일 경우 빈스트링("")으로 대치
	 *
	 *  @author sangwon
	 * 	protocol : https
	 * 	host : blog.dongolab.com
	 */
	private String correctImageUrl(String baseUrl, String imageUrl) {
		// "null"이 넘어오는 경우가 있음.
		// favicon 가져올 때 <link href=> -> "null"로 넘어옴
		if (imageUrl == null || imageUrl.trim().isEmpty() || imageUrl.equals("null")) {
			return "";
		}

		if (imageUrl.startsWith("://")) {
			return "https" + imageUrl;
		}
		if (imageUrl.startsWith("//")) {
			return "https:" + imageUrl;
		}

		try {
			URL url = new URL(baseUrl);
			// ex) https://blog.dongolab.com
			String domain = url.getProtocol() + "://" + url.getHost();

			// ex) /og-image.png -> https://blog.dongholab.com/og-image.png
			if (imageUrl.startsWith("/")) {
				return domain + imageUrl;
			}

			if (!imageUrl.startsWith("http://") && !imageUrl.startsWith("https://")) {
				return domain + "/" + imageUrl;
			}

			return imageUrl;
		} catch (MalformedURLException e) {
			// baseUrl이 올바르지 않은 경우 빈 문자열 반환
			return "";
		}
	}
}
