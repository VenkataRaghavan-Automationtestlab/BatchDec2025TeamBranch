package utils;

import org.testng.IRetryAnalyzer;
import org.testng.ITestResult;

/**
 * =====================================================================
 * TestRetryAnalyzer
 * =====================================================================
 *
 * Custom TestNG retry analyzer for handling flaky test failures.
 *
 * Features:
 *  - Config-driven retry count
 *  - Safe defaults (no crashes if config missing)
 *  - Retry awareness for reporting
 *  - One retry analyzer instance per test method
 *
 * Usage:
 *  @Test(retryAnalyzer = TestRetryAnalyzer.class)
 * =====================================================================
 */
public class TestRetryAnalyzer implements IRetryAnalyzer {

    /** Tracks current retry attempt for the test method */
    private int retryCount = 0;

    /** Maximum number of retries allowed */
    private final int maxRetryCount;

    /**
     * Constructor.
     * Reads retry count from config.properties.
     *
     * Config key:
     *   retry.count=1
     */
    public TestRetryAnalyzer() {
        this.maxRetryCount = ConfigManager.getInt("retry.count", 1);
    }

    /**
     * Determines whether a failed test should be retried.
     *
     * @param result TestNG test result
     * @return true if retry should happen, false otherwise
     */
    @Override
    public boolean retry(ITestResult result) {

        if (retryCount < maxRetryCount) {

            retryCount++;

            // Optional reporting hook
            try {
                ReportManager.stepInfo(
                        "Retrying test [" + result.getName() +
                        "] - Attempt " + retryCount +
                        " of " + maxRetryCount
                );
            } catch (Exception ignored) {
                // ReportManager may not be initialized yet
            }

            return true;
        }

        return false;
    }
}
