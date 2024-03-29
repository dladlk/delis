package dk.erst.delis.task.document.parse.cachingtransformerfactory;

import com.google.common.base.Strings;

import javax.xml.transform.Source;

class SourceWrapper {

    private final Source delegate;

    SourceWrapper(Source delegate) {
        this.delegate = delegate;
    }

    public boolean isCacheable() {
        return Strings.nullToEmpty(getDelegate().getSystemId()).length() > 0;
    }

    @Override
    public int hashCode() {
        if (isCacheable()) {
            return getDelegate().getSystemId().hashCode();
        } else {
            return getDelegate().hashCode();
        }
    }

    @Override
    public boolean equals(Object other) {
        if (!(other instanceof SourceWrapper)) {
            return false;
        }

        SourceWrapper otherWrapper = (SourceWrapper) other;
        if (isCacheable() && otherWrapper.isCacheable()) {
            return getDelegate().getSystemId().equals(otherWrapper.getDelegate().getSystemId());
        } else {
            return false;
        }
    }

    public Source getDelegate() {
        return delegate;
    }
}
