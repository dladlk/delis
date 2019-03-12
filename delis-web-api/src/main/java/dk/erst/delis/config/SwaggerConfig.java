package dk.erst.delis.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.ParameterBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.schema.ModelRef;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Parameter;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger.web.UiConfiguration;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.ArrayList;
import java.util.List;

/**
 * @author funtusthan, created by 12.03.19
 */

@Configuration
@EnableSwagger2
public class SwaggerConfig {

    @Bean
    public Docket api() {
        List<Parameter> parameters = new ArrayList<>();
        ParameterBuilder parameter = new ParameterBuilder();
        parameter.name("Authorization").modelRef(new ModelRef("string")).parameterType("header").required(false);
        parameters.add(parameter.build());
        return new Docket(DocumentationType.SWAGGER_2)
                .select()
                .apis(RequestHandlerSelectors.basePackage("dk.erst.delis.rest.controller"))
                .paths(PathSelectors.regex("/swagger/.*"))
                .build()
                .apiInfo(metadata()).pathMapping("").globalOperationParameters(parameters);
    }

    @Bean
    public UiConfiguration uiConfig() {
        return UiConfiguration.DEFAULT;
    }

    private ApiInfo metadata() {
        return new ApiInfoBuilder()
                .title("Delis Rest API")
                .license("Apache License Version 2.0")
                .version("1.0")
                .build();
    }
}
