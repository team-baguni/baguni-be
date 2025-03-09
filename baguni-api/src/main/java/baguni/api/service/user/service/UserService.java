package baguni.api.service.user.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import baguni.common.exception.base.ServiceException;
import baguni.infra.infrastructure.folder.FolderDataHandler;
import baguni.infra.infrastructure.user.UserDataHandler;
import baguni.infra.infrastructure.user.dto.UserInfo;
import baguni.infra.model.util.IDToken;
import baguni.security.exception.SecurityException;
import baguni.security.exception.AuthErrorCode;
import baguni.security.model.OAuth2UserInfo;
import baguni.common.dto.NamePassword;
import io.opentelemetry.instrumentation.annotations.WithSpan;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

	private final UserDataHandler userDataHandler;
	private final FolderDataHandler folderDataHandler;

	@WithSpan
	@Transactional
	public UserInfo createSocialUser(OAuth2UserInfo oAuthInfo) {
		var user = userDataHandler.createSocialUser(
			oAuthInfo.getProvider(),
			oAuthInfo.getProviderId(),
			oAuthInfo.getEmail()
		);
		try {
			folderDataHandler.createMandatoryFolder(user);
			return UserInfo.from(user);
		} catch (Exception e) {
			throw new SecurityException(AuthErrorCode.AUTH_SERVER_FAILURE);
		}
	}

	// TODO: remove later
	@Transactional
	public UserInfo createTestUser() {
		var user = userDataHandler.createTestUser();
		try {
			folderDataHandler.createMandatoryFolder(user);
			return UserInfo.from(user);
		} catch (Exception e) {
			throw new SecurityException(AuthErrorCode.AUTH_SERVER_FAILURE);
		}
	}

	@WithSpan
	@Transactional(readOnly = true)
	public boolean isSocialUserExists(OAuth2UserInfo oAuthInfo) {
		return userDataHandler.findSocialUser(oAuthInfo.getProvider(), oAuthInfo.getProviderId())
							  .isPresent();
	}

	@WithSpan
	@Transactional(readOnly = true)
	public UserInfo getUserInfoByToken(IDToken idToken) {
		var user = userDataHandler.getUser(idToken);
		return UserInfo.from(user);
	}

	@WithSpan
	@Transactional(readOnly = true)
	public UserInfo getUserInfoById(Long userId) {
		var user = userDataHandler.getUser(userId);
		return UserInfo.from(user);
	}

	@WithSpan
	@Transactional
	public void deleteUser(Long userId) {
		userDataHandler.deleteUser(userId);
	}

	@WithSpan
	@Transactional(readOnly = true)
	public UserInfo getTestUserInfoByNamePassword(NamePassword namePassword) {
		var user = userDataHandler.getTestUser(namePassword);
		return UserInfo.from(user);
	}

	@Transactional
	public UserInfo createTestUser(NamePassword namePassword) {
		var user = userDataHandler.createTestUser(namePassword);
		try {
			folderDataHandler.createMandatoryFolder(user);
			return UserInfo.from(user);
		} catch (Exception e) {
			throw new ServiceException(AuthErrorCode.AUTH_SERVER_FAILURE);
		}
	}
}
