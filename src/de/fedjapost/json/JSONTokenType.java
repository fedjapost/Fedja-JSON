package de.fedjapost.json;

public enum JSONTokenType {
	NULL,

	BOOLEAN,
	STRING,
	NUMBER,

	SEPARATOR,
	COLLON,

	OBJECT_START,
	OBJECT_END,

	ARRAY_START,
	ARRAY_END
}