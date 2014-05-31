package asian.mike.perfak;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.ContentResolver;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
 
@TargetApi(Build.VERSION_CODES.GINGERBREAD)
@SuppressLint("NewApi")
public class ImageAdapter extends BaseAdapter {
    private Context mContext;
 
    // Keep all Images in array
    private ArrayList<String> uris;
    private ArrayList<Bitmap> images;
    private ContentResolver cResolver;
    
    
    // Constructor
    public ImageAdapter(Context c, ArrayList<String> uris, ContentResolver cResolver) throws IOException{
        mContext = c;
        this.uris=uris;
        images=new ArrayList<Bitmap>();
        this.cResolver=cResolver;
        loadPictures();
    }
 
    @Override
    public int getCount() {
        return uris.size();
    }
 
    @Override
    public Object getItem(int position) {
        return uris.get(position);
    }
 
    @Override
    public long getItemId(int position) {
        return 0;
    }
    
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ImageView imageView = new ImageView(mContext);
        imageView.setImageBitmap(images.get(position));
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        imageView.setLayoutParams(new GridView.LayoutParams(70, 70));
        return imageView;
    	
    }
    
 
    /**
	 * Loads pictures and adds them into a view
	 * @throws IOException 
	 */
	@TargetApi(Build.VERSION_CODES.GINGERBREAD)
	@SuppressLint("NewApi")
	private void loadPictures() throws IOException{
		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
		StrictMode.setThreadPolicy(policy);
		if(uris.isEmpty())
		{
			return;
		}
		for(String s: uris)
		{
		
			Log.i("displaying image", "displaying image");
			Uri link = Uri.parse("file://"+s);
			BitmapFactory.Options bmOptions = new BitmapFactory.Options();
			bmOptions.inSampleSize = 1;
			Bitmap bmp=null;
			try {
				bmp = MediaStore.Images.Media.getBitmap( cResolver, link);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			images.add(bmp);
		}
	}
 
}