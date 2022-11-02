package eu.domibus.plugin.fs.configuration;

import java.util.Arrays;
import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import eu.domibus.common.NotificationType;
import eu.domibus.ext.services.DomibusPropertyExtService;
import eu.domibus.logging.DomibusLogger;
import eu.domibus.logging.DomibusLoggerFactory;
import eu.domibus.plugin.fs.DelisFSPluginImpl;
import eu.domibus.plugin.fs.FSPluginImpl;
import eu.domibus.plugin.fs.property.FSPluginPropertiesMetadataManagerImpl;

@Configuration("delisFSPluginFactory")
public class DelisFSPluginConfiguration {

	private static final DomibusLogger LOG = DomibusLoggerFactory.getLogger(DelisFSPluginConfiguration.class);

    protected List<NotificationType> defaultMessageNotifications = Arrays.asList(
            NotificationType.MESSAGE_RECEIVED, NotificationType.MESSAGE_SEND_FAILURE, NotificationType.MESSAGE_RECEIVED_FAILURE,
            NotificationType.MESSAGE_SEND_SUCCESS, NotificationType.MESSAGE_STATUS_CHANGE);
	
    @Primary
    @Bean("backendFSPlugin")
	public FSPluginImpl createFSPlugin(DomibusPropertyExtService domibusPropertyExtService) {
		List<NotificationType> messageNotifications = domibusPropertyExtService.getConfiguredNotifications(FSPluginPropertiesMetadataManagerImpl.MESSAGE_NOTIFICATIONS);
		LOG.debug("Using the following message notifications [{}]", messageNotifications);
		if (!messageNotifications.containsAll(defaultMessageNotifications)) {
			LOG.warn("FSPlugin will not function properly if the following message notifications will not be set: [{}]", defaultMessageNotifications);
		}

		LOG.error("Use DelisFSPluginImpl instead of standard FSPluginImpl");
		
		FSPluginImpl fsPlugin = new DelisFSPluginImpl();
		fsPlugin.setRequiredNotifications(messageNotifications);
		return fsPlugin;
	}

}
