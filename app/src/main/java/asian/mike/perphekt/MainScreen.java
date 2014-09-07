package asian.mike.perphekt;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;

import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;

import asian.mike.perphekt.constants.Action;
import asian.mike.perphekt.constants.ConnectToServer;
import asian.mike.perphekt.constants.UserID;




public class MainScreen extends CustomActivity {
	private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
	public static final int GALLERY_CODE = 322;
	private Menu menu;
	
	
	public static final String EXTRA_MESSAGE = "message";
    public static final String PROPERTY_REG_ID = "registration_id";
    private static final String PROPERTY_APP_VERSION = "appVersion";

    /**
     * Substitute you own sender ID here. This is the project number you got
     * from the API Console, as described in "Getting Started."
     */
    private String SENDER_ID = "656106316752";
	
    static final String TAG = "GCMDemo";

    private GoogleCloudMessaging gcm;
    private String regid;
    private Context context;
    private AtomicInteger msgId = new AtomicInteger();
    private SharedPreferences prefs;
    private TextView mDisplay;
    
	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main_screen);
        getActionBar().setDisplayShowTitleEnabled(true);
		
		setButtonListeners();
		setUserID();
		
		if (checkPlayServices()) {
	        //setAlert("play services exist!");
			context = getApplicationContext();
			gcm = GoogleCloudMessaging.getInstance(this);
			regid = getRegistrationId(context);
            if (regid.isEmpty()) {
                registerInBackground();
            }
            final SharedPreferences prefs = getGCMPreferences(context);
            UserID.gcmID = prefs.getString(PROPERTY_REG_ID, null);
	    }
		
		imageUploadProgress = (ProgressBar)findViewById(R.id.imageUploadProgress);

	}

    @Override
    protected void onResume()
    {
        super.onResume();
        updateLogin();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_screen, menu);
        this.menu=menu;
        MenuItem signoutItem =menu.findItem(R.id.signout);
        signoutItem.setVisible(false);
        updateLogin();
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.login:
                changeLoginScreen();
                break;
            case R.id.register:
                changeRegisterScreen();
                break;
            case R.id.signout:
                signOut();
                break;
            default:
                break;
        }

        return true;
    }


    /**
	 * Gets the current registration ID for application on GCM service.
	 * <p>
	 * If result is empty, the app needs to register.
	 *
	 * @return registration ID, or empty string if there is no existing
	 *         registration ID.
	 */
	@SuppressLint("NewApi")
	private String getRegistrationId(Context context) {
	    final SharedPreferences prefs = getGCMPreferences(context);
	    String registrationId = prefs.getString(PROPERTY_REG_ID, "");
	    if (registrationId.isEmpty()) {
	        Log.i(TAG, "Registration not found.");
	        return "";
	    }
	    // Check if app was updated; if so, it must clear the registration ID
	    // since the existing regID is not guaranteed to work with the new
	    // app version.
	    int registeredVersion = prefs.getInt(PROPERTY_APP_VERSION, Integer.MIN_VALUE);
	    int currentVersion = getAppVersion(context);
	    if (registeredVersion != currentVersion) {
	        Log.i(TAG, "App version changed.");
	        return "";
	    }
	    return registrationId;
	}
	
	/**
	 * @return Application's {@code SharedPreferences}.
	 */
	private SharedPreferences getGCMPreferences(Context context) {
	    // This sample app persists the registration ID in shared preferences, but
	    // how you store the regID in your app is up to you.
	    return getSharedPreferences(MainScreen.class.getSimpleName(),
	            Context.MODE_PRIVATE);
	}
	
	
	/**
	 * Registers the application with GCM servers asynchronously.
	 * <p>
	 * Stores the registration ID and app versionCode in the application's
	 * shared preferences.
	 */

	private void registerInBackground() {
	    new AsyncTask<Object, Object, Object>() {


			@Override
			protected Object doInBackground(Object... params) {
				String msg = "";
	            try {
	                if (gcm == null) {
	                    gcm = GoogleCloudMessaging.getInstance(context);
	                }
	                regid = gcm.register(SENDER_ID);
	                msg = "Device registered, registration ID=" + regid;

	                // You should send the registration ID to your server over HTTP,
	                // so it can use GCM/HTTP or CCS to send messages to your app.
	                // The request to your server should be authenticated if your app
	                // is using accounts.
	                //sendRegistrationIdToBackend();

	                // For this demo: we don't need to send it because the device
	                // will send upstream messages to a server that echo back the
	                // message using the 'from' address in the message.

	                // Persist the regID - no need to register again.
	                storeRegistrationId(context, regid);
	            } catch (IOException ex) {
	                msg = "Error :" + ex.getMessage();
	                // If there is an error, don't just keep trying to register.
	                // Require the user to click a button again, or perform
	                // exponential back-off.
			}

            return msg;
	    }
		}.execute(null, null, null);

	}
	
	/**
	 * Stores the registration ID and app versionCode in the application's
	 * {@code SharedPreferences}.
	 *
	 * @param context application's context.
	 * @param regId registration ID
	 */
	private void storeRegistrationId(Context context, String regId) {
	    final SharedPreferences prefs = getGCMPreferences(context);
	    int appVersion = getAppVersion(context);
	    //Log.i(TAG, "Saving regId on app version " + appVersion);
	    SharedPreferences.Editor editor = prefs.edit();
	    editor.putString(PROPERTY_REG_ID, regId);
	    editor.putInt(PROPERTY_APP_VERSION, appVersion);
	    editor.commit();
	}
	
	private int getAppVersion(Context context) {
		try {
	        PackageInfo packageInfo = context.getPackageManager()
	                .getPackageInfo(context.getPackageName(), 0);
	        return packageInfo.versionCode;
	    } catch (NameNotFoundException e) {
	        // should never happen
	        throw new RuntimeException("Could not get package name: " + e);
	    }
	}

	private boolean checkPlayServices() {
	    int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
	    if (resultCode != ConnectionResult.SUCCESS) {
	        if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
	            GooglePlayServicesUtil.getErrorDialog(resultCode, this,
	                    PLAY_SERVICES_RESOLUTION_REQUEST).show();
	        } else {
	            Log.i(TAG, "This device is not supported.");
	            finish();
	        }
	        return false;
	    }
	    return true;
	}
	
	@SuppressLint("NewApi")
	private void signOut()
	{
		UserID.userID="-1";
		MenuItem loginItem = menu.findItem(R.id.login);
		MenuItem registerItem =menu.findItem(R.id.register);
		MenuItem signoutItem =menu.findItem(R.id.signout);
		loginItem.setVisible(true);
		registerItem.setVisible(true);
		signoutItem.setVisible(false);
		ActionBar actionBar = getActionBar();
		actionBar.setTitle("Not Logged in");
		updateLogin();
		saveUserToDisk("-1", "-1");
	}
	
	private void setButtonListeners() {
		Button choosePhotos = (Button) findViewById(R.id.choosePhotos);
		choosePhotos.setOnClickListener(new View.OnClickListener() {
		    @Override
		    public void onClick(View v) {
		    	if(UserID.userID.equals("-1"))
		    	{
		    		setAlert("Not logged in");
		    	}else{
                    choosePhotos();
                }

		    }
		});
		
		Button goToProcessedImages = (Button) findViewById(R.id.goToProcessedImages);
		goToProcessedImages.setOnClickListener(new View.OnClickListener() {
		    @Override
		    public void onClick(View v) {
		    	dismissNotification();
		    	goToProcessedImages();  //goes to processed images
		    }
		});

        Button cancel = (Button) findViewById(R.id.cancel);
        cancel.setVisibility(View.GONE);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cancelUploading();
            }
        });
	}

    private void cancelUploading()
    {

        Toast.makeText(this.getApplicationContext(), "Cancelled upload", Toast.LENGTH_LONG).show();
        ConnectToServer.cancelUpload(imageUploadProgress);

    }

	private void dismissNotification()
	{
		NotificationManager manager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
		manager.cancel(1);
	}
	
	private void goToProcessedImages()
	{

		Intent i = new Intent(Action.ACTION_SHOW_PROCESSED);
		startActivityForResult(i, 100);
		
	}
	
	/**
	 * Lets user choose what photos to upload
	 */
	private void choosePhotos()
	{
		Intent i = new Intent(Action.ACTION_MULTIPLE_PICK);
		startActivityForResult(i, 200);
	}

	public void changeRegisterScreen() {
		
		try{
		    Intent i = new Intent(getApplicationContext(), Register.class);
		    startActivity(i);
	    }
	    catch(Exception ex)
	    {
	        Log.e("main",ex.toString());
	    }
		
	}
	
	/*
	 * Sends a message to the view after button is clicked
	 */
	public void changeLoginScreen() {
		
		try{
		    Intent i = new Intent(getApplicationContext(), LoginActivity.class);
		    startActivity(i);
	    }
	    catch(Exception ex)
	    {
	        Log.e("main",ex.toString());
	    }
		
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) 
	{
		super.onActivityResult(requestCode, resultCode, data);
		ArrayList<String> imagePath = null;
		
		if (requestCode == 100 && resultCode == Activity.RESULT_OK) 
		{
            imagePath = new ArrayList<String>();
            imagePath.add(data.getStringExtra("single_path"));
            
        } else if (requestCode == 200 && resultCode == Activity.RESULT_OK) 
        {
            String[] all_path = data.getStringArrayExtra("all_path");
            imagePath = new ArrayList<String>();

            for (String string : all_path) {
                imagePath.add(string);
            }
            
            
        }
		
		if(imagePath != null)
		{
            ((Button) findViewById(R.id.cancel)).setVisibility(View.VISIBLE);
            Toast.makeText(this.getApplicationContext(), "Uploading your photos...", Toast.LENGTH_LONG).show();
			sendImageData(imagePath, imageUploadProgress);
		}
     }

	@Override
	public void postExecute(String output) throws JSONException, IOException {
		
	}
	
	/**
	 * Updates login text
	 */
	@SuppressLint("NewApi")
	private void updateLogin()
	{
		if(UserID.userID==null || UserID.userID.equals("-1"))
		{
			return;
		}
		
		if(menu==null)
			return;
		ActionBar actionBar = getActionBar();
		if(UserID.userID.equals("-1") || UserID.getUserEmail().equals("-1"))
		{
			Log.e(UserID.userID, ""+(UserID.userID.equals("-1")));
			actionBar.setTitle("Not logged in");
		}
		else
		{
			actionBar.setTitle(UserID.getUserEmail());
			saveUserToDisk(UserID.userID, UserID.getUserEmail());
			showSignout();
		}
	}
	
	/**
	 * Shows the signout button after the user has logged in. 
	 * Disables the login and register buttons
	 */
	private void showSignout()
	{
		MenuItem loginItem = menu.findItem(R.id.login);
		MenuItem registerItem =menu.findItem(R.id.register);
		MenuItem signoutItem =menu.findItem(R.id.signout);
		loginItem.setVisible(false);
		registerItem.setVisible(false);
		signoutItem.setVisible(true);
	}

}
