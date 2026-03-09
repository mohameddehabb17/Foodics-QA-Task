package com.foodics.qa.shared.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Centralized configuration accessor.
 *
 * <p>Value resolution order is system property, then environment variable,
 * then {@code config.properties} file.</p>
 */
public final class ConfigManager {
    private static final Logger LOGGER = Logger.getLogger(ConfigManager.class.getName());
    private static final Properties PROPERTIES = new Properties();

    static {
        try (InputStream input = ConfigManager.class.getClassLoader().getResourceAsStream("config.properties")) {
            if (input != null) {
                PROPERTIES.load(input);
            } else {
                LOGGER.log(Level.FINE, "config.properties was not found on the classpath.");
            }
        } catch (IOException exception) {
            throw new RuntimeException("Failed to load config.properties", exception);
        }
    }

    private ConfigManager() {
    }

    /**
     * Resolves a configuration value by key.
     *
     * @param key configuration key
     * @return resolved value, or {@code null} if not found
     */
    public static String get(String key) {
        String normalizedSystemValue = normalize(System.getProperty(key));
        if (normalizedSystemValue != null) {
            return normalizedSystemValue;
        }

        String envKey = key.toUpperCase().replace('.', '_');
        String normalizedEnvValue = normalize(System.getenv(envKey));
        if (normalizedEnvValue != null) {
            return normalizedEnvValue;
        }

        return normalize(PROPERTIES.getProperty(key));
    }

    /**
     * Resolves an integer configuration value.
     *
     * @param key configuration key
     * @param defaultValue fallback value when not found or invalid
     * @return parsed integer or default value
     */
    public static int getInt(String key, int defaultValue) {
        String value = get(key);
        if (value == null || value.isBlank()) {
            return defaultValue;
        }
        try {
            return Integer.parseInt(value.trim());
        } catch (NumberFormatException exception) {
            LOGGER.log(Level.FINE, "Invalid integer config for key {0}: {1}", new Object[]{key, value});
            return defaultValue;
        }
    }

    /**
     * Resolves a boolean configuration value.
     *
     * @param key configuration key
     * @param defaultValue fallback value when not found or invalid
     * @return parsed boolean or default value
     */
    public static boolean getBoolean(String key, boolean defaultValue) {
        String value = get(key);
        if (value == null || value.isBlank()) {
            return defaultValue;
        }
        String normalizedValue = value.trim().toLowerCase();
        if ("true".equals(normalizedValue)) {
            return true;
        }
        if ("false".equals(normalizedValue)) {
            return false;
        }
        return defaultValue;
    }

    private static String normalize(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        return value.trim();
    }
}

