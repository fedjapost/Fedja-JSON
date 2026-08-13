package de.fedjapost.json;

import java.util.List;
import java.util.ArrayList;

/**
 * Instances of this class represent an Array in JSON.
 * This class allows you to parse and mutate arrays.
 * 
 * @author Fedja Post
 */
public class JSONArray {

	private final List<Object> array;

	/**
	 * Parses JSONArray source.
	 * 
	 * @param source The source the JSONArray will be parsed from
	 */
	public JSONArray(String source) {
		array = new ArrayList<>();
		parseJSONArray(new JSONTokenizer().tokenize(source), new int[1], true);
	}

	/**
	 * Initializes an empty JSONArray.
	 */
	public JSONArray() {
		array = new ArrayList<>();
	}

	/**
	 * Clones the passed JSONArray.
	 * 
	 * @param arr The JSONArray to be cloned 
	 */
	public JSONArray(JSONArray arr) {
		this();
		for (Object o : arr.array.toArray()) {
			if (o instanceof JSONArray)
				array.add(new JSONArray((JSONArray) o));
			else if (o instanceof JSONObject)
				array.add(new JSONObject((JSONObject) o));
			else
				array.add(o);
		}
	}

	/**
	 * Parses JSONArray source.
	 * 
	 * @param source The source the JSONArray will be parsed from
	 * 
	 * @throws JSONException When failing to parse a valid JSONArray, because of, for example, syntax errors
	 */
	public static JSONArray parse(String source) {
		return new JSONArray(source);
	}

	/**
	 * This method appends to an existing instance of JSONArray.
	 * 
	 * @return A reference to this
	 * 
	 * @param tokens The array of Tokens to be parsed
	 * @param index A reference to the pointer of parsing
	 */
	JSONArray parseJSONArray(JSONToken[] tokens, int[] index, boolean root) {
		if (tokens[index[0]++].getType() != JSONTokenType.ARRAY_START)
			throw new JSONException("Parsing Array\nexpected '{' operator at " + index[0]);

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
			throw new JSONException("Parsing Array\nexpected ']' operator at " + index[0]);

		if (root && index[0] < tokens.length - 1)
			throw new JSONException("Parsing Array\ntokens found after root");

		return this;
	}

	JSONArray parseJSONArray(JSONToken[] tokens, int[] index) {
		return parseJSONArray(tokens, index, false);
	}

	/**
	 * Appends the passed value.
	 * 
	 * @param value The appended value
	 */
	public void add(Object value) {
		array.add(value);
	}

	/**
	 * Inserts the specified value at the specified index.
	 * 
	 * @param index The index after which the value is inserted
	 * @param value The inserted value
	 */
	public void add(int index, Object value) {
		array.add(index, value);
	}

	/**
	 * Sets the specified index to the specified value.
	 * 
	 * @param index The index written to
	 * @param value The inputted value
	 */
	public void set(int index, Object value) {
		array.set(index, value);
	}

	/**
	 * Gets the value stored at the spefied index.
	 * 
	 * @param index The index read from
	 * @return The value stored at the index
	 */
	public Object get(int index) {
		return array.get(index);
	}

	public JSONObject getJSONObject(int index) {
		return (JSONObject) array.get(index);
	}

	public JSONArray getJSONArray(int index) {
		return (JSONArray) array.get(index);
	}

	public Number getNumber(int index) {
		return (Number) array.get(index);
	}

	public Boolean getBoolean(int index) {
		return (Boolean) array.get(index);
	}

	public String getString(int index) {
		return (String) array.get(index);
	}

	/**
	 * Removes the specified index and shifts the proceeding elements down.
	 * 
	 * @param index The removed index
	 */
	public void remove(int index) {
		array.remove(index);
	}

	/**
	 * Gets the length of the underlying List.
	 * 
	 * @return The length of the underlying List
	 */
	public int length() {
		return array.size();
	}

	public Object[] toArray() {
		return array.toArray();
	}

	String format(int lvl) {
		if (array.isEmpty())
			return "[]";

		StringBuilder sb = new StringBuilder();

		sb.append('[');
		sb.append('\n');

		String indentation = JSONStringer.indentation(lvl + 1);
		for (Object element : array) {
			sb.append(indentation);
			if (element instanceof JSONObject)
				sb.append(((JSONObject) element).format(lvl + 1));
			else if (element instanceof JSONArray)
				sb.append(((JSONArray) element).format(lvl + 1));
			else
				sb.append(JSONStringer.stringify(element));

				sb.append(",\n");
		}
		sb.deleteCharAt(sb.length() - 2);

		sb.append(JSONStringer.indentation(lvl));
		sb.append(']');

		return sb.toString();
	}

	/**
	 * Serializes the JSONArray in indented format.
	 * 
	 * @return Indented JSONArray source
	 * 
	 * @throws StackOverflowError when the underlying JSON structure is too nested or references itself 
	 */
	public String format() {
		return format(0);
	}

	/**
	 * Serializes the JSONArray in plain format.
	 * 
	 * @return Unformatted plain JSONArray source
	 * 
	 * @throws StackOverflowError when the underlying JSON structure is too nested or references itself 
	 */
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