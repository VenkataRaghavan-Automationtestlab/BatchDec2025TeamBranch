package base;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.Page;
import org.testng.ITestResult;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import utils.PageActions;
import utils.ReportManager;

/**
 * =====================================================================
 * BaseTest
 * =====================================================================
 *
 * Enterprise base class for all TestNG test classes.
 *
 * Responsibilities:
 *  - Initialize and shut down Playwright
 *  - Create browser context and page per test
 *  - Initialize ExtentReports test nodes
 *  - Handle pass/fail/skip reporting
 *  - Provide Page Object factory method
 *
 * All test classes MUST extend this class.
 * =====================================================================
 */
public class BaseTest {

    /** Playwright browser instance (shared per test class). */
    protected Browser browser;

    /** Browser context (created fresh for each test). */
    protected BrowserContext context;

    /** Playwright page instance (one per test). */
    protected Page page;

    /** Wrapper for Playwright actions with logging and screenshots. */
    protected PageActions actions;

    /** Stores the current test method name for reporting and logging. */
    protected String currentTestName;

    // ------------------------------------------------------------------
    // Test lifecycle
    // ------------------------------------------------------------------

    /**
     * Runs once before any test methods in the class.
     * Initializes reporting and Playwright.
     */
    @BeforeClass(alwaysRun = true)
    public void beforeClass() {
        ReportManager.initReports();
        PlaywrightFactory.start();
        browser = PlaywrightFactory.getBrowser();
    }

    /**
     * Runs before each test method.
     * Creates a new browser context, page, and report test node.
     */
    @BeforeMethod(alwaysRun = true)
    public void beforeMethod(java.lang.reflect.Method method) {

        context = PlaywrightFactory.getContext();
        page = context.newPage();

        currentTestName = method.getName();

        ReportManager.createTest(currentTestName);
        actions = new PageActions(page, currentTestName);
    }

    /**
     * Runs after each test method.
     * Logs the test result and cleans up browser context.
     */
    @AfterMethod(alwaysRun = true)
    public void afterMethod(ITestResult result) {

        try {
            switch (result.getStatus()) {

                case ITestResult.FAILURE:
                    ReportManager.stepFail(
                            "Test failed: " + result.getName(),
                            page
                    );
                    break;

                case ITestResult.SUCCESS:
                    ReportManager.stepPass(
                            "Test passed: " + result.getName()
                    );
                    break;

                case ITestResult.SKIP:
                    ReportManager.stepSkip(
                            "Test skipped: " + result.getName()
                    );
                    break;

                default:
                    break;
            }

        } finally {
            if (context != null) {
                context.close();
            }
            page = null;
            actions = null;
            context = null;
        }
    }

    /**
     * Runs once after all test methods in the class.
     * Flushes ExtentReports and stops Playwright.
     */
    @AfterClass(alwaysRun = true)
    public void afterClass() {
        PlaywrightFactory.stop();
        ReportManager.flush();
    }

    // ------------------------------------------------------------------
    // Page Object Factory
    // ------------------------------------------------------------------

    /**
     * Creates and returns a Page Object instance.
     *
     * @param pageClass Page Object class type
     * @param <T>       Type extending BasePage
     * @return instance of the requested Page Object
     */
    protected <T extends BasePage> T getPage(Class<T> pageClass) {
        try {
            return pageClass
                    .getConstructor(Page.class, String.class)
                    .newInstance(page, currentTestName);
        } catch (Exception e) {
            throw new RuntimeException(
                    "Failed to create page: " + pageClass.getSimpleName(), e
            );
        }
    }
}
