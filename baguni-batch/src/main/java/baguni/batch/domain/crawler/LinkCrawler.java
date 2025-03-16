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

			// 토스 글 중에 description에는 공백이 들어 있으므로, 더 긴 description 사용
			// 참고 : https://toss.tech/article/32197
			var desc = crawlResult.getTag(Metadata.DESCRIPTION).orElse("");
			var ogDesc = crawlResult.getTag(Metadata.OG_DESCRIPTION).orElse("");
			var description = (desc.length() >= ogDesc.length()) ? desc : ogDesc;

			var imageUrl = correctImageUrl(url, crawlResult.getTag(Metadata.OG_IMAGE)
														   .orElse(crawlResult.getTag(Metadata.IMAGE)
																			  .orElse(crawlResult.getTag(Metadata.ICON)
																								 .orElse(""))));

			// 토스 글 중에 p 태그가 없는 경우가 있어 content에 공백이 설정됨. -> description에 유효한 데이터가 있으므로 공백인 경우 사용
			// 참고 : https://toss.tech/article/32197
			var content = crawlResult.getTag(Metadata.CONTENT)
									 .filter(c -> !c.isBlank())
									 .orElse(description);

			if (title.isBlank() || imageUrl.isBlank()) {
				throw new ServiceException(LinkErrorCode.LINK_CRAWLING_FAILURE, "필수 필드 획득 실패, url : " + url);
			}

			if (content.length() > 65535) {
				log.error("text 필드보다 큰 content 길이 : {}, url : {}", content.length(), url);
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
			throw new ServiceException(LinkErrorCode.LINK_CRAWLING_FAILURE, "셀레니움 에러, url : " + url);
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
