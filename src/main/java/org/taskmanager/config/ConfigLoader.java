package org.taskmanager.config;

import org.taskmanager.exeptions.ConfigurationException;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ConfigLoader {
    private ConfigLoader(){}
    private static final Properties properties = new Properties();

    static {
        try (InputStream inputStream = ConfigLoader.class.getResourceAsStream("/config.properties")) {
            if (inputStream == null){
                throw new ConfigurationException("файла config.properties не существует");
            }
            properties.load(inputStream);
        } catch (IOException e) {
            throw new ConfigurationException("Ошибка считывания файла",e);
        }
    }
    public static String getProperty(String key) {return properties.getProperty(key);}
}
