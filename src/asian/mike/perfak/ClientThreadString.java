package asian.mike.perfak;

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
import android.view.Window;
import android.view.WindowManager;

/**
	 * Connects to the EC2 server
	 * @author Michael
	 *
	 */
	class ClientThreadString extends AsyncTask<String, Void, Void> {
		private CustomActivity activity;
		String output=null;
		private Socket socket;
		
		public ClientThreadString(CustomActivity activity)
		{
			this.activity=activity;
		}
		@Override
		protected Void doInBackground(String... arg0) {
			InetAddress serverAddr;
			try {
				serverAddr = InetAddress.getByName(ServerAddress.address);
				socket = new Socket(serverAddr, 5069);
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
				if(socket != null)
					socket.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if(output==null)
			{
				return;
			}
			try {
				
				if(activity.hasWindowFocus())
					activity.postExecute(output);
					
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
	        
	    }
		
		
		@Override
		protected void onPreExecute() {
			//pd = new ProgressDialog(activity.getApplicationContext());
			
	    }
		
	}