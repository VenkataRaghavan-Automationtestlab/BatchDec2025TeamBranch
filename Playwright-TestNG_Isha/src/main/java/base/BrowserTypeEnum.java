package base;

import utils.ConfigManager;

/**
 * =====================================================================
 * BrowserTypeEnum
 * =====================================================================
 *
 * Enum representing all supported browser types in the framework.
 *
 * Why this enum exists:
 *  - Avoids hardcoded browser strings
 *  - Centralizes browser selection logic
 *  - Makes framework config-driven and type-safe
 *
 * Used by:
 *  - PlaywrightFactory
 *  - BaseTest
 *  - Config-based browser initialization
 * =====================================================================
 */
public enum BrowserTypeEnum {

    CHROME("chrome"),
    FIREFOX("firefox"),
    WEBKIT("webkit"),
    MSEDGE("msedge");

    /** Browser name as used in config and logs */
    private final String browserName;

    /**
     * Constructor to associate enum with browser string.
     *
     * @param browserName lowercase browser name
     */
    BrowserTypeEnum(String browserName) {
        this.browserName = browserName;
    }

    /**
     * Returns the browser name as string.
     *
     * @return browser name (e.g. chrome, firefox)
     */
    public String getBrowserName() {
        return browserName;
    }

    // ------------------------------------------------------------------
    // Conversion utilities
    // ------------------------------------------------------------------

    /**
     * Converts a string value to BrowserTypeEnum.
     * Defaults to CHROME if value is null or unsupported.
     *
     * @param value browser name from config or system property
     * @return resolved BrowserTypeEnum
     */
    public static BrowserTypeEnum fromString(String value) {

        if (value == null || value.isBlank()) {
            return CHROME;
        }

        for (BrowserTypeEnum type : BrowserTypeEnum.values()) {
            if (type.browserName.equalsIgnoreCase(value.trim())) {
                return type;
            }
        }

        // Safe default
        return CHROME;
    }

    /**
     * Reads browser type from configuration.
     *
     * Config key used:
     *   browser=chrome|firefox|webkit|msedge
     *
     * @return BrowserTypeEnum resolved from config
     */
    public static BrowserTypeEnum fromConfig() {
        return fromString(ConfigManager.get("browser"));
    }
}
