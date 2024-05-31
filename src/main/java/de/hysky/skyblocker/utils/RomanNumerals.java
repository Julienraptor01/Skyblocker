package de.hysky.skyblocker.utils;

public class RomanNumerals {
	private static int getDecimalValue(char romanChar) {
		return switch (romanChar) {
			case 'I' -> 1;
			case 'V' -> 5;
			case 'X' -> 10;
			case 'L' -> 50;
			case 'C' -> 100;
			case 'D' -> 500;
			case 'M' -> 1000;
			default -> 0;
		};
	}

	/**
	 * Converts a roman numeral to a decimal number.
	 *
	 * @param romanNumeral The roman numeral to convert.
	 * @return The decimal number, or 0 if the roman numeral string is malformed, empty or null.
	 */
	public static int romanToDecimal(String romanNumeral) {
		if (romanNumeral == null || romanNumeral.isEmpty()) return 0;
		romanNumeral = romanNumeral.trim().toUpperCase();
		int decimal = 0;
		int lastNumber = 0;
		for (int i = romanNumeral.length() - 1; i >= 0; i--) {
			char ch = romanNumeral.charAt(i);
			int number = getDecimalValue(ch);
			if (number == 0) return 0; //Malformed roman numeral
			decimal = number >= lastNumber ? decimal + number : decimal - number;
			lastNumber = number;
		}
		return decimal;
	}
}
