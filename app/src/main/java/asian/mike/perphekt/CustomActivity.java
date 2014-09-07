package asian.mike.perphekt;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.ContextWrapper;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.widget.ProgressBar;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import asian.mike.perphekt.constants.StopUploading;
import asian.mike.perphekt.constants.UserID;
import asian.mike.perphekt.custom.threads.HTTPPostUploadImage;


public abstract class CustomActivity extends FragmentActivity{
	protected ProgressDialog pd;
	protected static boolean locationFound=false;
    protected static ProgressBar imageUploadProgress;
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
        getActionBar().setDisplayShowTitleEnabled(false);
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
	
	
	protected void setUserID()
	{
		UserID.userID = getUserFromDisk();
        UserID.setUserEmail(getUserEmailFromDisk());
	}

    /**
     * Saves user Id to disk so it can be retrieved later
     * @param user
     */
    protected void saveUserToDisk(String user, String userEmail)
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

        filename = "userEmail";

        try {
            outputStream = openFileOutput(filename, Context.MODE_PRIVATE);
            outputStream.write(userEmail.getBytes());
            outputStream.close();
            Log.e("written", "written "+user);
        } catch (Exception e) {
            e.printStackTrace();
        }
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

    private String getUserEmailFromDisk()
    {
        String ret="-1";
        try {
            InputStream inputStream = openFileInput("userEmail");

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

    protected void sendImageData(ArrayList<String> result, ProgressBar imageUploadProgress) {
        StopUploading.setUploading(false);
        imageUploadProgress.setMax(result.size());
        imageUploadProgress.setProgress(0);
        HTTPPostUploadImage uploadAllImages = new HTTPPostUploadImage(result, this.getContentResolver(), imageUploadProgress, true);
        uploadAllImages.execute();
    }
}
