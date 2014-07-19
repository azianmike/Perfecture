package asian.mike.perfak.custom.threads;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.ContentResolver;
import android.os.AsyncTask;
import android.util.Base64;
import android.util.Log;
import android.widget.ProgressBar;
import asian.mike.perfak.constants.ServerAddress;
import asian.mike.perfak.constants.UserID;

/**
	 * Connects to the EC2 server
	 * @author Michael
	 * Michael <3 Liz :)
	 */
	public class ClientThread extends AsyncTask<String, Void, Void> {

		String output=null;
		private Socket socket;
		private ArrayList<String> results;
		ContentResolver currContext ;
		ProgressBar imageUploadProgress;
		
		public ClientThread(ArrayList<String> result, ContentResolver currContext, ProgressBar imageUploadProgress){
			this.results = result;
			this.currContext = currContext;
			this.imageUploadProgress = imageUploadProgress;
		}
		
		@Override
		protected Void doInBackground(String... arg0) {

			InetAddress serverAddr;
			try {
				serverAddr = InetAddress.getByName(ServerAddress.address);

				socket = new Socket(serverAddr, 5069);

				DataOutputStream os = new DataOutputStream(socket.getOutputStream());
				PrintWriter pw = new PrintWriter(os);
				String dataToSend = getJSONString();
				pw.print(dataToSend+"\r\n\r\n");
				pw.flush();
				
					
				
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
			
			return null;
		}
		
		private String getJSONString() throws FileNotFoundException, JSONException, IOException
		{
			JSONObject imageData = new JSONObject();
			JSONArray images = new JSONArray(); 
			imageData.put(results.get(0), getImageData());
			Log.i("results length", Integer.toString(results.size()));
			images.put(imageData);
			JSONObject fullData = new JSONObject();
			fullData.put("userID", UserID.userID);
			fullData.put("gcm_ID", UserID.gcmID);
			fullData.put("function", "upload");
			fullData.put("image", images);
			fullData.put("images left", results.size());
			return fullData.toString();
		}
		
		/**
		 * Gets image data from URI
		 * @return
		 * @throws FileNotFoundException
		 * @throws IOException
		 */
		private String getImageData() throws FileNotFoundException, IOException {
			File myFile = new File (results.remove(0));
			byte [] mybytearray  = new byte [(int)myFile.length()];
            FileInputStream fis = new FileInputStream(myFile);
            BufferedInputStream bis = new BufferedInputStream(fis);
            bis.read(mybytearray,0,mybytearray.length);
            bis.close();
			return Base64.encodeToString(mybytearray, 0);
		}
		
		@Override
	    protected void onPostExecute(Void result) {
			int progress = imageUploadProgress.getProgress() + 1;
			imageUploadProgress.setProgress(progress);
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
				ClientThread newThread = new ClientThread(results, currContext, imageUploadProgress);
				newThread.execute();
			}
	    }
		
		
		@Override
		protected void onPreExecute() {

	    }
		
	}