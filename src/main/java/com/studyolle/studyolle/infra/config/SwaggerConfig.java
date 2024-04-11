package com.studyolle.studyolle.infra.config;

import io.swagger.annotations.ApiOperation;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.*;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.contexts.SecurityContext;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.Arrays;
import java.util.List;

import static com.google.common.collect.Sets.newHashSet;

@Configuration
@EnableSwagger2
public class SwaggerConfig {

    private final JwtTokenConfigure jwtTokenConfigure;

    public SwaggerConfig(JwtTokenConfigure jwtTokenConfigure) {
        this.jwtTokenConfigure = jwtTokenConfigure;
    }

    @Bean
    public Docket api() {
        return new Docket(DocumentationType.SWAGGER_2)
                .groupName( "USER" )
                .select()
                .apis(RequestHandlerSelectors.withMethodAnnotation(ApiOperation.class))
                .paths(PathSelectors.any())
                .build()
                .apiInfo(apiInfo())
                .securityContexts(Arrays.asList(securityContext()))
                .securitySchemes(Arrays.asList(apiKey()))
                ;
    }

    private ApiKey apiKey() {
        return new ApiKey("JWT", jwtTokenConfigure.getHeader(), "header");
    }

    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title("TEST_API")
                .description("ACE_SUPER_ADMIN")
                .version("0.0.1")
                .contact(new Contact("lee seunggu", "localhost:8080", "leesg107@naver.com"))
                .build();
    }

    private SecurityContext securityContext() {
        return SecurityContext.builder().securityReferences(defaultAuth()).build();
    }

    List<SecurityReference> defaultAuth() {
        AuthorizationScope authorizationScope = new AuthorizationScope("global", "accessEverything");
        AuthorizationScope[] authorizationScopes = new AuthorizationScope[1];
        authorizationScopes[0] = authorizationScope;
        return Arrays.asList(new SecurityReference("JWT", authorizationScopes));
    }

}
