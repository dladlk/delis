package dk.erst.delis;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.jms.config.DefaultJmsListenerContainerFactory;
import org.springframework.jms.listener.AbstractPollingMessageListenerContainer;

import eu.domibus.api.property.DomibusPropertyProvider;
import eu.domibus.logging.DomibusLogger;
import eu.domibus.logging.DomibusLoggerFactory;

public class CustomJmsReceiveTimeoutSetPostProcessor implements BeanPostProcessor {

	protected static final DomibusLogger LOG = DomibusLoggerFactory.getLogger(CustomJmsReceiveTimeoutSetPostProcessor.class);
	
    static final Set<String> listeners = newHashSet("dispatchContainer",
            "controllerListener");
    static final Set<String> factories = newHashSet("internalJmsListenerContainerFactory",
            "alertJmsListenerContainerFactory",
            "alertJmsListenerContainerFactory",
            "uiReplicationJmsListenerContainerFactory",
            "pullJmsListenerContainerFactory");
    public static final String DOMIBUS_DISPATCHER_TIMEOUT = "domibus.dispatcher.timeout";

    @Autowired
    protected DomibusPropertyProvider domibusPropertyProvider;
    
    private Long timeoutProperty;
    private boolean timeoutPropertyInitialized;
    
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        return bean;
    }

	public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
		Long timeoutProperty = getTimeoutProperty();

		if (timeoutProperty != null) {
			if (listeners.contains(beanName) && bean instanceof AbstractPollingMessageListenerContainer) {
				AbstractPollingMessageListenerContainer abstractPollingMessageListenerContainer = (AbstractPollingMessageListenerContainer) bean;
				abstractPollingMessageListenerContainer.setReceiveTimeout(timeoutProperty);
				logReplaceFact(beanName);
			} else if (factories.contains(beanName) && bean instanceof DefaultJmsListenerContainerFactory) {
				DefaultJmsListenerContainerFactory defaultJmsListenerContainerFactory = (DefaultJmsListenerContainerFactory) bean;
				defaultJmsListenerContainerFactory.setReceiveTimeout(timeoutProperty);
				logReplaceFact(beanName);
			}
		}

		return bean;
	}
    
	private void logReplaceFact(String beanName) {
		LOG.info("Default receiveTimeout at bean " + beanName + " is overwritten with " + timeoutProperty);
	}

	protected Long getTimeoutProperty() {
		if (!this.timeoutPropertyInitialized) {
			String timeoutPropertyStr = domibusPropertyProvider.getProperty(DOMIBUS_DISPATCHER_TIMEOUT);
			if (timeoutPropertyStr != null && timeoutPropertyStr.length() > 0) {
				try {
					timeoutProperty = new Long(timeoutPropertyStr);
				} catch (Exception e) {
				}
			}
			this.timeoutPropertyInitialized = true;
		}
		return this.timeoutProperty;
	}
    
	@SafeVarargs
	private static <E> HashSet<E> newHashSet(E... elements) {
		HashSet<E> set = new HashSet<E>(Math.max(elements.length * 2, 16));
		Collections.addAll(set, elements);
		return set;
	}    
}
