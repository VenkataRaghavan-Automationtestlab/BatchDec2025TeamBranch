package utils;

import com.microsoft.playwright.Page;
import com.microsoft.playwright.Page.ScreenshotOptions;

import java.nio.file.Files;
import java.nio.file.Path;
import java.text.SimpleDateFormat;
import java.util.Base64;
import java.util.Date;

/**
 * =====================================================================
 * ScreenshotUtil
 * =====================================================================
 *
 * Utility class for capturing screenshots using Playwright.
 *
 * Supports:
 *  - Base64 screenshots (recommended for ExtentReports)
 *  - File-based screenshots (optional, for local debugging)
 *
 * Design principles:
 *  - No logging here (handled by ReportManager)
 *  - No test logic here
 *  - Fail-safe (never breaks test execution)
 * =====================================================================
 */
public final class ScreenshotUtil {

    // Prevent instantiation
    private ScreenshotUtil() {
    }

    /**
     * Captures a screenshot and returns it as a Base64 string.
     *
     * @param page Playwright Page instance
     * @return Base64 screenshot string or null if capture fails
     */
    public static String captureBase64(Page page) {

        if (page == null) {
            return null;
        }

        try {
            byte[] bytes = page.screenshot(
                    new ScreenshotOptions().setFullPage(true)
            );
            return Base64.getEncoder().encodeToString(bytes);

        } catch (Exception e) {
            // Never fail the test due to screenshot issues
            return null;
        }
    }

    /**
     * Captures a screenshot and saves it to disk.
     *
     * Screenshot path:
     *   {reportDir}/screenshots/{stepName_timestamp}.png
     *
     * @param page     Playwright Page instance
     * @param stepName logical step name (used in file naming)
     * @return absolute screenshot path or null if capture fails
     */
    public static String captureToFile(Page page, String stepName) {

        if (page == null) {
            return null;
        }

        try {
            String safeStepName =
                    stepName == null ? "step"
                            : stepName.replaceAll("[^a-zA-Z0-9-_]", "_");

            String timestamp =
                    new SimpleDateFormat("yyyyMMdd_HHmmssSSS")
                            .format(new Date());

            String fileName = safeStepName + "_" + timestamp + ".png";

            Path screenshotsDir =
                    Path.of(ReportManager.getReportDir(), "screenshots");

            Files.createDirectories(screenshotsDir);

            Path screenshotPath = screenshotsDir.resolve(fileName);

            page.screenshot(
                    new ScreenshotOptions()
                            .setPath(screenshotPath)
                            .setFullPage(true)
            );

            return screenshotPath
                    .toAbsolutePath()
                    .toString()
                    .replace("\\", "/");

        } catch (Exception e) {
            // Never fail the test due to screenshot issues
            return null;
        }
    }
}
