package asian.mike.perphekt.custom.threads;

import android.os.AsyncTask;
import android.util.Log;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import asian.mike.perphekt.CustomActivity;
import asian.mike.perphekt.constants.ServerAddress;

/**
 * Connects to the EC2 server
 * @author Michael
 *
 */
public class HTTPPostRegister extends AsyncTask<String, Void, Void> {
    private CustomActivity activity;
    String output=null;
    private JSONObject data = null;

    public HTTPPostRegister(CustomActivity activity, JSONObject data)
    {
        this.activity=activity;
        this.data = data;

    }
    @Override
    protected Void doInBackground(String... arg0) {
        HttpClient httpclient = new DefaultHttpClient();
        HttpPost httppost = new HttpPost(ServerAddress.djangoAddress + "/register/");
        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
        try {
            StringEntity dataString = new StringEntity(data.toString());
            httppost.setEntity(dataString);
            httppost.setHeader("Accept", "application/json");
            httppost.setHeader("Content-type", "application/json");
        } catch (IOException e) {
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
        if(output==null)
        {
            return;
        }
        try {
            Log.i("output for register", output);
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