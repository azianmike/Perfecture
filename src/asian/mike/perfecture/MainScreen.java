package asian.mike.perfecture;

import java.io.FileNotFoundException;
import java.io.IOException;
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
import android.widget.ImageView;
import java.io.ByteArrayOutputStream;


public class MainScreen extends Activity {
	
	public static final int GALLERY_CODE = 322;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main_screen);
		
	    String[] projection = {MediaStore.Images.Media.DATA};

		ContextWrapper context = (ContextWrapper) getApplicationContext();
		final Cursor cursor = context.getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
	            projection,
	            null,
	            null,
	            null);

	    ArrayList<String> result = new ArrayList<String>(cursor.getCount());

	    Log.i("cursor.getCount()) :", cursor.getCount() + "");

	    if (cursor.moveToFirst()) {
	        final int dataColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
	        do {
	            final String data = cursor.getString(dataColumn);
	            Log.i ("data :",data );
	            result.add(data);
	        } while (cursor.moveToNext());
	    }
	    cursor.close();
	    
	    ImageView firstImageView = (ImageView) findViewById(R.id.imageView1);
	    Uri link = Uri.parse("file://"+result.get(0));
	    Uri link2 = Uri.parse("file://"+result.get(1));
	    Bitmap bitmap = null;

	    try {
			bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), link);
			bitmap=Bitmap.createScaledBitmap(bitmap, 100, 100, true);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    firstImageView.setImageBitmap(bitmap);
	    
	    ClientThread test = new ClientThread();
	    ByteArrayOutputStream stream = new ByteArrayOutputStream();
		bitmap.compress(Bitmap.CompressFormat.JPEG, 90, stream);
		byte[] byteArray = stream.toByteArray();
		String strBase64Image=Base64.encodeToString(byteArray, 0);  //converts image to base 64
	    test.execute(strBase64Image+"\r\n\r\n");
		
	    try {
			bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), link2);
			bitmap=Bitmap.createScaledBitmap(bitmap, 100, 100, true);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    test = new ClientThread();
	    stream = new ByteArrayOutputStream();
		bitmap.compress(Bitmap.CompressFormat.JPEG, 90, stream);
		byteArray = stream.toByteArray();
		strBase64Image=Base64.encodeToString(byteArray, 0);  //converts image to base 64
	    test.execute(strBase64Image+"\r\n\r\n");
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main_screen, menu);
		return true;
	}


}
