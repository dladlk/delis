package dk.erst.delis.task.document.parse.cachingtransformerfactory;

import com.google.common.base.Strings;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.CacheStats;
import com.google.common.cache.LoadingCache;

import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamSource;

public class CachingTransformerFactory extends TransformerFactory {

    private static final String DELEGATE_CLAZZ_PROPERTY = CachingTransformerFactory.class.getName() + ".delegate";
    private static final String CACHE_SPEC_PROPERTY = CachingTransformerFactory.class.getName() + ".cache";
    private static final String CACHE_STATS_PROPERTY = CachingTransformerFactory.class.getName() + ".stats";

    private final TransformerFactory delegate;

    private final LoadingCache<SourceWrapper, Templates> templateCache;

    private final CacheLoader<SourceWrapper, Templates> cacheLoader = new CacheLoader<SourceWrapper, Templates>() {
        @Override
        public Templates load(SourceWrapper streamSource) throws Exception {
            return delegate.newTemplates(streamSource.getDelegate());
        }
    };

    public CachingTransformerFactory() {
        try {
            String delegateClazz = System.getProperty(DELEGATE_CLAZZ_PROPERTY);
            String cacheSpec = Strings.nullToEmpty(System.getProperty(CACHE_SPEC_PROPERTY));
            boolean cacheStats = Boolean.parseBoolean(System.getProperty(CACHE_STATS_PROPERTY));

            if (Strings.isNullOrEmpty(delegateClazz)) {
                throw new IllegalArgumentException("System property is not set: " + DELEGATE_CLAZZ_PROPERTY);
            }

            TransformerFactory delegate = (TransformerFactory) Class.forName(delegateClazz).newInstance();

            CacheBuilder cacheBuilder = CacheBuilder.from(cacheSpec);

            if(cacheStats) {
                cacheBuilder.recordStats();
            }

            this.delegate = delegate;
            this.templateCache = cacheBuilder.build(cacheLoader);
        } catch (InstantiationException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public CachingTransformerFactory(TransformerFactory delegate, CacheBuilder cacheBuilder) {
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
}
