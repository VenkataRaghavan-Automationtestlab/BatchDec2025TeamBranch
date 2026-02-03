package utils;

import com.microsoft.playwright.Dialog;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.SelectOption;
import com.microsoft.playwright.options.WaitForSelectorState;

import java.text.Normalizer;
import java.time.Duration;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

/**
 * =====================================================================
 * PageActions
 * =====================================================================
 *
 * Enterprise-grade wrapper around Playwright Page & Locator.
 *
 * Design principles:
 *  - PageActions performs ONLY user actions & assertions
 *  - ReportManager handles logging & screenshots
 *  - Screenshot capture is config-driven (screenshot.on.pass)
 *  - No business logic, no test logic here
 *
 * This class is safe to reuse across all Page Objects.
 * =====================================================================
 */
public class PageActions {

    private final Page page;
    @SuppressWarnings("unused")
	private final String testNamePrefix;

    /**
     * Constructor.
     *
     * @param page     Playwright Page instance
     * @param testName current test name (used for logging context)
     */
    public PageActions(Page page, String testName) {

        if (page == null) {
            throw new IllegalArgumentException("Playwright Page must not be null");
        }

        this.page = page;
        this.testNamePrefix = sanitize(testName == null ? "test" : testName);
    }

    // ------------------------------------------------------------------
    // Internal helpers
    // ------------------------------------------------------------------

    /**
     * Sanitizes test name for safe logging/screenshot usage.
     */
    private String sanitize(String value) {
        String normalized = Normalizer.normalize(value, Normalizer.Form.NFKD);
        return normalized
                .replaceAll("[^\\p{Alnum}]+", "_")
                .replaceAll("_+", "_")
                .toLowerCase(Locale.ROOT);
    }

    /**
     * Centralized success logging for every action.
     */
    private void logAction(String description) {
        try {
            ReportManager.stepPass(description, page);
        } catch (Exception e) {
            ReportManager.stepInfo("Action logged without screenshot: " + description);
        }
    }

    // ------------------------------------------------------------------
    // Navigation
    // ------------------------------------------------------------------

    public PageActions navigate(String url) {
        page.navigate(url);
        logAction("Navigate → " + url);
        return this;
    }

    // ------------------------------------------------------------------
    // Click actions
    // ------------------------------------------------------------------

    public PageActions click(String selector) {
        page.locator(selector).first().click();
        logAction("Click → " + selector);
        return this;
    }

    public PageActions click(Locator locator) {
        locator.first().click();
        logAction("Click → locator");
        return this;
    }

    /**
     * Retry-based safe click (use sparingly).
     */
    public PageActions safeClick(String selector, int retries, Duration retryDelay) {

        Locator locator = page.locator(selector);
        int attempts = Math.max(1, retries);

        for (int i = 0; i < attempts; i++) {
            try {
                locator.click();
                logAction("SafeClick → " + selector + " (attempt " + (i + 1) + ")");
                return this;
            } catch (Exception e) {
                if (i == attempts - 1) {
                    throw e;
                }
                try {
                    Thread.sleep(retryDelay.toMillis());
                } catch (InterruptedException ignored) {
                }
            }
        }
        return this;
    }

    // ------------------------------------------------------------------
    // Typing / Input
    // ------------------------------------------------------------------

    public PageActions fill(String selector, String value) {
        page.locator(selector).fill(value);
        logAction("Fill → " + selector + " = " + value);
        return this;
    }

    public PageActions fill(Locator locator, String value) {
        locator.fill(value);
        logAction("Fill → locator = " + value);
        return this;
    }

    // ------------------------------------------------------------------
    // Text retrieval
    // ------------------------------------------------------------------

    public String getText(String selector) {
        String text = page.locator(selector).innerText();
        logAction("GetText → " + selector + " = " + text);
        return text;
    }

    public String getTextLoc(Locator locator) {
        String text = locator.innerText();
        logAction("GetText → locator = " + text);
        return text;
    }

    public List<String> getAllTexts(String selector) {
        List<String> texts = page.locator(selector).allInnerTexts();
        logAction("GetAllTexts → " + selector);
        return texts;
    }

