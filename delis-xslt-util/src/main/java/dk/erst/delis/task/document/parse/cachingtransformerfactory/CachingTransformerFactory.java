package dk.erst.delis.task.document.parse.cachingtransformerfactory;

import javax.xml.transform.ErrorListener;
import javax.xml.transform.Source;
import javax.xml.transform.Templates;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.URIResolver;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamSource;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.CacheStats;
import com.google.common.cache.LoadingCache;

public class CachingTransformerFactory extends TransformerFactory {

    private static final String delegateClassName = "net.sf.saxon.TransformerFactoryImpl";
    private static final String cacheSpec = "initialCapacity=50,maximumSize=100";
    private static final boolean cacheStats = true;

    private final TransformerFactory delegate;

    private final LoadingCache<SourceWrapper, Templates> templateCache;
    
    private static CachingTransformerFactory INSTANCE = null;

    private final CacheLoader<SourceWrapper, Templates> cacheLoader = new CacheLoader<SourceWrapper, Templates>() {
        @Override
        public Templates load(SourceWrapper streamSource) throws Exception {
            return delegate.newTemplates(streamSource.getDelegate());
        }
    };
    
	public static TransformerFactory getInstance() {
		if (INSTANCE == null) {
			CachingTransformerFactory inst = new CachingTransformerFactory();
			synchronized (CachingTransformerFactory.class) {
				if (INSTANCE == null) {
					INSTANCE = inst;
				}
			}
		}
		return INSTANCE;
	}

    private CachingTransformerFactory() {
        try {
            TransformerFactory delegate = (TransformerFactory) Class.forName(delegateClassName).newInstance();
            CacheBuilder<Object, Object> cacheBuilder = CacheBuilder.from(cacheSpec);
            if(cacheStats) {
                cacheBuilder.recordStats();
            }
            this.delegate = delegate;
            this.templateCache = cacheBuilder.build(cacheLoader);
        } catch (InstantiationException | ClassNotFoundException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public CachingTransformerFactory(TransformerFactory delegate, CacheBuilder<Object, Object> cacheBuilder) {
        this.delegate = delegate;
        this.templateCache = cacheBuilder.build(cacheLoader);
    }

    @Override
    public Transformer newTransformer(Source source) throws TransformerConfigurationException {
        return newTemplates(source).newTransformer();
    }

    @Override
    public Transformer newTransformer() throws TransformerConfigurationException {
        return delegate.newTransformer();
    }

    @Override
    public Templates newTemplates(Source source) throws TransformerConfigurationException {
        if (source instanceof StreamSource || source instanceof DOMSource) {
            SourceWrapper sourceWrapper = new SourceWrapper(source);

            if (sourceWrapper.isCacheable()) {
                return templateCache.getUnchecked(sourceWrapper);
            }
        }
        return delegate.newTemplates(source);
    }

    @Override
    public Source getAssociatedStylesheet(Source source, String media, String title, String charset) throws TransformerConfigurationException {
        return delegate.getAssociatedStylesheet(source, media, title, charset);
    }

    @Override
    public void setURIResolver(URIResolver resolver) {
        delegate.setURIResolver(resolver);
    }

    @Override
    public URIResolver getURIResolver() {
        return delegate.getURIResolver();
    }

    @Override
    public void setFeature(String name, boolean value) throws TransformerConfigurationException {
        delegate.setFeature(name, value);
    }

    @Override
    public boolean getFeature(String name) {
        return delegate.getFeature(name);
    }

    @Override
    public void setAttribute(String name, Object value) {
        delegate.setAttribute(name, value);
    }

    @Override
    public Object getAttribute(String name) {
        return delegate.getAttribute(name);
    }

    @Override
    public void setErrorListener(ErrorListener listener) {
        delegate.setErrorListener(listener);
    }

    @Override
    public ErrorListener getErrorListener() {
        return delegate.getErrorListener();
    }

    public CacheStats stats() {
        return templateCache.stats();
    }
    
    public void flushCache() {
    	this.templateCache.invalidateAll();
    }
}
