package dk.erst.delis.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger.web.UiConfiguration;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * @author funtusthan, created by 12.03.19
 */

@Configuration
@EnableSwagger2
public class SwaggerConfig {

    @Bean
    public Docket api() {

        return new Docket(DocumentationType.SWAGGER_2)
                .select()
                .apis(RequestHandlerSelectors.basePackage("dk.erst.delis.rest"))
                .paths(PathSelectors.regex("/rest/.*"))
                .build()
                .apiInfo(metadata());
    }

    @Bean
    public UiConfiguration uiConfig() {
        return UiConfiguration.DEFAULT;
    }

    private ApiInfo metadata() {

        return new ApiInfoBuilder()
                .title("Delis Swagger")
                .license("Apache License Version 2.0")
                .version("1.0")
                .build();
    }
}
