package baguni.infra.infrastructure.link;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import baguni.infra.model.link.Link;

public interface LinkRepository extends JpaRepository<Link, Long> {

	Optional<Link> findByUrl(String url);

	List<Link> findAllByImageUrl(String imageUrl);

	List<Link> findByUrlIn(List<String> urlList);

	boolean existsByUrl(String url);

	/**
	 * @deprecated
	 */
	@Query("""
			SELECT li FROM Link li
				WHERE li.isRss=true
				AND li.imageUrl IS NOT NULL
				ORDER BY li.publishedAt DESC
		""")
	List<Link> findAllRssBlogArticlesOrderByPublishedDate(Pageable pageable);

	/**
	 * @deprecated
	 */
	@Query("""
			SELECT li FROM Link li
				WHERE li.isRss=true
		""")
	List<Link> getAllFeedLinks();

	/**
	 * @deprecated
	 */
	@Query("""
			SELECT li FROM Link li
				WHERE li.content IS NOT NULL
				AND li.summary IS NULL
				AND li.categories IS NULL
		""")
	List<Link> findLinksWithOnlyContentAvailable();

	/**
	 * @deprecated
	 */
	@Query("""
			SELECT li FROM Link li
				WHERE li.content IS NOT NULL
				AND li.summary IS NOT NULL
				AND li.categories IS NULL
		""")
	List<Link> findLinksWithContentAndSummaryAvailable();
}
