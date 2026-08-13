package de.fedjapost.json;

class JSONToken {

	private final Object value;

	private final JSONTokenType type;

	public JSONToken(JSONTokenType type, Object value) {
		this.type = type;
		this.value = value;
	}

	public Object getValue() {
		return value;
	}

	public JSONTokenType getType() {
		return type;
	}
}