package it.eng.care.databaser.model;

public class StringPathUtils {

	public static String extract(String fullPath, String prefix, String postFix) {
		int indexStart = fullPath.indexOf(prefix);
		if (indexStart!=-1) {			
			int fromIndex = indexStart+prefix.length();
			int indexOfEnd = fullPath.indexOf(postFix, fromIndex);
			if (indexOfEnd!=-1) {
				return fullPath.substring(fromIndex, indexOfEnd);
			}
		}
		return null;
	}

}
