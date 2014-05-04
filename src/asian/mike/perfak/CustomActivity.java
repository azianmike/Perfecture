package asian.mike.perfak;

import java.io.IOException;

import org.json.JSONArray;
import org.json.JSONException;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;


public abstract class CustomActivity extends FragmentActivity{
	protected static LocationManager locationManager;
	protected LocationListener locationListener;
	protected ProgressDialog pd;
	protected static boolean locationFound=false;
	protected static ProgressDialog locationPd;
	protected static Location lastKnownLocation;
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		pd = new ProgressDialog(this);
	}
	
	/**
	 * Keeps checking if location is enabled if location is never enabled
	 */
	@Override
	protected void onResume()
	{
		super.onResume();
		if(UserID.extras != null)
		{
			Log.i("resumed", UserID.extras.getString("data"));
			try {
				JSONArray testData = new JSONArray(UserID.extras.getString("data"));
				for(int i=0;i<testData.length();i++)
				{
					Log.i("resumed", testData.getString(i));
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	@Override
	protected void onPause()
	{
		super.onPause();
	}
	
	
	/**
	 * Abstract postExecute for all activities
	 * @param output
	 * @throws IOException 
	 * @throws JSONException 
	 */
	public abstract void postExecute(String output) throws JSONException, IOException;
	

	/**
	 * Failure for registration, user email probably exists
	 */
	protected void setAlert(String message)
	{
		android.app.AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle(message);
        alert.setMessage(message);
        alert.setPositiveButton("OK", null);
        alert.show();
	}

}
