package baguni.infra.infrastructure.link;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.data.domain.Limit;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import baguni.common.exception.base.ServiceException;
import baguni.common.exception.error_code.LinkErrorCode;
import baguni.infra.infrastructure.link.dto.LinkCommand;
import baguni.infra.infrastructure.link.dto.LinkMapper;
import baguni.infra.infrastructure.link.dto.LinkResult;
import baguni.infra.model.link.Link;
import baguni.infra.model.link.LinkStats;
import io.opentelemetry.instrumentation.annotations.WithSpan;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class LinkDataHandler {

	private final LinkRepository linkRepository;
	private final LinkStatsRepository linkStatsRepository;
	private final LinkMapper linkMapper;

	@WithSpan
	@Transactional(readOnly = true)
	public LinkResult getLink(String url) {
		return linkRepository.findByUrl(url)
							 .map(linkMapper::toLinkResult)
							 .orElseThrow(() -> new ServiceException(LinkErrorCode.LINK_NOT_FOUND));
	}

	@Transactional(readOnly = true)
	public List<LinkResult> getLinksForSummary() {
		return linkRepository.findLinksWithOnlyContentAvailable().stream()
							 .map(linkMapper::toLinkResult)
							 .collect(Collectors.toList());
	}

	@Transactional(readOnly = true)
	public List<LinkResult> getLinksForCategories() {
		return linkRepository.findLinksWithContentAndSummaryAvailable().stream()
							 .map(linkMapper::toLinkResult)
							 .collect(Collectors.toList());
	}

	@WithSpan
	@Transactional(readOnly = true)
	public List<Link> getLinkList(List<String> urlList) {
		return linkRepository.findByUrlIn(urlList);
	}

	@WithSpan
	@Transactional(readOnly = true)
	public Optional<Link> getOptionalLink(String url) {
		return linkRepository.findByUrl(url);
	}

	@WithSpan
	@Transactional
	public Link saveLink(Link link) {
		return linkRepository.save(link);
	}

	@WithSpan
	@Transactional
	public List<LinkResult> updateLink(LinkCommand.UpdateImage command) {
		return linkRepository
			.findAllByImageUrl(command.imageUrl())
			.stream()
			.map(li -> li.updateImageUrl(command.imageUrl()))
			.map(linkMapper::toLinkResult)
			.toList();
	}

	@WithSpan
	@Transactional
	public LinkResult updateLink(LinkCommand.UpdateWithCrawledData command) {
		return linkRepository
			.findByUrl(command.linkUrl())
			.map(li -> li.updateMetadata(command.title(), command.description(), command.imageUrl()))
			.map(li -> li.updateContent(command.content()))
			.map(li -> li.updateSummary(null))     // reset for scheduler
			.map(li -> li.updateCategories(null))  // reset for scheduler
			.map(linkMapper::toLinkResult)
			.orElseThrow(() -> new ServiceException(LinkErrorCode.LINK_NOT_FOUND));
	}

	@WithSpan
	@Transactional
	public LinkResult updateLink(LinkCommand.UpdateSummary command) {
		return linkRepository
			.findByUrl(command.linkUrl())
			.map(li -> li.updateSummary(command.summary()))
			.map(linkMapper::toLinkResult)
			.orElseThrow(() -> new ServiceException(LinkErrorCode.LINK_NOT_FOUND));
	}

	@WithSpan
	@Transactional
	public LinkResult updateLink(LinkCommand.UpdateCategories command) {
		return linkRepository
			.findByUrl(command.linkUrl())
			.map(li -> li.updateCategories(command.categories()))
			.map(linkMapper::toLinkResult)
			.orElseThrow(() -> new ServiceException(LinkErrorCode.LINK_NOT_FOUND));
	}

	@WithSpan
	@Transactional(readOnly = true)
	public boolean existsByUrl(String url) {
		return linkRepository.existsByUrl(url);
	}

	@WithSpan
	@Transactional(readOnly = true)
	public List<Link> getRssLinkList(int limit) {
		return linkRepository.findAllRssBlogArticlesOrderByPublishedDate(
			PageRequest.of(0, limit)
		);
	}

	@WithSpan
	@Transactional(readOnly = true)
	public Optional<LinkStats> findLinkStats(LocalDate date, String url) {
		return linkStatsRepository.findByDateAndUrl(date, url);
	}

	@WithSpan
	@Transactional
	public LinkStats saveLinkStats(LinkStats linkStats) {
		return linkStatsRepository.save(linkStats);
	}

	@WithSpan
	@Transactional(readOnly = true)
	public List<LinkStats> getViewRank(LocalDate startDate, LocalDate endDate, Integer limit) {
		return linkStatsRepository.findByDateBetweenAndViewCountGreaterThanOrderByViewCountDesc(
			startDate, endDate, 0, Limit.of(limit)
		);
	}

	@WithSpan
	@Transactional(readOnly = true)
	public List<LinkStats> getBookmarkedRank(LocalDate startDate, LocalDate endDate, Integer limit) {
		return linkStatsRepository.findByDateBetweenAndBookmarkedCountGreaterThanOrderByBookmarkedCountDesc(
			startDate, endDate, 0, Limit.of(limit)
		);
	}
}
