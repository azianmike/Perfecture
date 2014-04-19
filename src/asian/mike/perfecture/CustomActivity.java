package asian.mike.perfecture;

import java.io.IOException;

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
		checkLocationEnabled();
		super.onResume();
	}
	
	@Override
	protected void onPause()
	{
		removeLocationUpdates();
		super.onPause();
	}
	
	/**
	 * Continually polls for latest gps signal
	 */
	protected void getLatestGPS()
	{
		checkLocationEnabled();
		// Acquire a reference to the system Location Manager
		locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

		// Define a listener that responds to location updates
	    locationListener = new LocationListener() {
	        @Override
			public void onLocationChanged(Location location) {
	        	//called when a new location is found
	        	lastKnownLocation=location;  //sets last known location
	        	locationFound=true;
	        	if (locationPd!=null) {
	        		Log.e("locationPD", "locationPD");
	        		locationPd.hide();
	        		locationPd.dismiss();
	        		locationPd=null;
				}
	        	removeLocationUpdates();
	        }

	        @Override
			public void onProviderDisabled(String provider) {}
	        @Override
			public void onProviderEnabled(String provider) {}
	        @Override
			public void onStatusChanged(String provider, int status, Bundle extras) {}
	      };

	    // Register the listener with the Location Manager to receive location updates
	    LocationManager service = (LocationManager) getSystemService(LOCATION_SERVICE);
	    if(service.isProviderEnabled(LocationManager.GPS_PROVIDER))
	    {
	    	locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
	    }
	    else
	    {
	    	locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
	    }
	}
	
	/**
	 * Checks to see if the current location is enabled
	 */
	protected void checkLocationEnabled()
	{
		LocationManager service = (LocationManager) getSystemService(LOCATION_SERVICE);
		boolean enabled = service
		  .isProviderEnabled(LocationManager.NETWORK_PROVIDER) || service
		  .isProviderEnabled(LocationManager.GPS_PROVIDER);

		// check if enabled and if not send user to the GSP settings
		// Better solution would be to display a dialog and suggesting to 
		// go to the settings
		if (!enabled) {
			android.app.AlertDialog.Builder alert = new AlertDialog.Builder(this);
			alert.setTitle("Please turn on location services or GPS");
			alert.setMessage("Please turn on location services or GPS");
			alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					 Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
					 startActivity(intent);
				}
			});
			alert.show();
		 
		} 
	}
	
	
	
	/**
	 * Abstract postExecute for all activities
	 * @param output
	 * @throws IOException 
	 * @throws JSONException 
	 */
	public abstract void postExecute(String output) throws JSONException, IOException;
	

	
	/**
	 * Removes updates from location manager to stop polling
	 */
	private void removeLocationUpdates() {
		if(locationManager!=null && locationListener!=null)
		{
			locationManager.removeUpdates(locationListener);
		}
	}
	
}
