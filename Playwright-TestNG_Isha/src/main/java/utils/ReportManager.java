package utils;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.MediaEntityBuilder;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import com.microsoft.playwright.Page;

import java.io.File;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.Locale;

/**
 * =====================================================================
 * ReportManager
 * =====================================================================
 *
 * Centralized manager for ExtentReports.
 *
 * Responsibilities:
 *  - Initialize ExtentReports (once per run)
 *  - Manage thread-safe ExtentTest instances
 *  - Log PASS / FAIL / SKIP / INFO steps
 *  - Capture screenshots via ScreenshotUtil
 *  - Flush reports at the end of execution
 *
 * Design principles:
 *  - Thread-safe via ThreadLocal
 *  - Screenshot responsibility owned here
 *  - Tests and PageActions never handle screenshots directly
 * =====================================================================
 */
public final class ReportManager {

    /** Singleton ExtentReports instance */
    private static ExtentReports extent;

    /** Thread-safe ExtentTest storage for parallel execution */
    private static final ThreadLocal<ExtentTest> TEST = new ThreadLocal<>();

    /** Single source of truth for report directory */
    private static String reportDir;

    // Prevent instantiation
    private ReportManager() {
    }

    // ------------------------------------------------------------------
    // Report lifecycle
    // ------------------------------------------------------------------

    /**
     * Initializes ExtentReports.
     * Safe to call multiple times (idempotent).
     */
    public static synchronized void initReports() {

        if (extent != null) {
            return;
        }

        String baseDir = ConfigManager.getProperty("report.dir", "reports");

        LocalDate today = LocalDate.now();
        LocalDateTime now = LocalDateTime.now();

        String year = String.valueOf(today.getYear());
        String month = today.getMonth()
                .getDisplayName(TextStyle.SHORT, Locale.ENGLISH);
        String date = today.format(DateTimeFormatter.ofPattern("dd-MM-yyyy"));
        String time = now.format(DateTimeFormatter.ofPattern("HH-mm-ss"));

        reportDir = String.join(
                File.separator,
                baseDir,
                year,
                month,
                date,
                "run_" + time
        );

        new File(reportDir).mkdirs();

        ExtentSparkReporter spark =
                new ExtentSparkReporter(reportDir + File.separator + "TestReport.html");

        extent = new ExtentReports();
        extent.attachReporter(spark);

        extent.setSystemInfo("Author", ConfigManager.get("name"));
        extent.setSystemInfo("Framework", "Playwright Java");
        extent.setSystemInfo("Environment",
                ConfigManager.getProperty("env", "QA"));
    }

    /**
     * Returns the report directory for the current run.
     */
    public static String getReportDir() {
        return reportDir;
    }

    /**
     * Creates and binds an ExtentTest to the current thread.
     */
    public static ExtentTest createTest(String testName) {
        ExtentTest t = extent.createTest(testName);
        TEST.set(t);
        return t;
    }

    /**
     * Returns the current thread's ExtentTest.
     */
    public static ExtentTest getTest() {
        return TEST.get();
    }

    /**
     * Flushes ExtentReports to disk.
     * Should be called once after all tests.
     */
    public static void flush() {
        if (extent != null) {
            extent.flush();
        }
        TEST.remove();
    }

    // ------------------------------------------------------------------
    // Step logging
    // ------------------------------------------------------------------

    /**
     * Logs a PASS step.
     * Screenshot capture is controlled via config:
     * screenshot.on.pass=true|false
     */
    public static void stepPass(String message, Page page) {

        ExtentTest t = getTest();
        if (t == null) {
            return;
        }

        boolean captureOnPass =
                ConfigManager.getBoolean("screenshot.on.pass");

        if (captureOnPass && page != null) {

            String base64 = ScreenshotUtil.captureBase64(page);

            if (base64 != null) {
                t.pass(
                        message,
                        MediaEntityBuilder
                                .createScreenCaptureFromBase64String(base64)
                                .build()
                );
                return;
            }
        }

        t.pass(message);
    }

    /**
     * Logs a FAIL step with screenshot (always attempts).
     */
    public static void stepFail(String message, Page page) {

        ExtentTest t = getTest();
        if (t == null) {
            return;
        }

        String base64 =
                (page != null) ? ScreenshotUtil.captureBase64(page) : null;

        if (base64 != null) {
            t.fail(
                    message,
                    MediaEntityBuilder
                            .createScreenCaptureFromBase64String(base64)
                            .build()
            );
        } else {
            t.fail(message);
        }
    }

    /**
     * Logs a SKIP step.
     */
    public static void stepSkip(String message) {
        ExtentTest t = getTest();
        if (t != null) {
            t.skip(message);
        }
    }

    /**
     * Logs an INFO step.
     */
    public static void stepInfo(String message) {
        ExtentTest t = getTest();
        if (t != null) {
            t.info(message);
        }
    }

    /**
     * Logs a PASS step without screenshot.
     */
    public static void stepPass(String message) {
        ExtentTest t = getTest();
        if (t != null) {
            t.pass(message);
        }
    }
}
