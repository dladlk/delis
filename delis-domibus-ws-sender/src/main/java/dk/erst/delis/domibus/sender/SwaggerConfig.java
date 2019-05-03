package dk.erst.delis.domibus.sender;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger.web.UiConfiguration;
import springfox.documentation.swagger.web.UiConfigurationBuilder;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@Configuration
@EnableSwagger2
public class SwaggerConfig {

    @Bean
    public Docket api() {

        return new Docket(DocumentationType.SWAGGER_2)
                .select()
                .apis(RequestHandlerSelectors.basePackage("dk.erst.delis.domibus.sender.rest"))
                .paths(PathSelectors.regex("/rest/.*"))
                .build()
                .apiInfo(metadata());
    }

    @Bean
    public UiConfiguration uiConfig() {
        return UiConfigurationBuilder.builder().build();
    }

    private ApiInfo metadata() {

        return new ApiInfoBuilder()
                .title("Delis Domibus Swagger")
                .license("Apache License Version 2.0")
                .version("1.0")
                .build();
    }
}
