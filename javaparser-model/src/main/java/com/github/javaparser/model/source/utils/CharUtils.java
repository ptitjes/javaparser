package com.github.javaparser.model.source.utils;

/**
 * @author Didier Villevalois
 */
public abstract class CharUtils {

	// TODO Check in parser that Unicode escapes are done earlier as specified (JLS8 §3.3)
	public static String unEscapeString(String value) {
		StringBuilder buffer = new StringBuilder();
		char[] chars = value.toCharArray();
		for (int i = 0; i < chars.length; ) {
			buffer.append(unEscapeChar(chars, i));
			i += escapedCharLength(chars, i);
		}
		return buffer.toString();
	}

	public static char unEscapeChar(char[] value, int index) {
		switch (value[index]) {
			case '\\':
				if (value.length == index + 1) {
					throw new IllegalArgumentException("Unknown char escape '" + value[index] + "'");
				}
				switch (value[index + 1]) {
					case 't':
						return '\t';
					case 'b':
						return '\b';
					case 'n':
						return '\n';
					case 'r':
						return '\r';
					case 'f':
						return '\f';
					case '\'':
						return '\'';
					case '"':
						return '\"';
					case '\\':
						return '\\';
					default:
						int codePoint;
						boolean firstZeroToThree = false;
						switch (value[index + 1]) {
							case '0':
							case '1':
							case '2':
							case '3':
								firstZeroToThree = true;
							case '4':
							case '5':
							case '6':
							case '7':
								codePoint = Character.digit(value[index + 1], 8);
								if (value.length > index + 2) {
									switch (value[index + 2]) {
										case '0':
										case '1':
										case '2':
										case '3':
										case '4':
										case '5':
										case '6':
										case '7':
											codePoint = codePoint * 8 + Character.digit(value[index + 2], 8);
											if (value.length > index + 3) {
												switch (value[index + 3]) {
													case '0':
													case '1':
													case '2':
													case '3':
													case '4':
													case '5':
													case '6':
													case '7':
														if (!firstZeroToThree) {
															throw new IllegalArgumentException("Unknown char escape '" +
																	value[index] + value[index + 1] +
																	value[index + 2] + value[index + 3] + "'");
														}
														codePoint = codePoint * 8 + Character.digit(value[index + 3], 8);
													default:
												}
											}
										default:
									}
								}
								return Character.toChars(codePoint)[0];
							default:
						}

						throw new IllegalArgumentException("Unknown char escape '" + value[index] + value[index + 1] + "'");
				}
			default:
				return value[0];
		}
	}

	private static char escapedCharLength(char[] value, int index) {
		switch (value[index]) {
			case '\\':
				if (value.length < index + 1) {
					throw new IllegalArgumentException("Unknown char escape '" + value[index] + "'");
				}
				switch (value[index + 1]) {
					case 't':
						return 2;
					case 'b':
						return 2;
					case 'n':
						return 2;
					case 'r':
						return 2;
					case 'f':
						return 2;
					case '\'':
						return 2;
					case '"':
						return 2;
					case '\\':
						return 2;
					default:
						boolean firstZeroToThree = false;
						switch (value[index + 1]) {
							case '0':
							case '1':
							case '2':
							case '3':
								firstZeroToThree = true;
							case '4':
							case '5':
							case '6':
							case '7':
								if (value.length > index + 2) {
									switch (value[index + 2]) {
										case '0':
										case '1':
										case '2':
										case '3':
										case '4':
										case '5':
										case '6':
										case '7':
											if (value.length > index + 3) {
												switch (value[index + 3]) {
													case '0':
													case '1':
													case '2':
													case '3':
													case '4':
													case '5':
													case '6':
													case '7':
														if (!firstZeroToThree) {
															throw new IllegalArgumentException("Unknown char escape '" +
																	value[index] + value[index + 1] +
																	value[index + 2] + value[index + 3] + "'");
														}
														return 4;
													default:
												}
											}
											return 3;
										default:
									}
								}
								return 2;
							default:
						}
						throw new IllegalArgumentException("Unknown char escape '" + value[index] + value[index + 1] + "'");
				}
			default:
				return 1;
		}
	}
}
