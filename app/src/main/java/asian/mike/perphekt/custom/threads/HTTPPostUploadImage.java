package asian.mike.perphekt.custom.threads;

import android.content.ContentResolver;
import android.os.AsyncTask;
import android.util.Base64;
import android.util.Log;
import android.widget.ProgressBar;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.UnknownHostException;
import java.util.ArrayList;

import asian.mike.perphekt.CustomActivity;
import asian.mike.perphekt.constants.ConnectToServer;
import asian.mike.perphekt.constants.ServerAddress;
import asian.mike.perphekt.constants.StopUploading;
import asian.mike.perphekt.constants.UserID;

/**
 * Connects to the EC2 server
 * @author Michael
 *
 */
public class HTTPPostUploadImage extends AsyncTask<String, Void, Void> {
    private CustomActivity activity;
    String output=null;
    private JSONObject data = null;
    private ArrayList<String> results;
    ContentResolver currContext ;
    ProgressBar imageUploadProgress;
    private static boolean firstTime = true;

    public HTTPPostUploadImage(ArrayList<String> result, ContentResolver currContext, ProgressBar imageUploadProgress, boolean firstTime){
        this.results = result;
        this.currContext = currContext;
        this.imageUploadProgress = imageUploadProgress;
        this.firstTime = firstTime;
    }
    @Override
    protected Void doInBackground(String... arg0) {
        HttpClient httpclient = new DefaultHttpClient();
        HttpPost httppost = new HttpPost(ServerAddress.djangoAddress + "/upload/");
        try
        {
            StringEntity dataString = new StringEntity(getJSONString());
            httppost.setEntity(dataString);
            httppost.setHeader("Accept", "application/json");
            httppost.setHeader("Content-type", "application/json");

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



    try{
            HttpResponse response = httpclient.execute(httppost);
            output = inputStreamToString(response.getEntity().getContent()).toString();
        }catch (IOException e) {
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

    private StringBuilder inputStreamToString(InputStream is) {
        String line = "";
        StringBuilder total = new StringBuilder();
        // Wrap a BufferedReader around the InputStream
        BufferedReader rd = new BufferedReader(new InputStreamReader(is));
        // Read response until the end
        try {
            while ((line = rd.readLine()) != null) {
                total.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        // Return full string
        return total;
    }
    @Override
    protected void onPostExecute(Void result) {
        Log.i("output for register", output);
        if(StopUploading.getUploading())
        {
            StopUploading.setUploading(false);
            return;
        }
        int progress = imageUploadProgress.getProgress() + 1;
        imageUploadProgress.setProgress(progress);

        ConnectToServer.setForwardedPublicIP(output);

        if(results.size() == 0)
        {
            Log.i("done uploading images", "done uploading images");
            ConnectToServer.setForwardedPublicIP(null);
            return;
        }
        else
        {
            HTTPPostUploadImage newThread = new HTTPPostUploadImage(results, currContext, imageUploadProgress, false);
            newThread.execute();
        }
    }


    @Override
    protected void onPreExecute() {
        //pd = new ProgressDialog(activity.getApplicationContext());

    }

}