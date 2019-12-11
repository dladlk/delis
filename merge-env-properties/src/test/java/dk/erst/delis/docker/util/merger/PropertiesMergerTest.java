package dk.erst.delis.docker.util.merger;

import org.junit.Test;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;

public class PropertiesMergerTest {

	@Test
	public void testMerge() throws Exception {
		PropertiesMerger merger = new PropertiesMerger();

		Map<String, String> envVariables = new HashMap<>();
		envVariables.put("SOME_VARIABLE", "new");
		envVariables.put("PM_test_new_property_mixedCase", "new");
		envVariables.put("PM_test_property", "new");
		envVariables.put("PM_test_property_commented2", "new");
		envVariables.put("PM_TEST_PROPERTY_COMMENTED", "new");
		envVariables.put("PM_TEST_PROPERTYUPPERCASE", "new");
		envVariables.put("PM_TEST_NEW_PROPERTYAB", "new");
		envVariables.put("PM_TEST_NEW_PROPERTYBC", "new");
		envVariables.put("PM_TEST_NEW_PROPERTY", "new");
		envVariables.put("PM_TEST_PROPERTY_UNCHANGED", "123");

		List<String> sourceLines = Arrays.asList(
				"test.property = 10",
				"",
				"#test.property.commented2=old",
				"#test.property.commented=old",
				"test.propertyUppercase=old",
				"test.property.unchanged=123",
				"test.property.unchanged.2=123"
		);
		List<String> expectedLines = Arrays.asList(
				"test.property=new",
				"",
				"#test.property.commented2=old",
				"test.property.commented2=new",
				"#test.property.commented=old",
				"test.property.commented=new",
				"test.propertyUppercase=new",
				"test.property.unchanged=123",
				"test.property.unchanged.2=123",
				"test.new.property=new",
				"test.new.property.mixedCase=new",
				"test.new.propertyab=new",
				"test.new.propertybc=new"
		);
		List<String> resultLines = merger.merge(sourceLines, envVariables, "PM_");

		String sourceString = linesToString(sourceLines, System.lineSeparator());
		String expectedString = linesToString(expectedLines, System.lineSeparator());;
		String resultString = linesToString(resultLines, System.lineSeparator()) ;

		System.out.println("source:"+System.lineSeparator() + sourceString);
		System.out.println("===============");
		System.out.println("expected:"+System.lineSeparator() + expectedString);
		System.out.println("===============");
		System.out.println("result:"+System.lineSeparator() + resultString);

		assertEquals(expectedString, resultString);
	}

	private String linesToString(List<String> lines, String separator) {
		StringBuilder result = new StringBuilder();
		for (String line : lines) {
			result.append(line).append(separator);
		}
		return result.toString();
	}
	
	@Test
	public void testMergeLinesWithCommentedEmptyValue() {
		PropertiesMerger m = new PropertiesMerger();

		Map<String, String> envVariables = new HashMap<>();
		envVariables.put("PM_test_line", "new");
		envVariables.put("PM_test.with__underscore", "new");
		envVariables = m.translateEnvVariables(envVariables, "PM_");

		assertEquals("#test.line=\r\ntest.line=new", m.mergeLine("#test.line=", envVariables));
		assertEquals("test.line=new", m.mergeLine("test.line=", envVariables));
		assertEquals("test.with_underscore=new", m.mergeLine("test.with_underscore=", envVariables));
	}
}
