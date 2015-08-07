package co.numberencoding.core.utilities;

public class Utilities {

	
	public static String cleanInput(String input) {
		input = input.replaceAll("[\\-\\/]", "");
		return input;
	}
	
}
