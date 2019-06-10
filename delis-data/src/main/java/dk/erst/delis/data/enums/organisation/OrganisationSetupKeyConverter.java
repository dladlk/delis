package dk.erst.delis.data.enums.organisation;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

@Converter(autoApply = false)
public class OrganisationSetupKeyConverter implements AttributeConverter<OrganisationSetupKey, String> {

	@Override
	public String convertToDatabaseColumn(OrganisationSetupKey attribute) {
		return attribute.name();
	}

	@Override
	public OrganisationSetupKey convertToEntityAttribute(String dbData) {
		return OrganisationSetupKey.getEnum(dbData);
	}

}
