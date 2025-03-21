package baguni.infra.model.article;

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

	@Column(name = "title", columnDefinition = "TEXT")
	private String title;

	@Column(name = "image_url", columnDefinition = "TEXT")
	private String imageUrl;

	@OneToOne
	@JoinColumn(name = "guid_url_id", unique = true)
	private GuidUrlJpaEntity guidUrl;

	@OneToOne
	@JoinColumn(name = "detail_id", unique = true)
	private ArticleDetailJpaEntity detail;
}
