package dk.erst.delis.data.enums;

import dk.erst.delis.data.util.BundleUtil;

/**
 * Marker interface for enums, which implement getName, and should be replaced on the fly with their name in JSON responses.
 * 
 * Also can be used for any class - checks for bundle simpleClassName.name
 */
public interface Named {

	public default String getName() {
		return BundleUtil.getName(this);
	}

}
