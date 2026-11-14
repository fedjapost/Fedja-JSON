package de.fedjapost.json;

import java.util.ArrayList;

class JSONTokenizer {

	private String source;

	private final ArrayList<JSONToken> tokens = new ArrayList<>();

	private int counter;

	private void advanceCounter() {
		++counter;
	}

	private void advanceCounter(int count) {
		counter += count;
	}

	private void pushToken(JSONTokenType type, Object value) {
		tokens.add(new JSONToken(type, value));
	}

	private void pushToken(JSONTokenType type) {
		pushToken(type, null);
	}

	private char unicode() {
		int unicode = 0;
		for (int i = 0; i < 4; ++i) {
			advanceCounter();
			unicode *= 16;
			if (source.charAt(counter) < '0' - 1 || source.charAt(counter) > '9' + 1 ||
				source.charAt(counter) < 'A' - 1 || source.charAt(counter) > 'F' + 1
				|| source.charAt(counter) < 'a' - 1 || source.charAt(counter) > 'f' + 1)
				throw new JSONException("Encountered invalid unicode notation at " + counter + " .");

			if (source.charAt(counter) >= '0' || source.charAt(counter) <= '9')
				unicode += source.charAt(counter) - 48;

			if (source.charAt(counter) >= 'A' || source.charAt(counter) <= 'F')
				unicode += source.charAt(counter) - 65 + 10;

			if (source.charAt(counter) >= 'a' || source.charAt(counter) <= 'f')
				unicode += source.charAt(counter) - 97 + 10;

			
		}
				
		return (char) unicode;
	}

	private char getEscapedCharacter(char escaped) {
		switch (escaped) {
		case 'f':
			return '\f';
		case 'r':
			return '\r';
		case 'n':
			return '\n';
		case 't':
			return '\t';
		case '\\':
			return '\\';
		case '"':
			return '"';
		case 'u':
			return unicode();

		default:
			return 0;
		}
	}

	private boolean currentCharEquals(char ch) {
		return (
			source.charAt(counter) == ch
		);
	}

	private void parseString() {
		if (counter >= source.length() || source.charAt(counter) != '"')
			return;
		advanceCounter();

		StringBuilder sb = new StringBuilder();
		boolean closed = false;
		while (true) {

			if (source.charAt(counter) == '"') {
				closed = true;
				break;
			}

			if (currentCharEquals('\n') || currentCharEquals('\r') || currentCharEquals('\t') || currentCharEquals('\f'))
				throw new JSONException("Tokenizing\nParsing String\nIllegal Character");

			if (source.charAt(counter) == '\\') {
				advanceCounter();
				char escaped = getEscapedCharacter(source.charAt(counter));
				if (escaped == 0)
					throw new JSONException("Encountered string containing invalid character escape while tokenizing JSON.");
				sb.append(escaped);
				advanceCounter();
				continue;
			}


			sb.append(source.charAt(counter));
			advanceCounter();
		}
		pushToken(JSONTokenType.STRING, sb.toString());
		advanceCounter();
	}

	private void parseValue() {

		if (source.startsWith("null", counter)) {
			pushToken(JSONTokenType.NULL);
			advanceCounter(4);
			return;
		}

		if (source.startsWith("true", counter)) {
			pushToken(JSONTokenType.BOOLEAN, true);
			advanceCounter(4);
			return;
		}

		if (source.startsWith("false", counter)) {
			pushToken(JSONTokenType.BOOLEAN, false);
			advanceCounter(5);
			return;
		}

		if (counter >= source.length() || !(Character.isDigit(source.charAt(counter))) && source.charAt(counter) != '-')
			return;

		boolean negative = false;
		if (source.charAt(counter) == '-') {
			negative = true;
			advanceCounter();
		}

		double value = 0;

		while (counter < source.length() && Character.isDigit(source.charAt(counter))) {
			value *= 10;
			value += source.charAt(counter) - '0';
			advanceCounter();
		}

		if (counter >= source.length() || !(source.charAt(counter) == '.')) {
			pushToken(JSONTokenType.NUMBER, value);
			return;
		} else advanceCounter();

		for (double i = 0.1; counter < source.length() && Character.isDigit(source.charAt(counter)); i /= 10) {
			value += i * (source.charAt(counter) - '0');
			advanceCounter();
		}

		if (negative)
			value = -value;

		pushToken(JSONTokenType.NUMBER, value);
	}

	private void parseOperator() {

		switch (source.charAt(counter)) {
		case '\n': case ' ': case '\t': case '\r':
			advanceCounter();
			return;

		case ',':
			pushToken(JSONTokenType.SEPARATOR);
			advanceCounter();
			return;

		case '{':
			pushToken(JSONTokenType.OBJECT_START);
			advanceCounter();
			return;

		case '}':
			pushToken(JSONTokenType.OBJECT_END);
			advanceCounter();
			return;

		case '[':
			pushToken(JSONTokenType.ARRAY_START);
			advanceCounter();
			return;

		case ']':
			pushToken(JSONTokenType.ARRAY_END);
			advanceCounter();
			return;

		case ':':
			pushToken(JSONTokenType.COLLON);
			advanceCounter();
			return;
		}
	}

	private void parse() {
		int before = counter;
		parseOperator();
		parseValue();
		parseString();
		int after = counter;
		if (before == after)
			throw new JSONException("Encountered unrecongnized character while tokenizing JSON.");
	}

	public JSONToken[] tokenize(String source) {
		try {
			this.source = source;

			counter = 0;
			while (counter < source.length())
				parse();

			JSONToken[] arr = new JSONToken[tokens.size()];
			for (int i = 0; i < arr.length; ++i)
				arr[i] = tokens.get(i);

			return arr;
		} catch (ArrayIndexOutOfBoundsException e) {
			throw new JSONException("Unexpectedly reached end of source while tokenizing JSON.");
		}
	}

}
