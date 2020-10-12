package com.fengyang.upload.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

@Data
@ConfigurationProperties(prefix = "fy.upload")
public class UploadProperties {
    private String baseUrl;
    private List<String> allowTypes;
}
