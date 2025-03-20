package baguni.batch.pojo.article.webArticle;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Category {

	DEVELOPMENT("개발"),
	DESIGN("디자인"),
	MUSIC("음악"),
	MARKETING("마케팅"),
	;

	private final String name;
}
