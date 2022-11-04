package dk.erst.domibus.metrics.prometheus;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;

import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import com.codahale.metrics.MetricRegistry;

import eu.domibus.logging.DomibusLogger;
import eu.domibus.logging.DomibusLoggerFactory;
import io.prometheus.client.CollectorRegistry;
import io.prometheus.client.dropwizard.DropwizardExports;
import io.prometheus.client.exporter.MetricsServlet;

@WebServlet("/prometheus")
public class PrometheusMetricsServlet extends MetricsServlet {

	private static final long serialVersionUID = 7922253448362163590L;
	
	protected static final DomibusLogger LOG = DomibusLoggerFactory.getLogger(PrometheusMetricsServlet.class);

	@Override
	public void init(ServletConfig config) throws ServletException {
		super.init(config);
		
		LOG.warn("Start PrometheusMetricsServlet initialization...");
		
		ServletContext servletContext = config.getServletContext();
		WebApplicationContext ctx = WebApplicationContextUtils.getWebApplicationContext(servletContext);
		MetricRegistry metricRegistry = ctx.getBean("metricRegistry", MetricRegistry.class);
		
		CollectorRegistry.defaultRegistry.register(new DropwizardExports(metricRegistry));
		
		LOG.warn("Done PrometheusMetricsServlet initialization - registered Dropwizard collector");
	}

}
