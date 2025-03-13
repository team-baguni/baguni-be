package baguni.api.application.development;

import java.time.LocalDateTime;

import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import baguni.api.application.user.controller.dto.UserApiMapper;
import baguni.api.application.user.controller.dto.UserInfoApiResponse;
import baguni.api.service.user.service.UserService;
import baguni.common.event.EventMessenger;
import baguni.common.event.LinkReadEvent;
import baguni.infra.infrastructure.link.LinkRepository;
import baguni.security.config.JwtProperties;
import baguni.security.config.SecurityProperties;
import baguni.security.util.AccessToken;
import baguni.security.util.CookieUtil;
import baguni.common.dto.NamePassword;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

/**
 * 개발 전용 API를 제공하는 Controller입니다.
 * 운영에는 반영되지 않습니다.
 */
@RestController
@RequiredArgsConstructor
@Profile({"local", "dev", "staging"})
@RequestMapping("/api/development")
@Tag(
	name = "개발/테스트 전용 API",
	description = """
		1. 인증 없이 사용 가능합니다.
		2. 운영 환경에선 해당 API가 비활성화됩니다.
		""")
public class DevelopmentController {

	private final CookieUtil cookieUtil;
	private final UserService userService;
	private final UserApiMapper userApiMapper;
	private final SecurityProperties securityProps;
	private final JwtProperties jwtProps;
	// ----------- 개발 전용임으로, 기존 레이어 규칙에 관계 없이 사용 -------0
	private final LinkRepository linkRepository;
	private final EventMessenger eventMessenger;

	@PostMapping("/users/new/signup")
	@Operation(summary = "테스트 회원 가입 (name + password)", description = "테스트용 회원을 생성합니다.")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "204", description = "테스트 계정 생성 성공")
	})
	public ResponseEntity<UserInfoApiResponse> createTestUserIdPassword(
		@Valid @RequestBody NamePassword namePassword
	) {
		var userInfo = userService.createTestUser(namePassword);
		var response = userApiMapper.toApiResponse(userInfo);
		return ResponseEntity.ok(response);
	}

	@PostMapping("/users/new/login")
	@Operation(summary = "테스트 회원 로그인 (name + password)", description = "테스트용 회원 로그인")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "204", description = "테스트 계정 로그인 성공")
	})
	public void loginTestUserIdPassword(
		@Valid @RequestBody NamePassword namePassword,
		HttpServletResponse response
	) {
		var userInfo = userService.getTestUserInfoByNamePassword(namePassword);
		cookieUtil.deleteCookie(response, securityProps.ACCESS_TOKEN_KEY);
		cookieUtil.addCookie(
			response, securityProps.ACCESS_TOKEN_KEY,
			AccessToken.makeNew(jwtProps, userInfo.idToken(), userInfo.role()),
			(int)AccessToken.EXPIRY_DAY.toSeconds(), true
		);
	}

	@DeleteMapping("/users")
	@Operation(summary = "회원 탈퇴", description = "회원 탈퇴를 하면 모든 폴더, 픽, 태그가 삭제됩니다.")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "204", description = "회원 탈퇴 성공")
	})
	public ResponseEntity<Void> deleteUser(
		@Valid @RequestBody Long userId,
		HttpServletResponse response
	) {
		userService.deleteUser(userId);
		cookieUtil.clearCookies(response);
		return ResponseEntity.noContent().build();
	}

	@PostMapping
	@Operation(summary = "링크 분석 배치 시작", description = """
		링크 분석 배치를 작동시킵니다.
		현재는 Feed 글만 분석하며, 모든 과정이 동기적으로 처리됩니다.
		분석 기능이 완료되면 해당 메서드는 삭제할 예정입니다.
		""")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "204", description = "시작 성공")
	})
	@Transactional
	public ResponseEntity<Void> runLinkAnalyzeBatch() {
		for (var link : linkRepository.getAllFeedLinks()) {
			link.changeUpdatedAt(LocalDateTime.now().minusDays(100));
			eventMessenger.send(new LinkReadEvent(link.getUrl()));
		}
		return ResponseEntity.noContent().build();
	}
}
