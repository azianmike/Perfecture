package asian.mike.perfak;

import java.io.IOException;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.Menu;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class ShowPhotos extends CustomActivity {


	private ArrayList<String> uris;
	Bitmap thumbnail;
	

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_photos);
		
		uris = getImageURIs();
		GridView gridView = (GridView) findViewById(R.id.gridView1);
		 
        // Instance of ImageAdapter Class
        try {
			gridView.setAdapter(new ImageAdapter(this, uris, this.getContentResolver()));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		return true;
	}
	
	

	
	

	@Override
	public void postExecute(String output) throws JSONException, IOException {

	}
}
