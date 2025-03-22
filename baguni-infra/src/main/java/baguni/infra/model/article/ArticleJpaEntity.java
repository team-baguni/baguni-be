package baguni.infra.model.article;

import java.util.Set;

import baguni.infra.model.common.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
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
	@JoinColumn(name = "link_id", unique = true)
	private LinkJpaEntity link;

	@ManyToOne
	@JoinTable(name = "article_category",
		joinColumns = @JoinColumn(name = "article_id"),
		inverseJoinColumns = @JoinColumn(name = "catetory_id")
	)
	private CategoryJpaEntity category;

	@ManyToMany
	@JoinTable(name = "article_keyword",
		joinColumns = @JoinColumn(name = "article_id"),
		inverseJoinColumns = @JoinColumn(name = "keyword_id")
	)
	private Set<KeywordJpaEntity> keywords;

	@OneToOne
	@JoinColumn(name = "detail_id", unique = true)
	private ArticleDetailJpaEntity detail;
}
