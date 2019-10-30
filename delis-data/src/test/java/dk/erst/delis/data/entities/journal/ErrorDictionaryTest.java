package dk.erst.delis.data.entities.journal;

import static org.junit.Assert.*;

import org.junit.Test;

import dk.erst.delis.data.enums.document.DocumentErrorCode;

public class ErrorDictionaryTest {

	@Test
	public void testCalculateHash() {
		ErrorDictionary ed = new ErrorDictionary();
		ed.setCode("PEPPOL-EN16931-R007");
		ed.setErrorType(DocumentErrorCode.CII_SCH);
		ed.setLocation("/CrossIndustryInvoice[1]/ExchangedDocumentContext[1]");
		ed.setMessage("Business process MUST be in the format 'urn:fdc:peppol.eu:2017:poacc:billing:NN:1.0' where NN indicates the process number.");
		ed.setFlag("fatal");
		
		System.out.println(ed.getCode()+": "+ed.getCode().hashCode());
		System.out.println(ed.getErrorTypeString()+": "+ed.getErrorTypeString().hashCode());
		System.out.println(ed.getLocation()+": "+ed.getLocation().hashCode());
		System.out.println(ed.getMessage()+": "+ed.getMessage().hashCode());
		System.out.println(ed.getFlag()+": "+ed.getFlag().hashCode());
		
		assertEquals(1088756055, ed.calculateHash());
	}

}
