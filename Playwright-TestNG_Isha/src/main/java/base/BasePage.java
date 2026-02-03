package base;

import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.LoadState;
import com.microsoft.playwright.options.WaitForSelectorState;
import com.microsoft.playwright.Locator;
import org.testng.Assert;
import utils.PageActions;

/**
 * =====================================================================
 * BasePage
 * =====================================================================
 *
 * Enterprise-level base class for all Page Objects.
 *
 * Responsibilities:
 *  - Holds the Playwright Page instance
 *  - Provides common navigation & browser utilities
 *  - Exposes reusable wait and validation helpers
 *  - Offers generic assertions usable across all pages
 *
 * ❌ What BasePage MUST NOT contain:
 *  - Page-specific locators
 *  - Business logic
 *  - Test data
 *  - Application-specific workflows
 *
 * All application pages (LoginPage, HomePage, CartPage, etc.)
 * MUST extend this class.
 * =====================================================================
 */
public abstract class BasePage {

    /** Playwright page instance representing the active browser tab */
    protected final Page page;

    /** Wrapper for Playwright actions with logging & reporting */
    protected final PageActions actions;
    protected final String testName;   // ✅ ADD THIS

    /**
     * Constructor for BasePage.
     *
     * @param page     Playwright Page instance
     * @param testName Current test name (used for logging & screenshots)
     */
    protected BasePage(Page page, String testName) {
        this.page = page;
        this.testName = testName;      // ✅ STORE IT
        this.actions = new PageActions(page, testName);
    }

    // ------------------------------------------------------------------
    // Navigation Helpers
    // ------------------------------------------------------------------

    /**
     * Navigates to the given URL.
     * Automatically waits for DOM content to load.
     */
    public void navigateTo(String url) {
        actions.navigate(url);
        waitForDomLoad();
    }

    /**
     * Reloads the current page.
     */
    public void refresh() {
        page.reload();
        waitForDomLoad();
    }

    /**
     * Navigates back in browser history.
     */
    public void goBack() {
        page.goBack();
        waitForDomLoad();
    }

    /**
     * Navigates forward in browser history.
     */
    public void goForward() {
        page.goForward();
        waitForDomLoad();
    }

    // ------------------------------------------------------------------
    // Page Load & Wait Helpers
    // ------------------------------------------------------------------

    /**
     * Waits until DOM content is fully loaded.
     */
    public void waitForDomLoad() {
        page.waitForLoadState(LoadState.DOMCONTENTLOADED);
    }

    /**
     * Waits until all network calls are idle.
     * Useful for SPAs and heavy AJAX pages.
     */
    public void waitForNetworkIdle() {
        page.waitForLoadState(LoadState.NETWORKIDLE);
    }

    /**
     * Waits until the given element becomes visible.
     */
    public void waitForVisible(String selector) {
        page.locator(selector).waitFor();
    }

    /**
     * Waits until the given element becomes hidden.
     */
    public void waitForHidden(String selector) {
        page.locator(selector).waitFor(
            new Locator.WaitForOptions()
                .setState(WaitForSelectorState.HIDDEN)
        );
    }

    // ------------------------------------------------------------------
    // Element State Helpers
    // ------------------------------------------------------------------

    /**
     * Checks if the element is visible on the page.
     */
    public boolean isVisible(String selector) {
        return page.locator(selector).isVisible();
    }

    /**
     * Checks if the element is enabled.
     */
    public boolean isEnabled(String selector) {
        return page.locator(selector).isEnabled();
    }

    /**
     * Checks if the element is disabled.
     */
    public boolean isDisabled(String selector) {
        return page.locator(selector).isDisabled();
    }

    // ------------------------------------------------------------------
    // Page Information Utilities
    // ------------------------------------------------------------------

    /**
     * Returns the current page title.
     */
    public String getPageTitle() {
        return page.title();
    }

    /**
     * Returns the current page URL.
     */
    public String getPageUrl() {
        return page.url();
    }

    // ------------------------------------------------------------------
    // Assertion Helpers (Generic & Reusable)
    // ------------------------------------------------------------------

    /**
     * Asserts the exact page title.
     */
    public void assertPageTitle(String expectedTitle) {
        Assert.assertEquals(
                getPageTitle(),
                expectedTitle,
                "Page title mismatch"
        );
    }

    /**
     * Asserts that the page URL contains the given value.
     */
    public void assertUrlContains(String value) {
        Assert.assertTrue(
                getPageUrl().contains(value),
                "Expected URL to contain: " + value
        );
    }

    /**
     * Asserts that an element is visible.
     */
    public void assertVisible(String selector) {
        Assert.assertTrue(
                isVisible(selector),
                "Expected element to be visible: " + selector
        );
    }

    // ------------------------------------------------------------------
    // Browser / Context Helpers
    // ------------------------------------------------------------------

    /**
     * Clears all cookies in the current browser context.
     */
    public void clearCookies() {
        page.context().clearCookies();
    }

    /**
     * Clears browser local storage.
     */
    public void clearLocalStorage() {
        page.evaluate("() => localStorage.clear()");
    }

    /**
     * Opens a new browser tab within the same context.
     *
     * @return new Page instance
     */
    public Page openNewTab() {
        return page.context().newPage();
    }

    /**
     * Returns the number of open tabs.
     */
    public int getOpenTabCount() {
        return page.context().pages().size();
    }
}
