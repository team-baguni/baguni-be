package baguni.infra.model.article;

import java.time.LocalDateTime;

import baguni.infra.model.common.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Table(name = "article")
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ArticleJpaEntity extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Long id;

	@OneToOne
	@JoinColumn(name = "link_id", unique = true)
	private LinkJpaEntity link;

	@OneToOne
	@JoinColumn(name = "information_id", unique = true)
	private ArticleInformationJpaEntity information;

	@Column(name = "published_at", columnDefinition = "TIMESTAMP")
	private LocalDateTime publishedAt;
}
