package asian.mike.perfak;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;

import org.json.JSONException;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;

import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.MediaStore.Images.Media;
import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import asian.mike.perfak.R;




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
    String SENDER_ID = "656106316752";
	
    static final String TAG = "GCMDemo";
    
    GoogleCloudMessaging gcm;
    String regid;
    Context context;
    AtomicInteger msgId = new AtomicInteger();
    SharedPreferences prefs;
    TextView mDisplay;
    
    
	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main_screen);
		
	    //sendImageData();
		
		setButtonListeners();
		
		if (checkPlayServices()) {
	        //setAlert("play services exist!");
			context = getApplicationContext();
			gcm = GoogleCloudMessaging.getInstance(this);
			regid = getRegistrationId(context);
			Log.i("regid", regid);
            if (regid.isEmpty()) {
                registerInBackground();
            }
	    }
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
	        
	        Log.i("done", "done with task");
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
	    Log.i(TAG, "Saving regId on app version " + appVersion);
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
		//getMenuInflater().inflate(R.menu.main, menu);
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

	
	private void signOut()
	{
		UserID.userID="-1";
		MenuItem loginItem = menu.findItem(R.id.login);
		MenuItem registerItem =menu.findItem(R.id.register);
		MenuItem signoutItem =menu.findItem(R.id.signout);
		loginItem.setVisible(true);
		registerItem.setVisible(true);
		signoutItem.setVisible(false);
		saveUserToDisk("-1");
		//showSignOutDialog();
		updateLogin();
	}
	
	/**
	 * Saves user Id to disk so it can be retrieved later
	 * @param user
	 */
	private void saveUserToDisk(String user)
	{
		String filename = "userID";
		FileOutputStream outputStream;

		try {
		  outputStream = openFileOutput(filename, Context.MODE_PRIVATE);
		  outputStream.write(user.getBytes());
		  outputStream.close();
		  Log.e("written", "written "+user);
		} catch (Exception e) {
		  e.printStackTrace();
		}
	}
	
	private void setButtonListeners() {
		Button submit=(Button) findViewById(R.id.registerButton);
		submit.setOnClickListener(new View.OnClickListener() {
		    @Override
		    public void onClick(View v) {
		        changeRegisterScreen();
		    }
		});
		
		Button login = (Button) findViewById(R.id.loginButton);
		login.setOnClickListener(new View.OnClickListener() {
		    @Override
		    public void onClick(View v) {
		        changeLoginScreen();
		    }
		});
		
		Button upload = (Button) findViewById(R.id.uploadButton);
		upload.setOnClickListener(new View.OnClickListener() {
		    @Override
		    public void onClick(View v) {
		    	if(UserID.userID.equals("-1"))
		    	{
		    		setAlert("Not logged in");
		    	}
		    	else{
		    		sendImageData();
		    	}
		    }
		});
		
	}

	/**
	 * Updates login text
	 */
	@SuppressLint("NewApi")
	private void updateLogin()
	{
		if(menu==null)
			return;
		ActionBar actionBar = getActionBar();
		if(UserID.userID.equals("-1"))
		{
			Log.e(UserID.userID, ""+(UserID.userID.equals("-1")));
			actionBar.setTitle("Not logged in");
		}
		else
		{
			actionBar.setTitle("User:"+UserID.userID);
			saveUserToDisk(UserID.userID);
			showSignOut();
		}
	}
	
	/**
	 * After user logs in, shows the sign out button
	 */
	private void showSignOut()
	{
		MenuItem loginItem = menu.findItem(R.id.login);
		MenuItem registerItem =menu.findItem(R.id.register);
		MenuItem signoutItem =menu.findItem(R.id.signout);
		loginItem.setVisible(false);
		registerItem.setVisible(false);
		signoutItem.setVisible(true);
	}
	
	private void sendImageData() {
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
	    
	    //ImageView firstImageView = (ImageView) findViewById(R.id.imageView1);
	    Uri link = Uri.parse("file://"+result.get(0));
	    Uri link2 = Uri.parse("file://"+result.get(1));
	    Bitmap bitmap = null;

	    try {
			bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), link);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    //firstImageView.setImageBitmap(bitmap);
	    
	    ClientThread test = new ClientThread(result, this.getContentResolver());
	    test.execute();
	}

	
	/**
	 * Gets user ID from disk if it exists
	 * @return UserID
	 */
	private String getUserFromDisk()
	{
		String ret="-1";
		try {
	        InputStream inputStream = openFileInput("userID");
	        
	        if ( inputStream != null ) {
	            InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
	            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
	            String receiveString = "";
	            StringBuilder stringBuilder = new StringBuilder();

	            while ( (receiveString = bufferedReader.readLine()) != null ) {
	                stringBuilder.append(receiveString);
	            }

	            inputStream.close();
	            ret = stringBuilder.toString();
	        }
	    }catch (FileNotFoundException e) {
	        Log.e("login activity", "File not found: " + e.toString());
	    } catch (IOException e) {
	        Log.e("login activity", "Can not read file: " + e.toString());
	    }
		
		return ret;
	}
	
	/*
	 * Sends a message to the view after button is clicked
	 */
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
	public void postExecute(String output) throws JSONException, IOException {
		// TODO Auto-generated method stub
		
	}
	

}
