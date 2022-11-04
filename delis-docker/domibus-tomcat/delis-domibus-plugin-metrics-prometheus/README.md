Domibus Prometheus Metrics Plugin
===============

Exposes DropWizard Metrics for Prometheus by /prometheus URL.


# Deployment

It is implemented not as a plugin (so not deployed to /plugins/lib folder), but just as a Domibus dependency directly 
to WEB-INF/lib folder, see Dockerfile for domibus-tomcat

# Details

For Domibus version 5.0, there is an unfinished attempt to run it as a plugin - but not succeeded, so annotation
@WebServlet("/prometheus") in PrometheusMetricsServlet is actually not used, also as config\delis-domibus-prometheus-plugin-domibusServlet.xml.

So it still works via META-INF/web-fragment.xml like in Domibus 4.0 and earlier.