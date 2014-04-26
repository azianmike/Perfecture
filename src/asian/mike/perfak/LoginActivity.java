package asian.mike.perfak;

import java.io.IOException;
import java.math.BigInteger;
import java.net.UnknownHostException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;


/**
 * Activity which displays a login screen to the user, offering registration as
 * well.
 */
public class LoginActivity extends CustomActivity {
	/**
	 * The default email to populate the email field with.
	 */
	public static final String EXTRA_EMAIL = "com.example.android.authenticatordemo.extra.EMAIL";
	// UI references.
	private ClientThreadString thread;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_login);
		Button submit=(Button) findViewById(R.id.sign_in_button);
		submit.setOnClickListener(new View.OnClickListener() {
		    @Override
		    public void onClick(View v) {
		        try {
					submitLogin();
				} catch (UnknownHostException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		    }
		});		
	}

	/**
	 * hashes a password string with sha 256
	 * @param password
	 * @return
	 */
	private String getHashPassword(String password)
	{
		MessageDigest digest=null;
	    try {
	        digest = MessageDigest.getInstance("SHA-256");
	    } catch (NoSuchAlgorithmException e1) {
	        // TODO Auto-generated catch block
	        e1.printStackTrace();
	    }
	       digest.reset();
       byte[] data=digest.digest(password.getBytes());
       return String.format("%0" + (data.length*2) + "X", new BigInteger(1, data));
	}
	
	/**
	 * Failure for login, user email probably exists
	 */
	private void loginFailure()
	{
		android.app.AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle("Login failure!");
        alert.setMessage("Login failure! :(");
        alert.setPositiveButton("OK", null);
        alert.show();
	}
	
	/**
	 * Success for login!
	 */
	private void loginSuccess(String output)
	{
		android.app.AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle("Login Success!");
        alert.setMessage("Login Success! Logged in as user:"+output);
        alert.setPositiveButton("OK", new DialogInterface.OnClickListener( ){
			@Override
			public void onClick(DialogInterface arg0, int arg1) {
				try{
				    finish();
			    }
			    catch(Exception ex)
			    {
			        Log.e("main",ex.toString());
			    }
				
			}
        });
        alert.show();
        UserID.userID=output;
        Log.i("userid", UserID.userID);
	}
	
	/**
	 * Submits login data to the server
	 * @throws JSONException
	 * @throws IOException
	 */
	private void submitLogin() throws JSONException, IOException
	{
		JSONObject sendInfo=new JSONObject();
		String email= ((EditText)findViewById(R.id.email)).getText().toString();
		String password=((EditText)findViewById(R.id.password)).getText().toString();
		
		sendInfo.put("function", "login");
		sendInfo.put("user_email", email);
		sendInfo.put("password", getHashPassword(password));
		
		String sendInfoString=sendInfo.toString()+"\r\n\r\n";
		thread=new ClientThreadString(this);;
		thread.execute(sendInfoString);
	}
	
	@Override
	public void postExecute(String output) throws JSONException, IOException {
		// TODO Auto-generated method stub
		if(output==null)
			return;
		if(output.equals("-1"))
		{
			loginFailure();
		}else
		{
			loginSuccess(output);
		}
	}
}
