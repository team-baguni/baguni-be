package baguni.common.event;

import com.fasterxml.jackson.annotation.JsonCreator;

import lombok.Getter;

/**
 * Link image_url 유효한지 확인하기 위한 Event
 * 추후, url에 접근하여 상태 코드를 판단하는 경우까지 고려해 LinkUpdateEvent로 정의
 */
@Getter
public class LinkCheckEvent extends Event {

	private final String url;

	@JsonCreator
	public LinkCheckEvent(String url) {
		super(new Topic("link.check"));
		this.url = url;
	}
}
