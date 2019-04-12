package dk.erst.delis.task.document.parse.cachingtransformerfactory;

import dk.erst.delis.config.ConfigBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.xml.transform.*;

@Component
public final class DelisTransformerFactory extends TransformerFactory {

	private static ConfigBean configBean;

	private static TransformerFactory INSTANCE;

	@Autowired
	public DelisTransformerFactory(ConfigBean configBean) {
		DelisTransformerFactory.configBean = configBean;
	}

	public static TransformerFactory getInstance() {
		if (INSTANCE == null) {
			/*
			 * Enable cache in all tests by default
			 */
			boolean cacheEnabled = configBean != null ? configBean.getXsltCacheEnabled() : true;
			INSTANCE = newInstance(cacheEnabled);
		}
		return INSTANCE;
	}

	public static TransformerFactory newInstance(boolean cacheEnabled) {
		if (cacheEnabled) {
			return new CachingTransformerFactory();
		} else {
			return TransformerFactory.newInstance();
		}
	}

	@Override
	public Transformer newTransformer(Source source) throws TransformerConfigurationException {
		return getInstance().newTransformer(source);
	}

	@Override
	public Transformer newTransformer() throws TransformerConfigurationException {
		return getInstance().newTransformer();
	}

	@Override
	public Templates newTemplates(Source source) throws TransformerConfigurationException {
		return getInstance().newTemplates(source);
	}

	@Override
	public Source getAssociatedStylesheet(Source source, String media, String title, String charset) throws TransformerConfigurationException {
		return getInstance().getAssociatedStylesheet(source, media, title, charset);
	}

	@Override
	public void setURIResolver(URIResolver resolver) {
		getInstance().setURIResolver(resolver);
	}

	@Override
	public URIResolver getURIResolver() {
		return getInstance().getURIResolver();
	}

	@Override
	public void setFeature(String name, boolean value) throws TransformerConfigurationException {
		getInstance().setFeature(name, value);
	}

	@Override
	public boolean getFeature(String name) {
		return getInstance().getFeature(name);
	}

	@Override
	public void setAttribute(String name, Object value) {
		getInstance().setAttribute(name, value);
	}

	@Override
	public Object getAttribute(String name) {
		return getInstance().getAttribute(name);
	}

	@Override
	public void setErrorListener(ErrorListener listener) {
		getInstance().setErrorListener(listener);
	}

	@Override
	public ErrorListener getErrorListener() {
		return getInstance().getErrorListener();
	}
}
