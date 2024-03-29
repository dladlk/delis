package dk.erst.delis.task.organisation.setup;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;

import dk.erst.delis.common.util.StatData;
import dk.erst.delis.dao.OrganisationSetupDaoRepository;
import dk.erst.delis.data.entities.organisation.Organisation;
import dk.erst.delis.data.entities.organisation.OrganisationSetup;
import dk.erst.delis.data.enums.organisation.OrganisationSetupKey;
import dk.erst.delis.task.document.deliver.DocumentDeliverService;
import dk.erst.delis.task.email.EmailValidator;
import dk.erst.delis.task.organisation.setup.data.OrganisationReceivingFormatRule;
import dk.erst.delis.task.organisation.setup.data.OrganisationReceivingMethod;
import dk.erst.delis.task.organisation.setup.data.OrganisationSetupData;
import dk.erst.delis.task.organisation.setup.data.OrganisationSubscriptionProfileGroup;
import dk.erst.delis.vfs.service.VFSConfigException;
import dk.erst.delis.vfs.service.VFSService;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class OrganisationSetupService {

	private static final boolean VALIDATE_BY_UPLOAD = true;

	private OrganisationSetupDaoRepository organisationSetupDaoRepository;

	private VFSService vfsService;
	
	@Autowired
	public OrganisationSetupService(OrganisationSetupDaoRepository organisationSetupDaoRepository, VFSService vfsService) {
		this.organisationSetupDaoRepository = organisationSetupDaoRepository;
		this.vfsService = vfsService;
	}

	public OrganisationSetupData load(Organisation organisation) {
		if (organisation == null) {
			return null;
		}
		List<OrganisationSetup> setupList = organisationSetupDaoRepository.findAllByOrganisation(organisation);
		return convertSetupListToData(setupList, organisation);
	}
	
	public void validate(OrganisationSetupData data, BindingResult bindingResult, boolean validateReceivingSetup) {
		if (data.isOnErrorAutoSendEmailToSupplier()) {
			if (StringUtils.isBlank(data.getOnErrorSenderEmailAddress())) {
				bindingResult.rejectValue("onErrorSenderEmailAddress", "undefined", "Mandatory as automatic email sending on error is enabled.");
			}
			if (StringUtils.isBlank(data.getOnErrorReceiverEmailAddress())) {
				bindingResult.rejectValue("onErrorReceiverEmailAddress", "undefined", "Mandatory as automatic email sending on error is enabled.");
			}
		}
		if (StringUtils.isNotBlank(data.getOnErrorSenderEmailAddress())) {
			StringBuilder newValue = new StringBuilder();
			validateEmailValue(bindingResult, data.getOnErrorSenderEmailAddress(), newValue, "onErrorSenderEmailAddress", false);
			data.setOnErrorSenderEmailAddress(newValue.toString());
		}
		if (StringUtils.isNotBlank(data.getOnErrorReceiverEmailAddress())) {
			StringBuilder newValue = new StringBuilder();
			validateEmailValue(bindingResult, data.getOnErrorReceiverEmailAddress(), newValue, "onErrorReceiverEmailAddress", true);
			data.setOnErrorReceiverEmailAddress(newValue.toString());
		}
		
		if (data.isReceiveBothOIOUBLBIS3() && data.getReceivingFormatRule() != OrganisationReceivingFormatRule.OIOUBL) {
			bindingResult.rejectValue("receiveBothOIOUBLBIS3", "undefined", "Receiving of both OIOUBL and BIS3 format can be selected only if receiving format is set to OIOUBL.");
		}
		
		if (data.getReceivingMethod() != null) {
			if (StringUtils.isBlank(data.getReceivingMethodSetup())){
				bindingResult.rejectValue("receivingMethodSetup", "undefined", "When receiving method is selected, setup line should be filled.");
			} else {
				File configFile = new File(data.getReceivingMethodSetup());
				if (data.getReceivingMethod() == OrganisationReceivingMethod.FILE_SYSTEM) {
					if (!configFile.exists() || !configFile.isDirectory()) {
						bindingResult.rejectValue("receivingMethodSetup", "undefined", "Path does not exists or is not a directory.");
					} else {
						if (validateReceivingSetup) {
							if (data.isReceiveBothOIOUBLBIS3()) {
								validateSubfolder(bindingResult, configFile, DocumentDeliverService.DELIVER_FOLDER_BIS3);
								validateSubfolder(bindingResult, configFile, DocumentDeliverService.DELIVER_FOLDER_OIOUBL);
							}							
						}
					}
				} else {
					if (!configFile.exists() || !configFile.isFile()) {
						bindingResult.rejectValue("receivingMethodSetup", "undefined", "Path does not exists or is not a file with VFS setup.");
					} else {
						try {
							vfsService.getConfig(configFile.getAbsolutePath());
							if (validateReceivingSetup) {
								validateVFSFolder(bindingResult, data.getReceivingMethodSetup(), "/", "Accessibility check of configured receiving method failed.");
								if (data.isReceiveBothOIOUBLBIS3()) {
									validateVFSSubfolder(bindingResult, data.getReceivingMethodSetup(), "/" + DocumentDeliverService.DELIVER_FOLDER_BIS3);
									validateVFSSubfolder(bindingResult, data.getReceivingMethodSetup(), "/" + DocumentDeliverService.DELIVER_FOLDER_OIOUBL);
								}								
							}
						} catch (Exception e) {
							bindingResult.rejectValue("receivingMethodSetup", "undefined", "VFS setup cannot be loaded: "+e.getMessage());
						}
					}
				}
			}
		}
	}
	
	private void validateVFSSubfolder(BindingResult bindingResult, String setup, String folderName) {
		validateVFSFolder(bindingResult, setup, folderName, "Receiving of both BIS3 and OIOUBL is configured, but subfolder " + folderName + " does not exists or is not a directory.");		
	}
	
	private void validateVFSFolder(BindingResult bindingResult, String setup, String path, String messageNotExists) {
		if (VALIDATE_BY_UPLOAD) {
			validateVFSFolderByUpload(bindingResult, setup, path, messageNotExists);
			return;
		}
		try {
			if (!vfsService.exist(setup, path)) {
				bindingResult.rejectValue("receivingMethodSetup", "undefined", messageNotExists);
			}
		} catch (VFSConfigException e) {
			bindingResult.rejectValue("receivingMethodSetup", "undefined", "VFS setup XML file is not valid: " + e.getMessage());
		} catch (Exception e) {
			bindingResult.rejectValue("receivingMethodSetup", "undefined", messageNotExists + ": " + e.getMessage());
		}
	}
	
	/*
	 * Not yet used
	 */
	protected void validateVFSFolderByUpload(BindingResult bindingResult, String setup, String path, String messageNotExists) {
		File tempFile = null;
		try {
			log.info("Validating VFS folder " + path + " by config " + setup);
			tempFile = File.createTempFile("validateVFSFolder_" + System.currentTimeMillis(), ".tmp");
			try (FileOutputStream fos = new FileOutputStream(tempFile)) {
				IOUtils.write("validateVFSFolder".getBytes(), fos);
			}
			String remoteFilePath = path + "/" + tempFile.getName();
			vfsService.upload(setup, tempFile.getAbsolutePath(), remoteFilePath);
			log.info("Successfully uploaded temp file to "+remoteFilePath);
			if (!vfsService.exist(setup, remoteFilePath)) {
				log.warn("File does not exist: "+remoteFilePath);
				bindingResult.rejectValue("receivingMethodSetup", "undefined", messageNotExists + ": temporary uploaded file does not exist: " + remoteFilePath);
			} else {
				log.info("File exists: "+remoteFilePath);
			}
			if (!vfsService.delete(setup, remoteFilePath)) {
				log.warn("Cannot delete temp file: "+remoteFilePath);
				bindingResult.rejectValue("receivingMethodSetup", "undefined", messageNotExists + ": cannot delete temporary uploaded file " + remoteFilePath);
			} else {
				log.info("Successfully deleted temp file: " + remoteFilePath);
			}
		} catch (VFSConfigException e) {
			bindingResult.rejectValue("receivingMethodSetup", "undefined", "VFS setup XML file is not valid: " + e.getMessage());
		} catch (Exception e) {
			bindingResult.rejectValue("receivingMethodSetup", "undefined", messageNotExists + ": " + e.getMessage());
		} finally {
			if (tempFile != null) {
				if (!tempFile.delete()) {
					log.error("Failed to delete temporary file, generated for VFS access validation: " + tempFile);
				}
			}
		}
	}	

	private void validateSubfolder(BindingResult bindingResult, File configFile, String subPath) {
		File subFolder = new File(configFile, subPath);
		if (!subFolder.exists() || !subFolder.isDirectory()) {
			bindingResult.rejectValue("receivingMethodSetup", "undefined", "Receiving of both BIS3 and OIOUBL is configured, but subfolder " + subPath + " does not exists or is not a directory.");
		}
	}

	private void validateEmailValue(BindingResult bindingResult, String value, StringBuilder newValue, String code, boolean allowMultiple) {
		value = value.trim();
		String[] values = new String[] {value};
		if (allowMultiple) {
			if (value.contains(";")) {
				values = value.split(";");
			}
		}
		for (String email : values) {
			email = email.trim();
			if (StringUtils.isNotBlank(email)) {
				if (newValue.length() > 0) {
					newValue.append(";");
				}
				newValue.append(email);
				if (!EmailValidator.isValidEmail(email)) {
					bindingResult.rejectValue(code, "undefined", "Email address '"+email+"' is not valid.");
				}
			}
		}
	}

	public StatData update(OrganisationSetupData data) {
		if (data == null) {
			return null;
		}
		StatData sd = new StatData();
		Map<OrganisationSetupKey, String> newDataToSetupMap = convertDataToSetupMap(data);
		List<OrganisationSetup> currentSetupList = organisationSetupDaoRepository.findAllByOrganisation(data.getOrganisation());
		
		/*
		 * Cleanup unsupported keys to avoid Duplicated Key exception
		 */
		for (Iterator<OrganisationSetup> iterator = currentSetupList.iterator(); iterator.hasNext();) {
			OrganisationSetup organisationSetup = iterator.next();
			if (organisationSetup.getKey() == null) {
				iterator.remove();
			}
		}
		
		Map<OrganisationSetupKey, OrganisationSetup> currentSetupMap = currentSetupList.stream().collect(Collectors.toMap(OrganisationSetup::getKey, Function.identity()));

		if (log.isDebugEnabled()) {
			log.debug("Build newDataMap: " + newDataToSetupMap);
			log.debug("Current detup map: " + currentSetupMap);
		}
		List<OrganisationSetupKey> changedFields = new ArrayList<>();
		for (OrganisationSetupKey key : newDataToSetupMap.keySet()) {
			String newValue = newDataToSetupMap.get(key);
			if (currentSetupMap.containsKey(key)) {
				OrganisationSetup os = currentSetupMap.get(key);
				if (!newValue.equals(os.getValue())) {
					os.setValue(newValue);
					organisationSetupDaoRepository.save(os);
					sd.increment("updated");
					changedFields.add(key);
				} else {
					sd.increment("unchanged");
				}
			} else {
				OrganisationSetup os = new OrganisationSetup();
				os.setKey(key);
				os.setOrganisation(data.getOrganisation());
				os.setValue(newValue);
				organisationSetupDaoRepository.save(os);
				sd.increment("created");
				changedFields.add(key);
			}
		}

		for (OrganisationSetupKey key : currentSetupMap.keySet()) {
			if (!newDataToSetupMap.containsKey(key)) {
				OrganisationSetup os = currentSetupMap.get(key);
				organisationSetupDaoRepository.delete(os);
				sd.increment("deleted");
				changedFields.add(key);
			}
		}
		
		sd.setResult(changedFields);
		
		return sd;
	}

/*	private Map<OrganisationSetupKey, String> buildDefaultMap() {
		Map<OrganisationSetupKey, String> m = new HashMap<>();
		m.put(OrganisationSetupKey.RECEIVING_DATA_FORMAT, OrganisationReceivingFormatRule.OIOUBL.getCode());
		m.put(OrganisationSetupKey.RECEIVING_METHOD, OrganisationReceivingMethod.FILE_SYSTEM.getCode());
		m.put(OrganisationSetupKey.RECEIVING_METHOD_SETUP, "/delis/output");
		m.put(OrganisationSetupKey.SUBSCRIBED_SMP_PROFILES, OrganisationSubscriptionProfileGroup.BIS3.getCode());
		return m;
	}*/

	protected Map<OrganisationSetupKey, String> convertDataToSetupMap(OrganisationSetupData d) {
		Map<OrganisationSetupKey, String> m = new HashMap<>();
		if (d == null) {
			return m;
		}

		if (d.getReceivingFormatRule() != null) {
			m.put(OrganisationSetupKey.RECEIVING_DATA_FORMAT, d.getReceivingFormatRule().getCode());
		}
		if (d.getReceivingMethod() != null) {
			m.put(OrganisationSetupKey.RECEIVING_METHOD, d.getReceivingMethod().getCode());
		}
		if (d.getReceivingMethodSetup() != null) {
			m.put(OrganisationSetupKey.RECEIVING_METHOD_SETUP, d.getReceivingMethodSetup().trim());
		}
		m.put(OrganisationSetupKey.SMP_INTEGRATION, d.isSmpIntegrationPublish() ? "PUBLISH": "");
		if (d.getSubscribeProfileSet() != null) {
			String joined = d.getSubscribeProfileSet().stream().map(OrganisationSubscriptionProfileGroup::getCode).collect(Collectors.joining(","));
			m.put(OrganisationSetupKey.SUBSCRIBED_SMP_PROFILES, joined);
		}
		if (d.getAs2() != null) {
			m.put(OrganisationSetupKey.ACCESS_POINT_AS2, String.valueOf(d.getAs2()));
		}
		if (d.getAs4() != null) {
			m.put(OrganisationSetupKey.ACCESS_POINT_AS4, String.valueOf(d.getAs4()));
		}
		m.put(OrganisationSetupKey.GENERATE_RESPONSE_ON_ERROR, String.valueOf(d.isGenerateInvoiceResponseOnError()));
		m.put(OrganisationSetupKey.SEND_UNDELIVERABLE_RESPONSE_TO_ERST, String.valueOf(d.isSendUndeliverableInvoiceResponseToERST()));
		
		m.put(OrganisationSetupKey.ON_ERROR_AUTO_SEND_EMAIL_SUPPLIER, String.valueOf(d.isOnErrorAutoSendEmailToSupplier()));
		m.put(OrganisationSetupKey.ON_ERROR_SENDER_EMAIL_ADDRESS, String.valueOf(d.getOnErrorSenderEmailAddress()));
		m.put(OrganisationSetupKey.ON_ERROR_RECEIVER_EMAIL_ADDRESS, String.valueOf(d.getOnErrorReceiverEmailAddress()));
		
		m.put(OrganisationSetupKey.RECEIVE_BOTH_BIS3_AND_OIOUBL, String.valueOf(d.isReceiveBothOIOUBLBIS3()));

		m.put(OrganisationSetupKey.CHECK_DELIVERED_CONSUMED, String.valueOf(d.isCheckDeliveredConsumed()));

		if (d.getCheckDeliveredAlertMins() > 0) {
			m.put(OrganisationSetupKey.CHECK_DELIVERED_ALERT_AFTER_MIN, String.valueOf(d.getCheckDeliveredAlertMins()));
		}
		return m;
	}

	protected OrganisationSetupData convertSetupListToData(List<OrganisationSetup> setupList, Organisation organisation) {
		OrganisationSetupData d = new OrganisationSetupData();
		d.setOrganisation(organisation);
		if (setupList != null) {
			for (OrganisationSetup os : setupList) {
				if (os.getKey() == null) {
					// Skip unsupported outdated setup options
					continue;
				}
				switch (os.getKey()) {
				case SMP_INTEGRATION:
					d.setSmpIntegrationPublish("PUBLISH".equalsIgnoreCase(os.getValue()));
					break;
				case SUBSCRIBED_SMP_PROFILES:
					String[] profiles = os.getValue().split(",");

					Set<OrganisationSubscriptionProfileGroup> set = new HashSet<>();
					for (int i = 0; i < profiles.length; i++) {
						if (!profiles[i].isEmpty()) {
							OrganisationSubscriptionProfileGroup profileGroup = OrganisationSubscriptionProfileGroup.valueOf(profiles[i]);
							if (profileGroup != null) {
								set.add(profileGroup);
							}
						}
					}
					d.setSubscribeProfileSet(set);
					break;
				case RECEIVING_DATA_FORMAT:
					d.setReceivingFormatRule(OrganisationReceivingFormatRule.valueOf(os.getValue()));
					break;
				case RECEIVING_METHOD:
					d.setReceivingMethod(OrganisationReceivingMethod.getInstance(os.getValue()));
					break;
				case RECEIVING_METHOD_SETUP:
					d.setReceivingMethodSetup(os.getValue());
					break;
				case ACCESS_POINT_AS2:
					d.setAs2(toId(os.getValue()));
					break;
				case ACCESS_POINT_AS4:
					d.setAs4(toId(os.getValue()));
					break;
				case GENERATE_RESPONSE_ON_ERROR:
					d.setGenerateInvoiceResponseOnError(Boolean.valueOf(os.getValue()));
					break;
				case SEND_UNDELIVERABLE_RESPONSE_TO_ERST:
					d.setSendUndeliverableInvoiceResponseToERST(Boolean.valueOf(os.getValue()));
					break;
				case ON_ERROR_AUTO_SEND_EMAIL_SUPPLIER:
					d.setOnErrorAutoSendEmailToSupplier(Boolean.valueOf(os.getValue()));
					break;
				case ON_ERROR_SENDER_EMAIL_ADDRESS:
					d.setOnErrorSenderEmailAddress(os.getValue());
					break;
				case ON_ERROR_RECEIVER_EMAIL_ADDRESS:
					d.setOnErrorReceiverEmailAddress(os.getValue());
					break;
				case RECEIVE_BOTH_BIS3_AND_OIOUBL:
					d.setReceiveBothOIOUBLBIS3(Boolean.valueOf(os.getValue()));
					break;
				case CHECK_DELIVERED_CONSUMED:
					d.setCheckDeliveredConsumed(Boolean.valueOf(os.getValue()));
					break;
				case CHECK_DELIVERED_ALERT_AFTER_MIN:
					if (!StringUtils.isEmpty(os.getValue()) && StringUtils.isNumeric(os.getValue())) {
						d.setCheckDeliveredAlertMins(Integer.valueOf(os.getValue()));
					}
					break;
				default:
					break;
				}
			}
		}
		if (d.getSubscribeProfileSet() == null) {
			d.setSubscribeProfileSet(Collections.emptySet());
		}
		if (d.getReceivingFormatRule() == null) {
			d.setReceivingFormatRule(OrganisationReceivingFormatRule.OIOUBL);
		}
		return d;
	}

	private Long toId(String value) {
		Long id = null;
		if (value != null && StringUtils.isNumeric(value)) {
			id = Long.parseLong(value);
		}
		return id;
	}

	public List<Long> loadOrganisationIdWithSetup(OrganisationSetupKey criteriaKey, String criteriaValue) {
		OrganisationSetup probe = new OrganisationSetup();
		probe.setKey(criteriaKey);
		probe.setValue(criteriaValue);
		probe.setUpdateTime(null);
		probe.setCreateTime(null);
		Example<OrganisationSetup> example = Example.of(probe);
		Iterable<OrganisationSetup> setupIterable = organisationSetupDaoRepository.findAll(example);
		
		List<Long> res = new ArrayList<Long>(); 
		for (OrganisationSetup organisationSetup : setupIterable) {
			res.add(organisationSetup.getOrganisation().getId());
		}
		return res;
	}
}
