package com.groupbyinc.datahub.consumer.config;

import com.google.cloud.secretmanager.v1.AccessSecretVersionResponse;
import com.google.cloud.secretmanager.v1.SecretManagerServiceClient;
import com.google.cloud.secretmanager.v1.SecretVersionName;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

@Slf4j
public class ApplicationConfig {

    private static ApplicationConfig instance;
    private final Properties properties = new Properties();

    private ApplicationConfig(String propertiesFilePath, String projectId) {
        loadProperties(propertiesFilePath);
        overridePropertiesWithSecrets(projectId);
    }

    public static synchronized ApplicationConfig getInstance(String propertiesFilePath,
                                                             String projectId) {
        if (instance == null) {
            instance = new ApplicationConfig(propertiesFilePath, projectId);
        }
        return instance;
    }

    public String getProperty(String key) {
        return properties.getProperty(key);
    }

    @Override
    public String toString() {
        return properties.toString();
    }

    private void loadProperties(String filePath) {
        try (InputStream input = getClass().getClassLoader().getResourceAsStream(filePath)) {
            properties.load(input);
        } catch (IOException e) {
            log.error("Error loading properties from {}", filePath, e);
            throw new RuntimeException();
        }
    }

    private void overridePropertiesWithSecrets(String projectId) {
        try (SecretManagerServiceClient client = SecretManagerServiceClient.create()) {
            for (String key : properties.stringPropertyNames()) {
                String secretValue = getSecretValue(client, projectId, key.toUpperCase());
                if (secretValue != null) {
                    properties.setProperty(key, secretValue);
                }
            }
        } catch (IOException e) {
            log.error("Error override config from secret manager", e);
            throw new RuntimeException();
        }
    }

    private String getSecretValue(SecretManagerServiceClient client, String projectId, String secretId) {
        try {
            SecretVersionName secretVersionName = SecretVersionName.of(projectId, secretId, "latest");
            AccessSecretVersionResponse response = client.accessSecretVersion(secretVersionName);
            return response.getPayload().getData().toStringUtf8();
        } catch (Exception e) {
            System.out.printf("Secret %s not found in Secret Manager.%n", secretId);
            return null;
        }
    }
}