    // ------------------------------------------------------------------
    // Waits
    // ------------------------------------------------------------------

    public PageActions waitForVisible(String selector, Duration timeout) {
        page.locator(selector).waitFor(
                new Locator.WaitForOptions()
                        .setState(WaitForSelectorState.VISIBLE)
                        .setTimeout((int) timeout.toMillis())
        );
        logAction("WaitForVisible → " + selector);
        return this;
    }

    public PageActions waitForHidden(String selector, Duration timeout) {
        page.locator(selector).waitFor(
                new Locator.WaitForOptions()
                        .setState(WaitForSelectorState.HIDDEN)
                        .setTimeout((int) timeout.toMillis())
        );
        logAction("WaitForHidden → " + selector);
        return this;
    }

    // ------------------------------------------------------------------
    // Dropdowns
    // ------------------------------------------------------------------

    public PageActions selectByValue(String selector, String value) {
        page.locator(selector).selectOption(value);
        logAction("SelectByValue → " + selector + " = " + value);
        return this;
    }

    public PageActions selectByText(String selector, String text) {
        page.locator(selector)
                .selectOption(new SelectOption().setLabel(text));
        logAction("SelectByText → " + selector + " = " + text);
        return this;
    }

    // ------------------------------------------------------------------
    // Checkbox / Radio
    // ------------------------------------------------------------------

    public PageActions check(String selector) {
        page.locator(selector).check();
        logAction("Check → " + selector);
        return this;
    }

    public PageActions uncheck(String selector) {
        page.locator(selector).uncheck();
        logAction("Uncheck → " + selector);
        return this;
    }

    public PageActions selectRadio(String selector) {
        page.locator(selector).check();
        logAction("SelectRadio → " + selector);
        return this;
    }

    // ------------------------------------------------------------------
    // State checks
    // ------------------------------------------------------------------

    public boolean isVisible(String selector) {
        boolean result = page.locator(selector).isVisible();
        logAction("IsVisible → " + selector + " = " + result);
        return result;
    }

    public boolean isEnabled(String selector) {
        boolean result = page.locator(selector).isEnabled();
        logAction("IsEnabled → " + selector + " = " + result);
        return result;
    }

    public boolean isChecked(String selector) {
        boolean result = page.locator(selector).isChecked();
        logAction("IsChecked → " + selector + " = " + result);
        return result;
    }

    // ------------------------------------------------------------------
    // Assertions (Playwright native)
    // ------------------------------------------------------------------

    public PageActions assertVisible(String selector) {
        assertThat(page.locator(selector)).isVisible();
        logAction("AssertVisible → " + selector);
        return this;
    }

    public PageActions assertEnabled(String selector) {
        assertThat(page.locator(selector)).isEnabled();
        logAction("AssertEnabled → " + selector);
        return this;
    }

    public PageActions assertChecked(String selector) {
        assertThat(page.locator(selector)).isChecked();
        logAction("AssertChecked → " + selector);
        return this;
    }

    public PageActions assertHasText(String selector, String exactText) {
        assertThat(page.locator(selector)).hasText(exactText);
        logAction("AssertHasText → " + selector + " = " + exactText);
        return this;
    }

    public PageActions assertHasText(String selector, Pattern regex) {
        assertThat(page.locator(selector)).hasText(regex);
        logAction("AssertHasText (regex) → " + selector);
        return this;
    }

    // ------------------------------------------------------------------
    // Alerts / Dialogs
    // ------------------------------------------------------------------

    public PageActions acceptNextAlert() {
        page.onceDialog(Dialog::accept);
        logAction("Register Alert → Accept");
        return this;
    }

    public PageActions dismissNextAlert() {
        page.onceDialog(Dialog::dismiss);
        logAction("Register Alert → Dismiss");
        return this;
    }

    public PageActions handleNextPrompt(String value) {
        page.onceDialog(dialog -> {
            if ("prompt".equals(dialog.type())) {
                dialog.accept(value);
            } else {
                dialog.accept();
            }
        });
        logAction("Register Prompt Handler → " + value);
        return this;
    }
}
