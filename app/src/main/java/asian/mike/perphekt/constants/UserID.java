package asian.mike.perphekt.constants;

import android.content.SharedPreferences;
import android.os.Bundle;

public class UserID{
	public static String userID="-1";
	public static String gcmID="-1";
    private static String userEmail = "-1";
	public static Bundle extras;


	public void setUserID(String output)
	{
		userID=output;
	}

    public static void setUserEmail(String output)
    {
        userEmail = output;
    }

    public static void resetUserEmail()
    {
        userEmail = "-1";
    }

    public static String getUserEmail()
    {
        return userEmail;
    }

    public static void setUserID(String id, SharedPreferences pref)
    {
       SharedPreferences.Editor editor = pref.edit();
       editor.putString("UserID", id);
       editor.commit();
    }


}

