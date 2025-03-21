package baguni.infra.model.article;

import java.util.UUID;

import org.hibernate.annotations.ColumnDefault;

import baguni.infra.model.common.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "guid_url")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class GuidUrlJpaEntity extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Long id;

	@Column(name = "guid", nullable = false, columnDefinition = "BINARY(16)", unique = true)
	private UUID guid; // 대체 URL

	@Column(name = "url", nullable = false, columnDefinition = "VARCHAR(2048)", unique = true)
	private String url; // 실제 url

	@Column(name = "is_accessible", nullable = false)
	@ColumnDefault("true")
	private Boolean isAccessible; // 링크가 유효 한가?
}
