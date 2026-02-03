package base;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.BrowserType;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import utils.ConfigManager;
import utils.ReportManager;

import java.util.List;

/**
 * =====================================================================
 * PlaywrightFactory
 * =====================================================================
 *
 * Central factory responsible for:
 *  - Playwright lifecycle management
 *  - Browser launch configuration
 *  - BrowserContext creation
 *  - Page creation
 *
 * Ensures:
 *  - Single Playwright + Browser instance per test run
 *  - Config-driven behavior
 *  - Clean startup and shutdown
 * =====================================================================
 */
public final class PlaywrightFactory {

    private static final Logger log =
            LoggerFactory.getLogger(PlaywrightFactory.class);

    private static Playwright playwright;
    private static Browser browser;

    private static boolean maximize;
    private static boolean headless;
    private static BrowserTypeEnum browserTypeEnum;

    private PlaywrightFactory() {
        // Prevent instantiation
    }

    // ------------------------------------------------------------------
    // Lifecycle
    // ------------------------------------------------------------------

    /**
     * Initializes Playwright and launches the configured browser.
     */
    public static synchronized void start() {

        if (playwright != null) {
            return;
        }

        playwright = Playwright.create();

        browserTypeEnum = BrowserTypeEnum.fromConfig();
        headless = ConfigManager.getBoolean("headless");
        maximize = ConfigManager.getBoolean("maximize.window");

        BrowserType browserType = resolveBrowserType(browserTypeEnum);
        BrowserType.LaunchOptions launchOptions =
                buildLaunchOptions(browserTypeEnum, headless, maximize);

        logInfo(
                "Launching browser: %s | Headless: %s | Maximize: %s",
                browserTypeEnum.getBrowserName(),
                headless,
                maximize
        );

        browser = browserType.launch(launchOptions);
    }

    /**
     * Returns the active Browser instance.
     */
    public static Browser getBrowser() {
        if (browser == null) {
            start();
        }
        return browser;
    }

    /**
     * Creates a new BrowserContext for a test.
     * Each test should have its own context.
     */
    public static BrowserContext getContext() {

        Browser.NewContextOptions options =
                new Browser.NewContextOptions();

        /*
         * IMPORTANT:
         * - Chromium supports true maximize via args
         * - Firefox/WebKit require explicit viewport
         * - In CI/headless, Toolkit-based screen size is unsafe
         */
        if (maximize && !isChromium(browserTypeEnum)) {
            options.setViewportSize(1920, 1080);
            logInfo("Viewport set to 1920x1080 (non-Chromium browser)");
        } else if (!maximize) {
            logInfo("Using default viewport (maximize disabled)");
        }

        return getBrowser().newContext(options);
    }

    /**
     * Creates a new Page in a fresh BrowserContext.
     */
    public static Page newPage() {
        return getContext().newPage();
    }

    /**
     * Stops Browser and Playwright.
     * Called once after test execution.
     */
    public static synchronized void stop() {

        try {
            if (browser != null) {
                browser.close();
            }
            if (playwright != null) {
                playwright.close();
            }
        } finally {
            browser = null;
            playwright = null;
            logInfo("Playwright shutdown completed");
        }
    }

    // ------------------------------------------------------------------
    // Internal helpers
    // ------------------------------------------------------------------

    /**
     * Resolves Playwright BrowserType from enum.
     */
    private static BrowserType resolveBrowserType(
            BrowserTypeEnum typeEnum) {

        switch (typeEnum) {
            case FIREFOX:
                return playwright.firefox();
            case WEBKIT:
                return playwright.webkit();
            case CHROME:
            case MSEDGE:
                return playwright.chromium();
            default:
                throw new IllegalStateException(
                        "Unsupported browser: " + typeEnum
                );
        }
    }

    /**
     * Builds browser launch options.
     */
    private static BrowserType.LaunchOptions buildLaunchOptions(
            BrowserTypeEnum typeEnum,
            boolean headless,
            boolean maximize) {

        BrowserType.LaunchOptions options =
                new BrowserType.LaunchOptions()
                        .setHeadless(headless);

        // Channel selection
        if (typeEnum == BrowserTypeEnum.CHROME) {
            options.setChannel("chrome");
        } else if (typeEnum == BrowserTypeEnum.MSEDGE) {
            options.setChannel("msedge");
        }

        // True maximize only supported by Chromium
        if (maximize && isChromium(typeEnum)) {
            options.setArgs(List.of("--start-maximized"));
        }

        return options;
    }

    /**
     * Checks if browser is Chromium-based.
     */
    private static boolean isChromium(BrowserTypeEnum typeEnum) {
        return typeEnum == BrowserTypeEnum.CHROME
                || typeEnum == BrowserTypeEnum.MSEDGE;
    }

    /**
     * Unified logging to console + Extent (if available).
     */
    private static void logInfo(String message, Object... args) {
        log.info(message, args);
        try {
            ReportManager.stepInfo(String.format(message, args));
        } catch (Exception ignored) {
            // ReportManager may not be initialized yet
        }
    }
}
