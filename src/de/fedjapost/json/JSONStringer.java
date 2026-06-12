package de.fedjapost.json;

class JSONStringer {

	static String stringify(String input) {

		if (input == null)
			return "null";

		StringBuilder sb = new StringBuilder();

		sb.append('"');

		for (char c : input.toCharArray()) {
			switch (c) {
			case '\t':
				sb.append("\t");
				break;
			case '\r':
				sb.append("\r");
				break;
			case '\n':
				sb.append("\n");
				break;
			case '\\':
			case '"':
				sb.append('\\').append(c);
				break;
			default:
				sb.append(c);
			}
		}

		sb.append('"');

		return sb.toString();
	}

	static String stringify(Object input) {
		if (input == null)
			return "null";

		if (input instanceof JSONObject || input instanceof JSONArray ||
			input instanceof Number || input instanceof Boolean)
			return input.toString();

		return stringify(input.toString());

	}
	
}