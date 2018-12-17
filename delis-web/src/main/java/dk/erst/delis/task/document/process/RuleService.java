package dk.erst.delis.task.document.process;

import static dk.erst.delis.data.DocumentFormatFamily.BIS3;
import static dk.erst.delis.data.DocumentFormatFamily.CII;
import static dk.erst.delis.data.DocumentFormatFamily.OIOUBL;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import dk.erst.delis.data.DocumentFormat;
import dk.erst.delis.data.DocumentFormatFamily;
import dk.erst.delis.data.RuleDocumentTransformation;
import dk.erst.delis.data.RuleDocumentValidation;
import dk.erst.delis.data.RuleDocumentValidationType;

@Service
public class RuleService {

	private List<RuleDocumentTransformation> transformationList;
	private List<RuleDocumentValidation> validationList;


	public RuleService() {
		this.validationList = buildHardcodedValidationList();
		this.transformationList = buildHardcoded();
		
	}
	
	private List<RuleDocumentValidation> buildHardcodedValidationList() {
		/*
		 * XSD rules
		 */
		List<RuleDocumentValidation> l = new ArrayList<>();
		l.add(xsd(DocumentFormat.OIOUBL_INVOICE, "xsd/UBL_2.0/maindoc/UBL-Invoice-2.0.xsd"));
		l.add(xsd(DocumentFormat.OIOUBL_CREDITNOTE, "xsd/UBL_2.0/maindoc/UBL-CreditNote-2.0.xsd"));

		l.add(xsd(DocumentFormat.BIS3_INVOICE, "xsd/UBL_2.1/maindoc/UBL-Invoice-2.1.xsd"));
		l.add(xsd(DocumentFormat.BIS3_CREDITNOTE, "xsd/UBL_2.1/maindoc/UBL-CreditNote-2.1.xsd"));

		l.add(xsd(DocumentFormat.CII, "xsd/CII_D16B_SCRDM_uncoupled/data/standard/CrossIndustryInvoice_100pD16B.xsd"));

		/*
		 * Schematron
		 */

		l.add(sch(DocumentFormat.OIOUBL_INVOICE, "sch/oioubl/OIOUBL_Schematron_2018-09-15_v1.10.0.35220/OIOUBL_Invoice_Schematron.xsl", 10));
		l.add(sch(DocumentFormat.OIOUBL_CREDITNOTE, "sch/oioubl/OIOUBL_Schematron_2018-09-15_v1.10.0.35220/OIOUBL_CreditNote_Schematron.xsl", 10));

		String BIS3_PEPPOL = "sch/bis3/peppol_2018-03-15_1/PEPPOL-EN16931-UBL.xslt";
		String BIS3_CEN = "sch/bis3/cen_2018-03-15_1/CEN-EN16931-UBL.xslt";
		l.add(sch(DocumentFormat.BIS3_INVOICE, BIS3_PEPPOL, 10));
		l.add(sch(DocumentFormat.BIS3_INVOICE, BIS3_CEN, 20));
		l.add(sch(DocumentFormat.BIS3_CREDITNOTE, BIS3_PEPPOL, 10));
		l.add(sch(DocumentFormat.BIS3_CREDITNOTE, BIS3_CEN, 20));

		l.add(sch(DocumentFormat.CII, "sch/cii/peppol_2018-03-15_1/PEPPOL-EN16931-CII.xslt", 10));
		l.add(sch(DocumentFormat.CII, "sch/cii/cen_2018-03-15_1/CEN-EN16931-CII.xslt", 20));

		return l;
	}

	private RuleDocumentValidation xsd(DocumentFormat format, String path) {
		RuleDocumentValidation v = new RuleDocumentValidation();
		v.setActive(true);
		v.setDocumentFormat(format);
		v.setPriority(10);
		v.setValidationType(RuleDocumentValidationType.XSD);
		v.setRootPath(path);
		return v;
	}

	private RuleDocumentValidation sch(DocumentFormat format, String path, int priority) {
		RuleDocumentValidation v = new RuleDocumentValidation();
		v.setActive(true);
		v.setDocumentFormat(format);
		v.setPriority(priority);
		v.setValidationType(RuleDocumentValidationType.SCHEMATRON);
		v.setRootPath(path);
		return v;
	}


	private List<RuleDocumentTransformation> buildHardcoded() {
		List<RuleDocumentTransformation> l = new ArrayList<>();
		l.add(b(CII, BIS3, "cii_to_bis3/v_2018-03-15_34856/CII_2_BIS-Billing.xslt"));
		l.add(b(BIS3, OIOUBL, "bis3_to_oioubl/v_2018-03-14_34841/BIS-Billing_2_OIOUBL_MASTER.xslt"));
		return l;
	}

	private RuleDocumentTransformation b(DocumentFormatFamily from, DocumentFormatFamily to, String path) {
		RuleDocumentTransformation r = new RuleDocumentTransformation();

		r.setActive(true);
		r.setDocumentFormatFamilyFrom(from);
		r.setDocumentFormatFamilyTo(to);
		r.setRootPath(path);

		return r;
	}
	
	public List<RuleDocumentValidation> getValidationList() {
		return this.validationList;
	}

	public List<RuleDocumentValidation> getValidationRuleListByFormat(DocumentFormat format) {
		return this.validationList.stream()

				.filter(r -> r.getDocumentFormat() == format)

				.sorted(new Comparator<RuleDocumentValidation>() {
					@Override
					public int compare(RuleDocumentValidation o1, RuleDocumentValidation o2) {
						if (o1.getValidationType() != o2.getValidationType()) {
							return o1.getValidationType().compareTo(o2.getValidationType());
						}
						return Integer.compare(o1.getPriority(), o2.getPriority());
					}
				})

				.collect(Collectors.toList());
	}

	public List<RuleDocumentTransformation> getTransformationList() {
		return transformationList;
	}

	public RuleDocumentTransformation getTransformation(DocumentFormatFamily format) {
		Optional<RuleDocumentTransformation> findFirst = transformationList

				.stream()

				.filter(r -> r.getDocumentFormatFamilyFrom() == format)

				.findFirst();

		return findFirst.orElse(null);
	}
}