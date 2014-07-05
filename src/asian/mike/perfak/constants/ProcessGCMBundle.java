package asian.mike.perfak.constants;

import java.util.ArrayList;

import android.os.Bundle;
import android.util.Log;

public class ProcessGCMBundle {
	private static ArrayList<String> uris;
	
	public static void setURIS(Bundle bundle)
	{
		String data = bundle.getString("data");
		Log.i("data in ProcessGCMBundle", data);
	}
	
}
