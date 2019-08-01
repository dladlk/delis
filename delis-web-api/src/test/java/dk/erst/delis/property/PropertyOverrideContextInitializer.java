package dk.erst.delis.property;

import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.test.context.support.TestPropertySourceUtils;

public class PropertyOverrideContextInitializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {

    private static final String EXPIRED = "1";

    @Override
    public void initialize(ConfigurableApplicationContext configurableApplicationContext) {
        TestPropertySourceUtils.addInlinedPropertiesToEnvironment(
                configurableApplicationContext, "security.client.access.token.valid.time=" + EXPIRED);
        TestPropertySourceUtils.addInlinedPropertiesToEnvironment(
                configurableApplicationContext, "security.client.refresh.token.valid.time=" + EXPIRED);
        TestPropertySourceUtils.addInlinedPropertiesToEnvironment(
                configurableApplicationContext, "mysql.driver=org.h2.Driver");
        TestPropertySourceUtils.addInlinedPropertiesToEnvironment(
                configurableApplicationContext, "spring.jpa.hibernate.ddl-auto=update");
        TestPropertySourceUtils.addInlinedPropertiesToEnvironment(
                configurableApplicationContext, "spring.datasource.url=jdbc:h2:mem:delis;DB_CLOSE_ON_EXIT=FALSE;DB_CLOSE_DELAY=-1");
        TestPropertySourceUtils.addInlinedPropertiesToEnvironment(
                configurableApplicationContext, "spring.datasource.username=sa");
        TestPropertySourceUtils.addInlinedPropertiesToEnvironment(
                configurableApplicationContext, "spring.datasource.password=");
        TestPropertySourceUtils.addInlinedPropertiesToEnvironment(
                configurableApplicationContext, "spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.H2Dialect");
        TestPropertySourceUtils.addInlinedPropertiesToEnvironment(
                configurableApplicationContext, "spring.liquibase.enabled=false");
    }
}
