package baguni.batch.domain.link.util;

import java.net.URI;

import org.springframework.http.ResponseEntity;
import org.springframework.web.service.annotation.GetExchange;

/**
 * @author sangwon
 * image_url에 접근하여 timeout 발생 여부를 판단하기 위함.
 * 추후, url에 접근하여 상태 코드를 판단하는 경우까지 고려해 Link로 정의
 */
public interface LinkApi {

	@GetExchange
	ResponseEntity<Void> checkImageUrl(URI uri);
}
