package asian.mike.perphekt.constants;

/**
 * Created by michaelluo on 8/9/14.
 */
public class StopUploading {
    private static boolean stopUploading = false;

    public static void setUploading(boolean set)
    {
        stopUploading = set;
    }

    public static boolean getUploading()
    {
        return stopUploading;
    }
}
