package dk.erst.delis.web.utilities;

import static org.junit.Assert.*;

import org.junit.Test;

public class StringsUtilTest {

	@Test
	public void testSpaceTexts() {
		StringsUtil u = new StringsUtil();
		String input = "Invalid schemeID: 'ZZZ'. Must be a value from the codelist: ',GLN,DUNS,DK:P,DK:CVR,DK:CPR,DK:SE,DK:VANS,FR:SIRET,SE:ORGNR,FI:OVT,IT:FTI,IT:SIA,IT:SECETI,IT:VAT,IT:CF,NO:ORGNR,NO:VAT,HU:VAT,EU:VAT,EU:REID,AT:VAT,AT:GOV,AT:CID,IS:KT,IBAN,AT:KUR,ES:VAT,IT:IPA,AD:VAT,AL:VAT,BA:VAT,BE:VAT,BG:VAT,CH:VAT,CY:VAT,CZ:VAT,DE:VAT,EE:VAT,GB:VAT,GR:VAT,HR:VAT,IE:VAT,LI:VAT,LT:VAT,LU:VAT,LV:VAT,MC:VAT,ME:VAT,MK:VAT,MT:VAT,NL:VAT,PL:VAT,PT:VAT,RO:VAT,RS:VAT,SI:VAT,SK:VAT,SM:VAT,TR:VAT,VA:VAT,'";
		String output = u.spaceTexts(input, 60);
		System.out.println(input);
		System.out.println(output);
		
		assertEquals("123 456 789", u.spaceTexts("123456789", 3));
		assertEquals("123", u.spaceTexts("123", 3));
		assertEquals("12", u.spaceTexts("12", 3));
		assertNull(u.spaceTexts(null, 3));
		assertEquals(" 123 4 123 4 123 4 ", u.spaceTexts(" 1234 1234 1234 ", 3));
	}

}
