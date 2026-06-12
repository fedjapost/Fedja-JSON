package de.fedjapost.json;

import java.util.HashMap;
import java.util.Set;

public class JSONObject {

	private final HashMap<String, Object> object = new HashMap<>();

	public JSONObject(String source) {
		parseJSONObject(new JSONTokenizer().tokenize(source), new int[1]);
	}

	public JSONObject() {}

	public JSONObject parseJSONObject(JSONToken[] tokens, int[] index) {
		if (tokens[index[0]++].getType() != JSONTokenType.OBJECT_START)
			throw new JSONException("missing '{'");

		if (tokens[index[0]].getType() == JSONTokenType.OBJECT_END)
			return this;

		do {

			if (tokens[index[0]].getType() != JSONTokenType.STRING)
				throw new JSONException("invalid key for object: " + tokens[index[0]].getType());

			String key = (String) tokens[index[0]++].getValue();

			if (tokens[index[0]++].getType() != JSONTokenType.COLLON)
				throw new JSONException("missing ':'");

			switch (tokens[index[0]].getType()) {
			case NULL: case BOOLEAN: case NUMBER: case STRING:
				object.put(key, tokens[index[0]].getValue());
				break;

			case OBJECT_START:
				object.put(key, new JSONObject().parseJSONObject(tokens, index));
				break;
			case ARRAY_START:
				object.put(key, new JSONArray().parseJSONArray(tokens, index));
			}
			++index[0];

		} while (tokens[index[0]++].getType() == JSONTokenType.SEPARATOR);
		--index[0];

		if (tokens[index[0]].getType() != JSONTokenType.OBJECT_END)
			throw new JSONException("missing '}'");

		return this;
	}

	public Object get(String key) {
		return object.get(key);
	}

	public void set(String key, Object value) {
		object.put(key, value);
	}

	public void remove(String key) {
		object.remove(key);
	}

	public Set<?> keySet() {
		return object.keySet();
	}

	@Override
	public String toString() {
		if (object.isEmpty())
			return "{}";

		StringBuilder sb = new StringBuilder();

		sb.append('{');

		for (final String key : object.keySet()) {
			sb
				.append(JSONStringer.stringify(key))
				.append(':')
				.append(JSONStringer.stringify(object.get(key)))
				.append(',');
		}
		sb.deleteCharAt(sb.length() - 1);

		sb.append('}');

		return sb.toString();
	}
	
}