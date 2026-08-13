package de.fedjapost.json;

/**
 * An unchecked Exception, thrown when JSON encounters semantically unrecoverable failures.
 * Usually thrown when a parser encounters syntactically or structurally invalid JSON.
 *
 * @author Fedja Post
 */
public class JSONException extends RuntimeException {

	public JSONException(String msg) {
		super(msg);
	}
}