package asian.mike.perphekt.constants;

import android.widget.ProgressBar;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 * Created by michaelluo on 8/9/14.
 */
public class ConnectToServer {
    private static Socket socket;
    private static String forwardedPublicIP = null;

    public static void connectToServer() {
        InetAddress serverAddr;
        String serverToConnectTo = forwardedPublicIP;
        if (forwardedPublicIP == null) {
            serverToConnectTo = ServerAddress.address;
        }
        try {
            serverAddr = InetAddress.getByName(serverToConnectTo);

            socket = new Socket(serverAddr, 5069);
        } catch (UnknownHostException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public static Socket getSocket()
    {
        return socket;
    }

    public static void setForwardedPublicIP(String output)
    {
        if(output != null)
        {
            forwardedPublicIP = output;
        }else
        {
            forwardedPublicIP = null;
        }
    }

    public static void cancelUpload(ProgressBar imageUploadProgress)
    {
        StopUploading.setUploading(true);
        imageUploadProgress.setProgress(0);
    }
}
