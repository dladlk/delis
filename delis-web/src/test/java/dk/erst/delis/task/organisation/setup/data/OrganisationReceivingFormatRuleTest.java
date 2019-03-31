package dk.erst.delis.task.organisation.setup.data;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.Test;

import dk.erst.delis.data.enums.document.DocumentFormatFamily;

public class OrganisationReceivingFormatRuleTest {

	private static String EXPECTED_CONVERTED_FORMATS_BY_RULE = "BIS3:CII;BIS3_POSITIVE:CII;OIOUBL:BIS3;OIOUBL:CII";

	@Test
	public void testIsLast() {

		assertTrue(OrganisationReceivingFormatRule.BIS3.isLast(DocumentFormatFamily.UNSUPPORTED));

		OrganisationReceivingFormatRule[] rules = OrganisationReceivingFormatRule.values();
		DocumentFormatFamily[] formats = appendNull(DocumentFormatFamily.values());

		List<String> list = new ArrayList<>();

		for (OrganisationReceivingFormatRule rule : rules) {
			for (DocumentFormatFamily documentFormatFamily : formats) {
				boolean last = rule.isLast(documentFormatFamily);
				if (!last) {
					list.add(rule.name() + ":" + documentFormatFamily);
				}
			}
		}

		Collections.sort(list);

		String res = list.stream().sorted().collect(Collectors.joining(";"));
		assertEquals(EXPECTED_CONVERTED_FORMATS_BY_RULE, res);
	}

	private DocumentFormatFamily[] appendNull(DocumentFormatFamily[] initial) {
		DocumentFormatFamily[] res = new DocumentFormatFamily[initial.length + 1];
		System.arraycopy(initial, 0, res, 0, initial.length);
		res[initial.length] = null;
		return res;
	}

}
