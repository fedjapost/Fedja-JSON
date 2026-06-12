package de.fedjapost.json;

import java.util.ArrayList;

public class JSONArray {

	private final ArrayList<Object> array = new ArrayList<>();

	public JSONArray(String source) {
		parseJSONArray(new JSONTokenizer().tokenize(source), new int[1]);
	}

	public JSONArray() {}

	public JSONArray parseJSONArray(JSONToken[] tokens, int[] index) {
		if (tokens[index[0]++].getType() != JSONTokenType.ARRAY_START)
			throw new JSONException("missing '['");

		if (tokens[index[0]].getType() == JSONTokenType.ARRAY_END)
			return this;

		do {

			switch (tokens[index[0]].getType()) {
			case NULL: case BOOLEAN: case NUMBER: case STRING:
				array.add(tokens[index[0]].getValue());
				break;

			case OBJECT_START:
				array.add(new JSONObject().parseJSONObject(tokens, index));
				break;
			case ARRAY_START:
				array.add(new JSONArray().parseJSONArray(tokens, index));
			}
			++index[0];

		} while (tokens[index[0]++].getType() == JSONTokenType.SEPARATOR);
		--index[0];

		if (tokens[index[0]].getType() != JSONTokenType.ARRAY_END)
			throw new JSONException("missing ']'");

		return this;
	}

	public void add(Object value) {
		array.add(value);
	}

	public void add(int index, Object value) {
		array.add(index, value);
	}

	public void set(int index, Object value) {
		array.set(index, value);
	}

	public Object get(int index) {
		return array.get(index);
	}

	public void remove(int index) {
		array.remove(index);
	}

	public int length() {
		return array.size();
	}

	public Object[] toArray() {
		return array.toArray();
	}

	@Override
	public String toString() {
		if (array.isEmpty())
			return "[]";

		StringBuilder sb = new StringBuilder();

		sb.append('[');

		for (Object element : array) {
			sb
				.append(JSONStringer.stringify(element))
				.append(',');
		}
		sb.deleteCharAt(sb.length() - 1);

		sb.append(']');

		return sb.toString();
	}

}