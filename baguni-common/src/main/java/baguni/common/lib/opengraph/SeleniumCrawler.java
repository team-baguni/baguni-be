package baguni.common.lib.opengraph;

import java.net.URI;
import java.net.URISyntaxException;
import java.time.Duration;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.stereotype.Component;

import io.github.bonigarcia.wdm.WebDriverManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * @author sangwon
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class SeleniumCrawler {

	private final OpenGraphOption openGraphOption;

	public String getContent(String uri) throws OpenGraphException {
		StringBuilder content = new StringBuilder();
		// URL 검사
		assertUri(uri);

		// 1. WebDriver 설정
		// WebDriverManager 사용 시, 별도의 드라이버 설치 및 경로 지정 없이 자동 세팅
		WebDriverManager.chromedriver().setup();

		// 2. 크롬 옵션 설정
		WebDriver driver = chromeOptionSetting();

		try {
			// 페이지 로딩 타임아웃 설정
			driver.manage().timeouts().pageLoadTimeout(openGraphOption.getHttpRequestTimeoutyDuration());

			// 3. 페이지 로딩
			driver.get(uri);

			WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
			wait.until(webDriver -> ((JavascriptExecutor)driver)
				.executeScript("return document.readyState").equals("complete")); // 초기 로딩 완료될 때까지 대기
			Thread.sleep(300);

			// 본문 데이터 크롤링 (3회 재시도)
			int retryCount = 0;
			int maxRetries = 3;
			while (true) {
				try {
					content = new StringBuilder(); // 본문
					getContent(driver, content);
					break;
				} catch (StaleElementReferenceException e) {
					if (++retryCount >= maxRetries) {
						throw e;
					}
				}
			}
		} catch (org.openqa.selenium.TimeoutException e) {
			log.error("getDescription -> Selenium TimeoutException 발생 : {}, url : {}", e.getMessage(), uri);
		} catch (Exception e) {
			throw new OpenGraphException("Error occurred when reading OG tags via Selenium, url : " + uri, e);
		} finally {
			// 5. 리소스 해제
			driver.quit();
		}
		return content.toString();
	}

	private void assertUri(String uri) throws OpenGraphException {
		try {
			new URI(uri);
		} catch (URISyntaxException e) {
			throw new OpenGraphException("Invalid URI: " + uri, e);
		}
	}

	// TODO: <ul><li> 태그도 필요하면 추가
	private void getContent(WebDriver driver, StringBuilder description) {
		List<WebElement> pTagList = driver.findElements(By.tagName("p"));
		for (WebElement pTag : pTagList) {
			description.append(pTag.getText()).append(" ");
		}
	}

	private WebDriver chromeOptionSetting() {
		ChromeOptions options = new ChromeOptions();
		options.addArguments("--headless=new"); // 브라우저 UI 없이 백그라운드로 동작
		options.addArguments("--disable-gpu"); // gpu 비활성화
		options.addArguments("--user-agent=" + openGraphOption.getUserAgent());
		options.addArguments("--no-sandbox");
		options.addArguments("--start-maximized");
		options.addArguments("--disable-popup-blocking"); // 팝업 안뜨게
		options.addArguments("--remote-allow-origins=*"); // 모든 출처에서의 연결을 허용, 자동화된 테스트나 CORS 제한을 우회할 때 유용
		options.addArguments("--disable-dev-shm-usage"); // Chrome이 /dev/shm 대신 /tmp 디렉토리를 사용, /tmp는 일반적인 파일 시스템으로, 크기 제한이 없어 메모리 부족

		// WebDriver 생성
		return new ChromeDriver(options);
	}
}
