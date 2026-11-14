import de.fedjapost.json.*;

public class Test {


	public static void main(String[] args) {
		JSONArray arr = new JSONArray("[\"\u0025\"]");
		System.out.println(arr);
	}

}
