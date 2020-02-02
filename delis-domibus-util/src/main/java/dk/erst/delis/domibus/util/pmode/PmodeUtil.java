package dk.erst.delis.domibus.util.pmode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import dk.erst.delis.domibus.util.pmode.PmodeData.Service;

public class PmodeUtil {
	
	private static String buildServiceKey(String type, String value) {
		return type + "\t" + value;
	}
	
	private static String buildServiceKey(Service service) {
		return buildServiceKey(service.getType(), service.getValue());
	}
	
    public static PmodeData populateServicesActionsLegs(PmodeData pmode) {
        PModeProfileGroup[] values = PModeProfileGroup.values();

        List<PmodeData.Service> services = new ArrayList<>();
        List<PmodeData.Action> actions = new ArrayList<>();
        List<PmodeData.Leg> legs = new ArrayList<>();

        Map<String, PmodeData.Service> serviceMap = new HashMap<>();
        for (PModeProfileGroup pModeProfileGroup : values) {
        	String code = pModeProfileGroup.getCode();

        	PmodeData.Service service = serviceMap.get(buildServiceKey(pModeProfileGroup.getProcessId(), pModeProfileGroup.getProcessType()));
            PmodeData.Service serviceCen = serviceMap.get(buildServiceKey(pModeProfileGroup.getProcessSchemeSMP() + "::" + pModeProfileGroup.getProcessId(), pModeProfileGroup.getProcessType()));
            PmodeData.Service serviceType = serviceMap.get(buildServiceKey(pModeProfileGroup.getProcessId(), pModeProfileGroup.getProcessSchemeSMP()));
            if (service == null) {
                service = new PmodeData.Service(code, pModeProfileGroup.getProcessId(), pModeProfileGroup.getProcessType());
                serviceCen = new PmodeData.Service(code + "Emb", pModeProfileGroup.getProcessSchemeSMP() + "::" + service.getValue(), service.getType());
                serviceType = new PmodeData.Service(code + "TB", service.getValue(), pModeProfileGroup.getProcessSchemeSMP());
                
                serviceMap.put(buildServiceKey(service), service);
                serviceMap.put(buildServiceKey(serviceCen), serviceCen);
                serviceMap.put(buildServiceKey(serviceType), serviceType);
            }

            String[] documentIdentifiers = pModeProfileGroup.getDocumentIdentifiers();
            
            for (int i = 0; i < documentIdentifiers.length; i++) {
            	String docType = documentIdentifiers.length > 1 ? (i == 0? "_Invoice" : "_CreditNote"): "";
                PmodeData.Action action = new PmodeData.Action(code + docType, documentIdentifiers[i]);
                actions.add(action);
                PmodeData.Action actionQns = new PmodeData.Action(code + docType+"Qns", "busdox-docid-qns::" + documentIdentifiers[i]);
                actions.add(actionQns);

                legs.add(new PmodeData.Leg(action.getName(), service.getName(), action.getName()));
                legs.add(new PmodeData.Leg(actionQns.getName(), service.getName(), actionQns.getName()));
                
                legs.add(new PmodeData.Leg(action.getName()+"Emb", serviceCen.getName(), action.getName()));
                legs.add(new PmodeData.Leg(actionQns.getName()+"Emb", serviceCen.getName(), actionQns.getName()));

                legs.add(new PmodeData.Leg(action.getName()+"TB", serviceType.getName(), action.getName()));
                legs.add(new PmodeData.Leg(actionQns.getName()+"TB", serviceType.getName(), actionQns.getName()));
            }
        }

        for (PmodeData.Service service : serviceMap.values()) {
            services.add(service);
        }

        pmode.setServiceList(services.stream().sorted((e1, e2) -> e1.getName().compareTo(e2.getName())).collect(Collectors.toList()));
        pmode.setActionList(actions.stream().sorted((e1, e2) -> e1.getName().compareTo(e2.getName())).collect(Collectors.toList()));
        pmode.setLegList(legs.stream().sorted((e1, e2) -> e1.getName().compareTo(e2.getName())).collect(Collectors.toList()));

        return pmode;
    }

}
