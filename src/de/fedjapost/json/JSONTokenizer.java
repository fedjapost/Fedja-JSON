package de.fedjapost.json;

import java.util.ArrayList;

public class JSONTokenizer {

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

	private void parseString() {
		if (counter >= source.length() || source.charAt(counter) != '"')
			return;
		advanceCounter();

		StringBuilder sb = new StringBuilder();
		while (counter < source.length()) {

			if (source.charAt(counter) == '"')
				break;

			if (counter + 1 < source.length() && source.charAt(counter) == '\\')
				advanceCounter();

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

		if (counter >= source.length() || !(Character.isDigit(source.charAt(counter))))
			return;

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
		parseOperator();
		parseValue();
		parseString();
	}

	public JSONToken[] tokenize(String source) {
		this.source = source;

		counter = 0;
		while (counter < source.length())
			parse();

		JSONToken[] arr = new JSONToken[tokens.size()];
		for (int i = 0; i < arr.length; ++i)
			arr[i] = tokens.get(i);

		return arr;
	}

}