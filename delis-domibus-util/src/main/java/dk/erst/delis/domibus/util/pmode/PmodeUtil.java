package dk.erst.delis.domibus.util.pmode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import dk.erst.delis.domibus.util.pmode.PmodeData.Service;

public class PmodeUtil {
	
	private static String buildServiceKey(String value, String type) {
		return type + "\t" + value;
	}
	
	private static String buildServiceKey(Service service) {
		return buildServiceKey(service.getValue(), service.getType());
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
            PmodeData.Service serviceEmb = serviceMap.get(buildServiceKey(pModeProfileGroup.getProcessSchemeSMP() + "::" + pModeProfileGroup.getProcessId(), pModeProfileGroup.getProcessType()));
            PmodeData.Service serviceTB = serviceMap.get(buildServiceKey(pModeProfileGroup.getProcessId(), pModeProfileGroup.getProcessSchemeSMP()));
            if (service == null) {
                service = new PmodeData.Service(code, pModeProfileGroup.getProcessId(), pModeProfileGroup.getProcessType());
                serviceMap.put(buildServiceKey(service), service);
            }
            if (serviceEmb == null) {
                serviceEmb = new PmodeData.Service(code + "Emb", pModeProfileGroup.getProcessSchemeSMP() + "::" + service.getValue(), service.getType());
                serviceMap.put(buildServiceKey(serviceEmb), serviceEmb);
            }
            if (serviceTB == null) {
                serviceTB = new PmodeData.Service(code + "TB", service.getValue(), pModeProfileGroup.getProcessSchemeSMP());
                serviceMap.put(buildServiceKey(serviceTB), serviceTB);
            }

            String[] documentIdentifiers = pModeProfileGroup.getDocumentIdentifiers();
            
            for (int i = 0; i < documentIdentifiers.length; i++) {
            	String docType = "";
            	if (documentIdentifiers.length > 1) {
            		String di = documentIdentifiers[i];
            		int startRootTag = di.indexOf("::") + 2;
            		int endRootTag = di.indexOf("##", startRootTag);
            		docType = di.substring(startRootTag, endRootTag);
            		docType = "_" + docType;
            	}
                PmodeData.Action action = new PmodeData.Action(code + docType, documentIdentifiers[i]);
                actions.add(action);
                PmodeData.Action actionQns = new PmodeData.Action(code + docType+"Qns", "busdox-docid-qns::" + documentIdentifiers[i]);
                actions.add(actionQns);

                legs.add(new PmodeData.Leg(action.getName(), service.getName(), action.getName()));
                legs.add(new PmodeData.Leg(actionQns.getName(), service.getName(), actionQns.getName()));
                
                legs.add(new PmodeData.Leg(action.getName()+"Emb", serviceEmb.getName(), action.getName()));
                legs.add(new PmodeData.Leg(actionQns.getName()+"Emb", serviceEmb.getName(), actionQns.getName()));

                legs.add(new PmodeData.Leg(action.getName()+"TB", serviceTB.getName(), action.getName()));
                legs.add(new PmodeData.Leg(actionQns.getName()+"TB", serviceTB.getName(), actionQns.getName()));
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
