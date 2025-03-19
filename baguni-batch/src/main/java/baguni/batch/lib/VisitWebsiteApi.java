package baguni.batch.lib;

import java.net.URI;

import org.springframework.web.service.annotation.GetExchange;

import baguni.batch.config.VisitWebsiteApiConfig;

/**
 * 타 웹사이트에 대한 정보 획득용 Http Interface
 * {@link VisitWebsiteApiConfig} 를 참고.
 */
public interface VisitWebsiteApi {

	/**
	 * 대상 사이트의 Rss, Atom Feed를 획득
	 * String 형태로 xml을 받아 Rss, Atom 타입 구분
	 */
	@GetExchange
	String getFeed(URI uri);

	/**
	 * @param uri 이미지 url
	 * image_url에 접근하여 timeout 발생 여부를 판단하기 위함.
	 * 추후, url에 접근하여 상태 코드를 판단하는 경우까지 고려해 Link로 정의
	 */
	@GetExchange
	void checkUrl(URI uri);
}
