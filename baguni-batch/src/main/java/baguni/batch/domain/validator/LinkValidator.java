package baguni.batch.domain.validator;

import java.net.URI;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.ResourceAccessException;

import baguni.batch.lib.VisitWebsiteApi;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class LinkValidator {

	private final VisitWebsiteApi webApi;

	public boolean isAccessible(String url) {
		try {
			webApi.checkUrl(URI.create(url));
		} catch (HttpStatusCodeException e) { // 4xx, 5xx 응답 시 발생하는 예외
			var statusCode = e.getStatusCode();
			if (statusCode == HttpStatus.UNAUTHORIZED || statusCode == HttpStatus.FORBIDDEN) {
				log.error("[에러x 확인 전용] 401 또는 403 응답 : {}", statusCode); // 401, 403 응답하는 상황 확인을 위한 로그
				return true; // 401, 403의 경우 유효하다고 판단
			}
			return false;
		} catch (ResourceAccessException e) {
			log.info("url 타임 아웃 발생, url : {}", url, e);
		} catch (Exception e) {
			log.info("url 예외 발생: {},", url, e);
		}
		return true;
	}
}
