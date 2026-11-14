import de.fedjapost.json.*;

public class Test {


	public static void main(String[] args) {
		try {
			new JSONObject("{");
		} catch (Exception e) {e.printStackTrace();}
	}

}
