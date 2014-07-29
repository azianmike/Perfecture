package asian.mike.perfak.constants;

import android.app.Application;
import android.os.Bundle;

public class UserID extends Application{
	public static String userID="-1";
	public static String gcmID="-1";
	public static Bundle extras;
	@Override
	public void onCreate()
	{
		super.onCreate();
	}
	
	public void setUserID(String output)
	{
		userID=output;
	}
	
}

