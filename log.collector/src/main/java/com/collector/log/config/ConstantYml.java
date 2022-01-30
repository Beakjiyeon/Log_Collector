package com.collector.log.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Getter
@Setter
@Configuration
// value를 통해 값이 있는 위치를 명시해준다.
@PropertySource(value = "classpath:constant-dev.yml", factory = YamlPropertySourceFactory.class)
@ConfigurationProperties(prefix = "email")
public class ConstantYml {

    private String receiver;

}
