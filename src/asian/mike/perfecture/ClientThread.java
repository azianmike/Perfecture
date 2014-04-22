package asian.mike.perfecture;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;

import org.json.JSONException;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;

/**
	 * Connects to the EC2 server
	 * @author Michael
	 * Michael <3 Liz :)
	 */
	class ClientThread extends AsyncTask<String, Void, Void> {

		String output=null;
		private Socket socket;
		private ArrayList<String> results;
		ContentResolver currContext ;
		public ClientThread(ArrayList<String> result, ContentResolver currContext){
			this.results = result;
			this.currContext = currContext;
		}
		
		@Override
		protected Void doInBackground(String... arg0) {

			InetAddress serverAddr;
			try {
				serverAddr = InetAddress.getByName("54.86.52.137");

				socket = new Socket(serverAddr, 5069);
				DataInputStream in = new DataInputStream(socket.getInputStream());
				
				DataOutputStream os = new DataOutputStream(socket.getOutputStream());
				PrintWriter pw = new PrintWriter(os);
				String dataToSend = getImageData();
				//os.write(dataToSend);
				pw.print(dataToSend);
				
				
				
				//Log.i("image", arg0[0]);
				pw.flush();
				//os.flush();
				
				//BufferedReader is = new BufferedReader(new InputStreamReader(in));
				//output= is.readLine();

				
					
				
			} catch (UnknownHostException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 
			
			return null;
		}

		private String getImageData() throws FileNotFoundException, IOException {
//			Uri link = Uri.parse("file://"+results.remove(0));
//			Bitmap bitmap = MediaStore.Images.Media.getBitmap(currContext, link);
//			ByteArrayOutputStream stream = new ByteArrayOutputStream();
//			bitmap.compress(Bitmap.CompressFormat.JPEG, 90, stream);
//			byte[] byteArray = stream.toByteArray();
//			String strBase64Image=Base64.encodeToString(byteArray, 0);  //converts image to base 64
			File myFile = new File (results.remove(0));
			byte [] mybytearray  = new byte [(int)myFile.length()];
            Log.i("####### file length = ", String.valueOf(myFile.length()) );
            FileInputStream fis = new FileInputStream(myFile);
            BufferedInputStream bis = new BufferedInputStream(fis);
            bis.read(mybytearray,0,mybytearray.length);
			return Base64.encodeToString(mybytearray, 0)+"\r\n\r\n";
		}
		
		@Override
	    protected void onPostExecute(Void result) {
			Log.i("post execute", "post execute");
			try {
				if(socket != null)
					socket.close();
				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if(results.size() == 0)
				return;
			else
			{
				ClientThread newThread = new ClientThread(results, currContext);
				newThread.execute();
			}
			
	    }
		
		
		@Override
		protected void onPreExecute() {
			//pd = new ProgressDialog(activity.getApplicationContext());
	    }
		
	}