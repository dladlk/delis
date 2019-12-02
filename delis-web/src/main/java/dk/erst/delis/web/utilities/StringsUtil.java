package dk.erst.delis.web.utilities;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.WordUtils;
import org.springframework.stereotype.Component;

@Component("strings")
public class StringsUtil {

	private static final Set<Integer> SPACE_PREFERED_CHARS = buildCharSet(",;/:\\-");

	public String spaceTags(String path) {
		if (!StringUtils.isEmpty(path)) {
			return path.replaceAll("/", " /").trim();
		}
		return path;
	}

	private static Set<Integer> buildCharSet(String string) {
		Set<Integer> res = new HashSet<Integer>();
		char[] charArray = string.toCharArray();
		for (char c : charArray) {
			res.add((int) c);
		}
		return res;
	}

	public String spaceTextsWordUtils(String text, int maxNoSpaceLength) {
		return WordUtils.wrap(text, maxNoSpaceLength, " ", true, "[,:]");
	}

	public String spaceTexts(String text, int maxNoSpaceLength) {
		if (!StringUtils.isEmpty(text) && text.length() > maxNoSpaceLength) {
			int lastSpace = -1;
			int lastSpacePrefered = -1;

			List<Integer> insertSpacePositions = new ArrayList<Integer>();

			for (int i = 0; i < text.length(); i++) {
				char c = text.charAt(i);
				if (Character.isWhitespace(c)) {
					lastSpace = i;
					lastSpacePrefered = -1;
				} else {
					if (SPACE_PREFERED_CHARS.contains((int) c)) {
						lastSpacePrefered = i;
					}
					int curNoSpaceLength = i - lastSpace;
					if (curNoSpaceLength > maxNoSpaceLength) {
						if (lastSpacePrefered >= 0) {
							insertSpacePositions.add(lastSpacePrefered);
						} else {
							insertSpacePositions.add(i);
						}
						lastSpace = i - 1;
						lastSpacePrefered = -1;
					}
				}
			}

			if (!insertSpacePositions.isEmpty()) {
				StringBuilder sb = new StringBuilder(text.length() + insertSpacePositions.size());
				int lastPos = 0;
				for (int i = 0; i < insertSpacePositions.size(); i++) {
					Integer spacePos = insertSpacePositions.get(i);
					sb.append(text.substring(lastPos, spacePos));
					sb.append(' ');
					lastPos = spacePos;
					if (i == insertSpacePositions.size() - 1) {
						sb.append(text.substring(spacePos));
					}
				}
				return sb.toString();
			}
		}
		return text;
	}
	
	public String unraw(String rawIdentifier) {
		if (StringUtils.isNotBlank(rawIdentifier)) {
			String s = rawIdentifier.replaceAll("\\:\\:", ":");
			if (s.trim().equals(":")) {
				return "";
			}
			return s;
		}
		return rawIdentifier;
	}
}
