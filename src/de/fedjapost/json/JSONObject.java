package de.fedjapost.json;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Instances of this class represent an Object in JSON.
 * This class allows you to parse and mutate objects.
 * 
 * @author Fedja Post
 */
public class JSONObject {

	private final Map<String, Object> object;

	/**
	 * Parses JSONObject source.
	 * 
	 * @param source The source the JSONObject will be parsed from
	 * 
	 * @throws JSONException When encountering syntax errors
	 */
	public JSONObject(String source) {
		object = new HashMap<>();
		parseJSONObject(new JSONTokenizer().tokenize(source), new int[1], true);
	}

	/**
	 * Initializes an empty JSONObject.
	 */
	public JSONObject() {
		object = new HashMap<>();
	}

	/**
	 * Clones the passed JSONObject.
	 * 
	 * @param obj The JSONObject to be cloned 
	 */
	public JSONObject(JSONObject obj) {
		this();
		for (String key : obj.keySet()) {
			if (obj.get(key) instanceof JSONArray)
				object.put(key, new JSONArray((JSONArray) obj.get(key)));
			else if (obj.get(key) instanceof JSONObject)
				object.put(key, new JSONObject((JSONObject) obj.get(key)));
			else
				object.put(key, obj.get(key));
		}
	}

	/**
	 * Parses JSONObject source.
	 * 
	 * @param source The source the JSONObject will be parsed from
	 * 
	 * @throws JSONException When failing to parse a valid JSONObject, because of, for example, syntax errors
	 */
	public static JSONObject parse(String source) {
		return new JSONObject(source);
	}

	/**
	 * This method mutates an existing instance of JSONObject.
	 * 
	 * @return A reference to this
	 * 
	 * @param tokens The array of Tokens to be parsed
	 * @param index A reference to the pointer of parsing
	 */
	JSONObject parseJSONObject(JSONToken[] tokens, int[] index, boolean root) {
		if (tokens[index[0]++].getType() != JSONTokenType.OBJECT_START)
			throw new JSONException("Parsing Object\nexpected '{' operator at " + index[0]);

		if (tokens[index[0]].getType() == JSONTokenType.OBJECT_END)
			return this;

		do {

			if (tokens[index[0]].getType() != JSONTokenType.STRING)
				throw new JSONException("Parsing Object\nexpected valid key at " + index[0]);

			String key = (String) tokens[index[0]++].getValue();

			if (tokens[index[0]++].getType() != JSONTokenType.COLLON)
				throw new JSONException("Parsing Object\nexpected ':' at " + index[0]);

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
			throw new JSONException("Parsing Object\nexpected '}' operator at " + index[0]);

		if (root && index[0] < tokens.length - 1)
			throw new JSONException("Parsing Array\ntokens found after root");

		return this;
	}

	JSONObject parseJSONObject(JSONToken[] tokens, int[] index) {
		return parseJSONObject(tokens, index, false);
	}

	/**
	 * Sets the specified key to the specified value.
	 * 
	 * @param key The key the specified value will be stored at
	 * @param value The value that will be assigned to the specified key
	 */
	public void set(String key, Object value) {
		object.put(key, value);
	}

	/**
	 * Gets the value assigned to the specified key.
	 * 
	 * @param key The key the value is gotten from
	 * @return The value assigned to that key
	 */
	public Object get(String key) {
		return object.get(key);
	}

	public JSONObject getJSONObject(String key) {
		return (JSONObject) object.get(key);
	}

	public JSONArray getJSONArray(String key) {
		return (JSONArray) object.get(key);
	}

	public Number getNumber(String key) {
		return (Number) object.get(key);
	}

	public Boolean getBoolean(String key) {
		return (Boolean) object.get(key);
	}

	public String getString(String key) {
		return (String) object.get(key);
	}

	/**
	 * Deletes the specified key.
	 * 
	 * @param key The key to be undefined 
	 */
	public void remove(String key) {
		object.remove(key);
	}

	/**
	 * Returns a Set of keys the object defines.
	 * 
	 * @return The set of defined keys
	 */
	public Set<String> keySet() {
		return object.keySet();
	}

	String format(int lvl) {
		if (object.isEmpty())
			return "{}";

		StringBuilder sb = new StringBuilder();

		sb.append('{');
		sb.append('\n');

		String indentation = JSONStringer.indentation(lvl + 1);
		for (final String key : object.keySet()) {
			sb
				.append(indentation)
				.append(JSONStringer.stringify(key))
				.append(": ");
			if (object.get(key) instanceof JSONObject)
				sb.append(((JSONObject) object.get(key)).format(lvl + 1));
			else if (object.get(key) instanceof JSONArray)
				sb.append(((JSONArray) object.get(key)).format(lvl + 1));
			else
				sb.append(JSONStringer.stringify(object.get(key)));

			sb.append(",\n");
		}
		sb.setLength(sb.length() - 2);
		sb.append('\n');

		sb.append(JSONStringer.indentation(lvl));
		sb.append('}');

		return sb.toString();
	}

	/**
	 * Serializes the JSONObject in indented format.
	 * 
	 * @return Indented JSONObject source
	 * 
	 * @throws StackOverflowError when the underlying JSON structure is too nested or references itself 
	 */
	public String format() {
		return format(0);
	}

	/**
	 * Serializes the JSONObject in plain format.
	 * 
	 * @return Plain unformatted JSONObject source
	 * 
	 * @throws StackOverflowError when the underlying JSON structure is too nested or references itself 
	 */
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