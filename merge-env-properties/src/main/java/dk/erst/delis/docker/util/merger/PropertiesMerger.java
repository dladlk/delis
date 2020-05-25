package dk.erst.delis.docker.util.merger;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.TreeMap;

public class PropertiesMerger {

	public List<String> merge(List<String> sourceLines, Map<String, String> envVariables, String variablePrefix) {
		List<String> resultLines = new ArrayList<>();
		Map<String, String> translatedEnvVariables = translateEnvVariables(envVariables, variablePrefix);
		if (translatedEnvVariables.isEmpty()) {
			System.out.println("No matching variables found - skip merging.");
			return resultLines;
		}
		List<String> updatedLines = updateExistingProperties(sourceLines, translatedEnvVariables);
		resultLines.addAll(updatedLines);
		List<String> newPropLines = getNewProperties(resultLines, translatedEnvVariables);
		resultLines.addAll(newPropLines);
		System.out.println(String.format("ADDED %d lines to the end:", newPropLines.size()));
		for (String newLine : newPropLines) {
			System.out.println(newLine);
		}
		return resultLines;
	}

	private List<String> updateExistingProperties(List<String> sourceLines, Map<String, String> translatedEnvVariables) {
		List<String> resultLines = new ArrayList<>();
		List<String> updatedLines = new ArrayList<>();
		List<String> unchangedPropValueLines = new ArrayList<>();
		for (int i = 0; i < sourceLines.size(); i++) {
			String sourceLine = sourceLines.get(i);
			String resultLine = mergeLine(sourceLine, translatedEnvVariables);
			resultLines.add(resultLine);
			if (!sourceLine.equalsIgnoreCase(resultLine)) {
				updatedLines.add(String.format("[%d]\t%s", i + 1, resultLine));
			}
			if(isPropValuesEquals(sourceLine, resultLine, translatedEnvVariables)){
				unchangedPropValueLines.add(String.format("[%d]\t%s", i + 1, resultLine));
			}
		}
		System.out.println(String.format("UPDATED %d lines:", updatedLines.size()));
		for (String updatedLine : updatedLines) {
			System.out.println(updatedLine);
		}
		if(!unchangedPropValueLines.isEmpty()) {
			System.out.println(String.format("KEEP %d lines unchanged as given value equals to present:", unchangedPropValueLines.size()));
			for (String unchangedPropValueLine : unchangedPropValueLines) {
				System.out.println(unchangedPropValueLine);
			}
		}
		return resultLines;
	}

	private boolean isPropValuesEquals(String sourceLine, String resultLine, Map<String, String> envVariables) {
		if(hasPropertyDefinition(sourceLine)) {
			String propertyName = getPropertyName(sourceLine);
			if(envVariables.containsKey(propertyName)) {
				String oldPropertyValue = getPropertyValue(sourceLine);
				String newPropertyValue = getPropertyValue(resultLine);
				return oldPropertyValue.equalsIgnoreCase(newPropertyValue);
			}
		}
		return false;
	}

	private String getPropertyName(String propertyLine) {
		return getPropertyPart(propertyLine, 0);
	}

	private String getPropertyValue(String propertyLine) {
		return getPropertyPart(propertyLine, 1);
	}

	private String getPropertyPart(String propertyLine, int index) {
		int equationIndex = propertyLine.indexOf('=');
		if (equationIndex > 0) {
			if (index == 0) {
				return propertyLine.substring(0, equationIndex).trim();
			}
			return propertyLine.substring(equationIndex + 1).trim();
		}
		return null;
	}

	private List<String> getNewProperties(List<String> existingPropLines, Map<String, String> envVariables) {
		List<String> result = new ArrayList<>();
		Set<String> existingPropertyNames = buildPropertySet(existingPropLines);
		for (Map.Entry<String, String> entry : envVariables.entrySet()) {
			String propName = entry.getKey();
			String propValue = entry.getValue();
			if (!existingPropertyNames.contains(propName.toLowerCase())) {
				result.add(String.format("%s=%s", propName, propValue));
			}
		}
		return result;
	}

	private Set<String> buildPropertySet(List<String> lines) {
		StringBuilder result = new StringBuilder();
		for (String line : lines) {
			result.append(line.toLowerCase());
			result.append("\n");
		}
		
		Properties currentProperties = new Properties();
		try {
			currentProperties.load(new StringReader(result.toString()));
		} catch (IOException e) {
		}
		return currentProperties.stringPropertyNames();
	}

	protected Map<String, String> translateEnvVariables(Map<String, String> envVariables, String variablePrefix) {
		Map<String, String> result = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
		
		List<String> foundList = new ArrayList<>();
		for (Map.Entry<String, String> entry : envVariables.entrySet()) {
			String envVariableName = entry.getKey();
			String envVariableValue = entry.getValue();
			if (envVariableName.startsWith(variablePrefix)) {
				String translatedVariableName = translateVariableName(variablePrefix, envVariableName);
				foundList.add(String.format("%s=%s", envVariableName, envVariableValue));
				result.put(translatedVariableName, envVariableValue);
			}
		}
		System.out.println(String.format("Found %d environment variables with prefix %s:", result.size(), variablePrefix));
		Collections.sort(foundList);
		for (String foundEnv : foundList) {
			System.out.println(foundEnv);
		}
		return result;
	}

	private String translateVariableName(String variablePrefix, String envVariableName) {
		String result = envVariableName.replace(variablePrefix, "").replace("_", ".").replace("..", "_");
		if (!hasLowerCaseChar(result)) {
			result = result.toLowerCase();
		}
		return result;
	}

	private boolean hasLowerCaseChar(String string) {
		for (char ch : string.toCharArray()) {
			if (Character.isLowerCase(ch)) {
				return true;
			}
		}
		return false;
	}

	protected String mergeLine(String sourceLine, Map<String, String> envVariables) {
		String resultLine = sourceLine;
		if (!hasPropertyDefinition(sourceLine)) {
			return resultLine;
		}
		String propName = getPropertyName(sourceLine);
		String translatedPropName = propName.replace("#", "").toLowerCase().trim();
		String newPropValue = envVariables.get(translatedPropName);
		if (newPropValue == null) {
			return resultLine;
		}
		if (isCommentedOut(sourceLine)) {
			propName = propName.replace("#", "");
			resultLine += System.lineSeparator() + String.format("%s=%s", propName, newPropValue);
		} else {
			resultLine = String.format("%s=%s", propName, newPropValue);
		}
		return resultLine;
	}

	private boolean hasPropertyDefinition(String line) {
		return getPropertyName(line) != null;
	}

	private boolean isCommentedOut(String sourceLine) {
		return sourceLine.trim().startsWith("#");
	}

}
