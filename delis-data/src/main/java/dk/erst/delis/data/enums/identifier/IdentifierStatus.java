package dk.erst.delis.data.enums.identifier;

import dk.erst.delis.data.enums.Named;
import dk.erst.delis.data.util.BundleUtil;

public enum IdentifierStatus implements Named {

	ACTIVE, DELETED;
	
	public static IdentifierStatus getInstanceByName(String code) {
		for (IdentifierStatus e : values()) {
			if (e.name().equalsIgnoreCase(code)) {
				return e;
			}
		}
		return null;
	}

	public boolean isActive() {
		return this == ACTIVE;
	}
	
	public boolean isDeleted() {
		return this == DELETED;
	}

	@Override
	public String getName() {
		return BundleUtil.getName(this);
	}
}
