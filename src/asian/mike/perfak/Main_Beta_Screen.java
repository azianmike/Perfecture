package asian.mike.perfak;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import asian.mike.perfak.constants.UserID;

public class Main_Beta_Screen extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main__beta__screen);
		Log.i("userid", UserID.userID);
		if(UserID.userID != null && UserID.userID != "-1")
		{
			    Intent i = new Intent(getApplicationContext(), MainScreen.class);
			    startActivity(i);
			
		}
	}
}
