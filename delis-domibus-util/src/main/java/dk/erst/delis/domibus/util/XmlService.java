package dk.erst.delis.domibus.util;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring5.SpringTemplateEngine;

import dk.erst.delis.domibus.util.XmlTemplateConfig.SpringTemplateEngineWrapper;
import dk.erst.delis.domibus.util.pmode.PmodeData;

@Service
public class XmlService {

	protected final Log log = LogFactory.getLog(getClass());

	private SpringTemplateEngine xmlTemplateEngine;

	@Autowired
	public XmlService(@Qualifier("customXmlTemplateEngineWrapper") SpringTemplateEngineWrapper xmlTemplateEngineWrapper) {
		this.xmlTemplateEngine = xmlTemplateEngineWrapper.getTemplateEngine();

	}

	public String build(PmodeData pmode) {
		Context context = new Context();
		context.setVariable("pmode", pmode);

		String template = "pmode.xml";
		String result = xmlTemplateEngine.process(template, context);
		return result;
	}
}
