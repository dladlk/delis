package dk.erst.domibus.metrics.prometheus;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;

import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import com.codahale.metrics.MetricRegistry;

import io.prometheus.client.CollectorRegistry;
import io.prometheus.client.dropwizard.DropwizardExports;
import io.prometheus.client.exporter.MetricsServlet;

public class PrometheusMetricsServlet extends MetricsServlet {

	private static final long serialVersionUID = 7922253448362163590L;

	@Override
	public void init(ServletConfig config) throws ServletException {
		super.init(config);
		
		ServletContext servletContext = config.getServletContext();
		WebApplicationContext ctx = WebApplicationContextUtils.getWebApplicationContext(servletContext);
		MetricRegistry metricRegistry = ctx.getBean("metricRegistry", MetricRegistry.class);
		
		CollectorRegistry.defaultRegistry.register(new DropwizardExports(metricRegistry));
	}

}
