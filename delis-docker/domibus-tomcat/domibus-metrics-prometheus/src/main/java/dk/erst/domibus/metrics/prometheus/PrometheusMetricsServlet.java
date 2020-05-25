package dk.erst.domibus.metrics.prometheus;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;

import eu.domibus.common.metrics.MetricsHelper;
import io.prometheus.client.CollectorRegistry;
import io.prometheus.client.dropwizard.DropwizardExports;
import io.prometheus.client.exporter.MetricsServlet;

public class PrometheusMetricsServlet extends MetricsServlet {

	private static final long serialVersionUID = 7922253448362163590L;

	@Override
	public void init(ServletConfig config) throws ServletException {
		super.init(config);
		
		CollectorRegistry.defaultRegistry.register(new DropwizardExports(MetricsHelper.getMetricRegistry()));
	}

}
