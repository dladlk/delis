package dk.erst.delis.validator;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import lombok.Data;

@Configuration
@Data
public class DelisValidatorConfig {

	@Value("${delis.validation-artifact-folder}")
	private String storageValidationRoot;

	@Value("${delis.validator.persist.mode:none}")
	private String persistModeStr;

	@Value("${delis.validator.persist.folder}")
	private String persistFolder;

	@Value("${delis.validator.response.status-header:description}")
	private String responseStatusHeader;

	@Value("${delis.validator.response.valid.code:200}")
	private int responseResponseValidCode;
	@Value("${delis.validator.response.valid.status:DELIS:VALID}")
	private String responseResponseValidStatus;

	@Value("${delis.validator.response.invalid.code:412}")
	private int responseResponseInvalidCode;
	@Value("${delis.validator.response.valid.status.xml:DELIS:INVALID_XML}")
	private String responseResponseInvalidStatusXml;
	@Value("${delis.validator.response.valid.status.xsd:DELIS:INVALID_BY_XSD}")
	private String responseResponseInvalidStatusXsd;
	@Value("${delis.validator.response.valid.status.sch:DELIS:INVALID_BY_SCHEMATRON}")
	private String responseResponseInvalidStatusSchematron;

	private DelisValidatorPesistMode persistMode;

	public DelisValidatorPesistMode getPersistMode() {
		if (this.persistMode == null) {
			try {
				this.persistMode = DelisValidatorPesistMode.valueOf(persistModeStr.toLowerCase());
			} catch (Exception e) {
				this.persistMode = DelisValidatorPesistMode.NONE;
			}
		}
		return this.persistMode;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(" validationRoot=");
		sb.append(storageValidationRoot);
		sb.append("\n\t persistMode=");
		sb.append(getPersistMode());
		sb.append("\n\t persistFolder=");
		sb.append(persistFolder);

		sb.append("\n\t responseStatusHeader=");
		sb.append(this.responseStatusHeader);
		sb.append("\n\t responseResponseValidCode=");
		sb.append(this.responseResponseValidCode);
		sb.append("\n\t responseResponseValidStatus=");
		sb.append(this.responseResponseValidStatus);
		sb.append("\n\t responseResponseInvalidCode=");
		sb.append(this.responseResponseInvalidCode);
		sb.append("\n\t responseResponseInvalidStatusXml=");
		sb.append(this.responseResponseInvalidStatusXml);
		sb.append("\n\t responseResponseInvalidStatusXsd=");
		sb.append(this.responseResponseInvalidStatusXsd);
		sb.append("\n\t responseResponseInvalidStatusSchematron=");
		sb.append(this.responseResponseInvalidStatusSchematron);
		return sb.toString();
	}
}
