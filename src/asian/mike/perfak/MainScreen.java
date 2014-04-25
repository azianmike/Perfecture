package asian.mike.perfak;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.UnknownHostException;
import java.util.ArrayList;



import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.MediaStore.Images.Media;
import android.app.Activity;
import android.content.ContextWrapper;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import asian.mike.perfak.R;




public class MainScreen extends Activity {
	
	public static final int GALLERY_CODE = 322;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main_screen);
		
	    //sendImageData();
		
		setButtonListeners();
	}

	private void setButtonListeners() {
		Button submit=(Button) findViewById(R.id.registerButton);
		submit.setOnClickListener(new View.OnClickListener() {
		    @Override
		    public void onClick(View v) {
		        changeRegisterScreen();
		    }
		});
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
//	    ByteArrayOutputStream stream = new ByteArrayOutputStream();
//		bitmap.compress(Bitmap.CompressFormat.JPEG, 90, stream);
//		byte[] byteArray = stream.toByteArray();
//		String strBase64Image=Base64.encodeToString(byteArray, 0);  //converts image to base 64
//	    test.execute(strBase64Image+"\r\n\r\n");
//		
//	    try {
//			bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), link2);
//			bitmap=Bitmap.createScaledBitmap(bitmap, 100, 100, true);
//		} catch (FileNotFoundException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//
//	    //test = new ClientThread();
//	    stream = new ByteArrayOutputStream();
//		bitmap.compress(Bitmap.CompressFormat.JPEG, 90, stream);
//		byteArray = stream.toByteArray();
//		strBase64Image=Base64.encodeToString(byteArray, 0);  //converts image to base 64
//	    //test.execute(strBase64Image+"\r\n\r\n");
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main_screen, menu);
		return true;
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
}
