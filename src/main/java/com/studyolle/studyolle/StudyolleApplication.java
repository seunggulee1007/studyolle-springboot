package com.studyolle.studyolle;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
public class StudyolleApplication {

    public static void main ( String[] args ) {
        SpringApplication.run( StudyolleApplication.class, args );
    }

}
