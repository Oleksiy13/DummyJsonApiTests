package com.dummyjson.api.tests.helpers;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ConfigReader {

    private static final String CONFIG_FILE_NAME = "config.properties";
    private static Properties properties;


    private ConfigReader() {
    }


    public static Properties getProperties() {
        if (properties == null) {
            synchronized (ConfigReader.class) {
                if (properties == null) {
                    properties = new Properties();
                    try (InputStream input = ConfigReader.class.getClassLoader().getResourceAsStream(CONFIG_FILE_NAME)) {
                        if (input == null) {
                            throw new RuntimeException("ERROR: Unable to find " + CONFIG_FILE_NAME + " in classpath. Please ensure it's in src/test/resources.");
                        }
                        properties.load(input);
                    } catch (IOException e) {
                        throw new RuntimeException("ERROR loading " + CONFIG_FILE_NAME + ": " + e.getMessage(), e);
                    }
                }
            }
        }
        return properties;
    }


    public static String getProperty(String key) {
        return getProperties().getProperty(key);
    }
}
