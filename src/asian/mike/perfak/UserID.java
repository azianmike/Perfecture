package asian.mike.perfak;

import android.app.Application;

public class UserID extends Application{
	public static String userID="-1";
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
