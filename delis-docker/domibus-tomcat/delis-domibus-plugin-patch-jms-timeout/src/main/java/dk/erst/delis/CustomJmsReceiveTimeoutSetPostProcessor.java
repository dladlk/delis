package dk.erst.delis;

import com.google.common.collect.Sets;
import eu.domibus.api.property.DomibusPropertyProvider;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.jms.config.DefaultJmsListenerContainerFactory;
import org.springframework.jms.listener.AbstractPollingMessageListenerContainer;

import java.util.Set;

public class CustomJmsReceiveTimeoutSetPostProcessor implements BeanPostProcessor {

    static final Set<String> listeners = Sets.newHashSet("dispatchContainer",
            "controllerListener");
    static final Set<String> factories = Sets.newHashSet("internalJmsListenerContainerFactory",
            "alertJmsListenerContainerFactory",
            "alertJmsListenerContainerFactory",
            "uiReplicationJmsListenerContainerFactory",
            "pullJmsListenerContainerFactory");
    public static final String DOMIBUS_DISPATCHER_TIMEOUT = "domibus.dispatcher.timeout";

    @Autowired
    protected DomibusPropertyProvider domibusPropertyProvider;

    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        return bean;
    }

    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {

        String timeoutProperty = domibusPropertyProvider.getProperty(DOMIBUS_DISPATCHER_TIMEOUT);

        if (timeoutProperty != null && timeoutProperty.length() > 0) {
            try {
                long receiveTimeout = 0;
                receiveTimeout = new Long(timeoutProperty);
                if (receiveTimeout > 0) {
                    if (listeners.contains(beanName) && bean instanceof AbstractPollingMessageListenerContainer) {
                        ((AbstractPollingMessageListenerContainer)bean).setReceiveTimeout(receiveTimeout);
                    } else if (factories.contains(beanName) && bean instanceof DefaultJmsListenerContainerFactory) {
                        ((DefaultJmsListenerContainerFactory)bean).setReceiveTimeout(receiveTimeout);
                    }
                }
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        }

        return bean;
    }
}
