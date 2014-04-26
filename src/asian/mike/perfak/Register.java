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
import android.content.Intent;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import asian.mike.perfak.CustomActivity;

/**
 * Activity which displays a login screen to the user, offering registration as
 * well.
 */
public class Register extends CustomActivity {

	/**
	 * The default email to populate the email field with.
	 */
	public static final String EXTRA_EMAIL = "com.example.android.authenticatordemo.extra.EMAIL";
	private ClientThreadString thread;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_register);
		Button submit=(Button) findViewById(R.id.register_button);
		submit.setOnClickListener(new View.OnClickListener() {
		    @Override
		    public void onClick(View v) {
		        try {
					submitRegister();
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
	 * Failure for registration, user email probably exists
	 */
	private void setAlert(String message)
	{
		android.app.AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle(message);
        alert.setMessage(message);
        alert.setPositiveButton("OK", null);
        alert.show();
	}
	
	/**
	 * Success for registration!
	 */
	private void registrationSuccess()
	{
		android.app.AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle("Registration Success!");
        alert.setMessage("Registration Success!");
        alert.setPositiveButton("OK", new DialogInterface.OnClickListener( ){
			@Override
			public void onClick(DialogInterface arg0, int arg1) {
				try{
				    Intent i = new Intent(getApplicationContext(), MainScreen.class);
				    startActivity(i);
			    }
			    catch(Exception ex)
			    {
			        Log.e("main",ex.toString());
			    }
				
			}
        });
        alert.show();
	}
	
	

	/**
	 * Connects to a server to register
	 * @throws IOException 
	 * @throws UnknownHostException 
	 * @throws JSONException 
	 */
	public void submitRegister() throws UnknownHostException, IOException, JSONException
	{

		
		JSONObject sendInfo=new JSONObject();
		String email= ((EditText)findViewById(R.id.locationName)).getText().toString();
		int indexOfAtSign = email.indexOf("@");
		if(indexOfAtSign != -1 && email.indexOf(".", indexOfAtSign) != -1)
		{
			String password=((EditText)findViewById(R.id.password)).getText().toString();
			String repeatPassword=((EditText)findViewById(R.id.repeatPassword)).getText().toString();
			if(password.equals(repeatPassword)==false)
			{
				setAlert("Passwords do not match");
			}else{
				sendInfo.put("function", "register");
				sendInfo.put("user_email", email);
				sendInfo.put("password", getHashPassword(password));
				
				String sendInfoString=sendInfo.toString()+"\r\n\r\n";
				thread=new ClientThreadString(this);;
				thread.execute(sendInfoString);
			}
		}else
		{
			setAlert("Not a valid email address");
		}
	}
	
	@Override
	public void postExecute(String output) throws JSONException, IOException {
		// TODO Auto-generated method stub
		if(output==null)
			return;
		if(output.equals("1"))
		{
			registrationSuccess();
			Looper.loop();
		}else
		{
			setAlert("Registration failure!");
			Looper.loop();
		}
	}
	
}
