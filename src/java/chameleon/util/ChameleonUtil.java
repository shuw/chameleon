package util;

public class ChameleonUtil {
	public static String processString(String input, int maxLength) {
		String noHTMLString = input.replaceAll("\\<.*?>", "");

		if (maxLength < 3) {
			maxLength = 3;
		}

		if (noHTMLString.length() > maxLength) {
			noHTMLString = noHTMLString.substring(0, noHTMLString.length() - 3) + "...";

		}
		return noHTMLString;

	}
}
