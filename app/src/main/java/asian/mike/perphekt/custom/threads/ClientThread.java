package asian.mike.perphekt.custom.threads;

import android.content.ContentResolver;
import android.os.AsyncTask;
import android.util.Base64;
import android.util.Log;
import android.widget.ProgressBar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;

import asian.mike.perphekt.constants.ConnectToServer;
import asian.mike.perphekt.constants.StopUploading;
import asian.mike.perphekt.constants.UserID;

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
        private static boolean firstTime = true;
		
		public ClientThread(ArrayList<String> result, ContentResolver currContext, ProgressBar imageUploadProgress, boolean firstTime){
			this.results = result;
			this.currContext = currContext;
			this.imageUploadProgress = imageUploadProgress;
            this.firstTime = firstTime;
		}
		
		@Override
		protected Void doInBackground(String... arg0) {

			if(results.size()==0)
			{
				return null;
			}

			try {
                ConnectToServer.connectToServer();
				socket = ConnectToServer.getSocket();
				DataInputStream in = new DataInputStream(socket.getInputStream());
				DataOutputStream os = new DataOutputStream(socket.getOutputStream());
				PrintWriter pw = new PrintWriter(os);
				String dataToSend = getJSONString();
				pw.print(dataToSend+"\r\n\r\n");
				pw.flush();
				
				BufferedReader is = new BufferedReader(new InputStreamReader(in));
				output = is.readLine();
				Log.i("output for public ip", output);
				
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

            if(firstTime)
            {

                fullData.put("firstTime", true);
                firstTime = false;
                Log.i("switching first time to false", "false");
            }else
            {
                Log.i("switching first time to false", "false");
                fullData.put("firstTime", false);
            }
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
            String returnBase64Encoded = Base64.encodeToString(mybytearray, 0);
            myFile = null;
            mybytearray = null;
            fis = null;
            bis = null;
			return returnBase64Encoded;
		}
		
		@Override
	    protected void onPostExecute(Void result) {
            if(StopUploading.getUploading())
            {
                StopUploading.setUploading(false);
                return;
            }
			int progress = imageUploadProgress.getProgress() + 1;
			imageUploadProgress.setProgress(progress);
			
            ConnectToServer.setForwardedPublicIP(output);
			
			try {
				if(socket != null)
					socket.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if(results.size() == 0)
			{
				Log.i("done uploading images", "done uploading images");
				ConnectToServer.setForwardedPublicIP(null);
				return;
			}
			else
			{
				ClientThread newThread = new ClientThread(results, currContext, imageUploadProgress, false);
				newThread.execute();
			}
	    }
		
		
		@Override
		protected void onPreExecute() {

	    }
		
	}