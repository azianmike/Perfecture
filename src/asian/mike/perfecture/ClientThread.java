package asian.mike.perfecture;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

import org.json.JSONException;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;

/**
	 * Connects to the EC2 server
	 * @author Michael
	 *
	 */
	class ClientThread extends AsyncTask<String, Void, Void> {
		private CustomActivity activity;
		String output=null;
		private Socket socket;

		
		@Override
		protected Void doInBackground(String... arg0) {
			InetAddress serverAddr;
			try {
				serverAddr = InetAddress.getByName("54.86.52.137");
				Log.i("connecting", "connecting");
				socket = new Socket(serverAddr, 5069);
				Log.i("connected", "connected");
				DataInputStream in = new DataInputStream(socket.getInputStream());
				
				DataOutputStream os = new DataOutputStream(socket.getOutputStream());
				PrintWriter pw = new PrintWriter(os);
				pw.println(arg0[0]);
				pw.flush();
				
				BufferedReader is = new BufferedReader(new InputStreamReader(in));
				output= is.readLine();

				
					
				
			} catch (UnknownHostException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 
			
			return null;
		}
		
		@Override
	    protected void onPostExecute(Void result) {

			try {
				socket.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if(output==null)
			{
				return;
			}

	        
	    }
		
		
		@Override
		protected void onPreExecute() {
			//pd = new ProgressDialog(activity.getApplicationContext());
			Log.i("sending test", "test");
	    }
		
	}