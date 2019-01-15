package dk.erst.delis.data.enums.identifier;

public enum IdentifierPublishingStatus {

	PENDING, DONE, FAILED;
	
	public static IdentifierPublishingStatus getInstanceByName(String code) {
		for (IdentifierPublishingStatus e : values()) {
			if (e.name().equalsIgnoreCase(code)) {
				return e;
			}
		}
		return null;
	}

	public boolean isPending() {
		return this == PENDING;
	}

	public boolean isDone() {
		return this == DONE;
	}
	
	public boolean isFailed() {
		return this == FAILED;
	}
}
