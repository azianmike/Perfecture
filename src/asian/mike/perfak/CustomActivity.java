package asian.mike.perfak;

import java.io.IOException;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import asian.mike.perfak.constants.UserID;


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
	
	protected ArrayList<String> getImageURIs() {
		String[] projection = {MediaStore.Images.Media.DATA};

		ContextWrapper context = (ContextWrapper) getApplicationContext();
		final Cursor cursor = context.getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
	            projection,
	            null,
	            null,
	            null);

	    ArrayList<String> result = new ArrayList<String>(cursor.getCount());


	    if (cursor.moveToFirst()) {
	        final int dataColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
	        do {
	            final String data = cursor.getString(dataColumn);
	            result.add(data);
	        } while (cursor.moveToNext());
	    }
	    cursor.close();
		return result;
	}
	
}
