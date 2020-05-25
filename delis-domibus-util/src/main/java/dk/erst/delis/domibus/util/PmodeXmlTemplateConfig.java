package dk.erst.delis.domibus.util;

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.thymeleaf.spring5.SpringTemplateEngine;
import org.thymeleaf.spring5.templateresolver.SpringResourceTemplateResolver;

@Configuration
public class PmodeXmlTemplateConfig {

	public SpringResourceTemplateResolver xmlTemplateResolver(ApplicationContext appCtx) {
		SpringResourceTemplateResolver templateResolver = new SpringResourceTemplateResolver();

		templateResolver.setApplicationContext(appCtx);
		templateResolver.setPrefix("classpath:/templates_pmode/");
		templateResolver.setSuffix(".xml");
		templateResolver.setTemplateMode("XML");
		templateResolver.setCharacterEncoding("UTF-8");
		templateResolver.setCacheable(true);

		return templateResolver;
	}

	@Bean(name = "pmodeXmlTemplateEngineWrapper")
	public SpringTemplateEngineWrapper templateEngine(ApplicationContext appCtx) {
		SpringTemplateEngine templateEngine = new SpringTemplateEngine();
		templateEngine.setTemplateResolver(xmlTemplateResolver(appCtx));
		return new SpringTemplateEngineWrapper(templateEngine);
	}

	public static class SpringTemplateEngineWrapper {

		private SpringTemplateEngine templateEngine;

		public SpringTemplateEngineWrapper(SpringTemplateEngine templateEngine) {
			this.templateEngine = templateEngine;
		}

		public SpringTemplateEngine getTemplateEngine() {
			return templateEngine;
		}
	}
}
