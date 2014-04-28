package asian.mike.perfak;

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
				serverAddr = InetAddress.getByName(ServerAddress.address);

				socket = new Socket(serverAddr, 5069);
				DataInputStream in = new DataInputStream(socket.getInputStream());
				
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
			images.put(imageData);
			JSONObject fullData = new JSONObject();
			fullData.put("userID", UserID.userID);
			fullData.put("function", "upload");
			fullData.put("image", images);
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
            Log.i("####### file length = ", String.valueOf(myFile.length()) );
            FileInputStream fis = new FileInputStream(myFile);
            BufferedInputStream bis = new BufferedInputStream(fis);
            bis.read(mybytearray,0,mybytearray.length);
			return Base64.encodeToString(mybytearray, 0);
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

	    }
		
	}