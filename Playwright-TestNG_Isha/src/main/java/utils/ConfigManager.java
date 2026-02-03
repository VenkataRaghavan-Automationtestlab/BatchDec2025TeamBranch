package utils;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Properties;

/**
 * =====================================================================
 * ConfigManager
 * =====================================================================
 *
 * Centralized configuration reader for the framework.
 *
 * Responsibilities:
 *  - Load config.properties once at startup
 *  - Provide type-safe access to configuration values
 *  - Support default values
 *  - Support system property override (CI/CD friendly)
 *
 * Config file location:
 *  src/test/resources/config.properties
 * =====================================================================
 */
public final class ConfigManager {

    private static final Properties PROPERTIES = new Properties();

    static {
        loadProperties();
    }

    // Prevent instantiation
    private ConfigManager() {
    }

    /**
     * Loads config.properties into memory.
     * Executed once when the class is loaded.
     */
    private static void loadProperties() {
        try (InputStream is =
                     new FileInputStream("src/test/resources/config.properties")) {

            PROPERTIES.load(is);

        } catch (Exception e) {
            throw new RuntimeException(
                    "Failed to load config.properties from src/test/resources",
                    e
            );
        }
    }

    // ------------------------------------------------------------------
    // Property access methods
    // ------------------------------------------------------------------

    /**
     * Returns a configuration value.
     * System property takes precedence over config.properties.
     *
     * @param key configuration key
     * @return value or null if not found
     */
    public static String get(String key) {

        // Priority 1: JVM system property (-Dkey=value)
        String systemValue = System.getProperty(key);
        if (systemValue != null && !systemValue.isBlank()) {
            return systemValue.trim();
        }

        // Priority 2: config.properties
        String value = PROPERTIES.getProperty(key);
        return (value != null) ? value.trim() : null;
    }

    /**
     * Returns a configuration value with a default fallback.
     *
     * @param key          configuration key
     * @param defaultValue value to return if key is missing
     * @return resolved value
     */
    public static String getProperty(String key, String defaultValue) {
        String value = get(key);
        return (value == null || value.isBlank()) ? defaultValue : value;
    }

    /**
     * Returns a boolean configuration value.
     * Defaults to false if key is missing or invalid.
     *
     * @param key configuration key
     * @return boolean value
     */
    public static boolean getBoolean(String key) {
        return Boolean.parseBoolean(get(key));
    }

    /**
     * Returns an integer configuration value.
     * Defaults to the given fallback if missing or invalid.
     *
     * @param key          configuration key
     * @param defaultValue fallback integer value
     * @return integer configuration value
     */
    public static int getInt(String key, int defaultValue) {
        try {
            return Integer.parseInt(get(key));
        } catch (Exception e) {
            return defaultValue;
        }
    }
}
