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
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "article_detail")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ArticleDetailJpaEntity extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Long id;

	@Column(name = "content", columnDefinition = "TEXT")
	private String content;

	@Column(name = "summary", columnDefinition = "TEXT")
	private String summary;

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
}
