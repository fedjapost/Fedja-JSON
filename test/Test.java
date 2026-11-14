import de.fedjapost.json.*;

public class Test {


	public static void main(String[] args) {
		JSONArray arr = new JSONArray("[\"\u0025\", 56.07e-3]");
		System.out.println(arr);

		try {
			new JSONArray("[0.1]");
		} catch (Exception e) {e.printStackTrace();}

		String src =
		"{\"yo\": \"no\", \"bro\": [67]}";
		System.out.println(new JSONObject((new JSONObject(src)).format()).format());
	}

}
