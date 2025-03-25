package baguni.infra.model.article;

import java.net.URL;
import java.util.UUID;

import org.hibernate.annotations.ColumnDefault;

import baguni.infra.model.common.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "link_v2")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class LinkJpaEntity extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Long id;

	@Column(name = "guid", nullable = false, columnDefinition = "BINARY(16)", unique = true)
	private UUID guid; // 대체 Hash URL

	@Column(name = "url", nullable = false, columnDefinition = "TEXT")
	private URL url; // 실제 url. base64 인코딩된 url이 들어오면 길이가 길어질 수 있어 TEXT 타입으로 변경

	@Column(name = "status", nullable = false)
	@Enumerated(EnumType.STRING)
	@ColumnDefault("'UNKNOWN'")
	private LinkStatus status; // 링크가 유효 한가?

	public enum LinkStatus {
		UNKNOWN,       // 미확인
		INACCESSIBLE,  // 접근 불가
		ACCESSIBLE     // 접근 가능
	}
}
