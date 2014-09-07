package asian.mike.perphekt;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.nostra13.universalimageloader.core.ImageLoader;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.ArrayList;

import asian.mike.perphekt.constants.ProcessGCMBundle;
import asian.mike.perphekt.custom.gallery.CustomGallery;

public abstract class BaseActivity extends Activity {

	protected ImageLoader imageLoader = ImageLoader.getInstance();

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		return false;
	}

    protected void saveChosenImage(ArrayList<CustomGallery> galleryList, int pagerPosition, int arrayListPosition) {
        CustomGallery imageToCopy = galleryList.get(pagerPosition);
        int lastSlash = imageToCopy.sdcardPath.lastIndexOf("/");
        String filename = imageToCopy.sdcardPath.substring(lastSlash);
        String folderName = getFolderNameFromSharedPref();
        String folderPath = Environment.getExternalStorageDirectory() + "/Pictures/"+folderName+"/";
        String newImagepath = folderPath + filename;
        File source = new File(imageToCopy.sdcardPath);
        File destination = new File(newImagepath);
        File dir = new File(folderPath);
        if (!dir.exists()) {
            boolean returned = dir.mkdirs();
            Log.i("dir exists", Boolean.toString(dir.exists()));
            Log.i("dir return", Boolean.toString(returned));
        }
        if (source.exists()) {

            try {
                destination.createNewFile();
                FileChannel src = new FileInputStream(source).getChannel();
                FileChannel dst = new FileOutputStream(destination).getChannel();
                dst.transferFrom(src, 0, src.size());
                src.close();
                dst.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
        sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(destination)));
        ProcessGCMBundle.removeArraylist(arrayListPosition);
        finishActivity(10);
        finish();
    }

    private String getFolderNameFromSharedPref()
    {
        final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);

        String folderName = prefs.getString("folderName", "Perphekt");
        return folderName;
    }
}