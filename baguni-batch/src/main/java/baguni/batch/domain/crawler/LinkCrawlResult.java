package baguni.batch.domain.crawler;

import lombok.Builder;

@Builder
public record LinkCrawlResult(
	String imageUrl,
	String title,
	String description,
	String content, // 본문 크롤링한 데이터
	String publishedAt
	// Link Analyzer 가 고도화 됨에 따라 정보가 추가될 예정 입니다.
) {
}
