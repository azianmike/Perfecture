package asian.mike.perfak.custom.processed.gallery;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Collections;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.PopupWindow;
import asian.mike.perfak.ImagePagerActivity;
import asian.mike.perfak.R;
import asian.mike.perfak.constants.Action;
import asian.mike.perfak.constants.ProcessGCMBundle;
import asian.mike.perfak.custom.gallery.CustomGallery;
import asian.mike.perfak.custom.gallery.GalleryAdapter;

import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiscCache;
import com.nostra13.universalimageloader.cache.memory.impl.WeakMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.assist.PauseOnScrollListener;
import com.nostra13.universalimageloader.utils.StorageUtils;

public class ShowSingleProcessedImages extends Activity {

	GridView gridGallery;
	Handler handler;
	GalleryAdapter adapter;

	private ImageView imgNoMedia;
	Button btnGalleryOk;

	String action;
	private ImageLoader imageLoader;
	private int imagePosition;
	private ArrayList<CustomGallery> galleryList;
	private String[] galleryListArray;
	private Context thisContext;
	private ArrayList<String> galleryListString;
	private View viewParent;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.processed_gallery);
		
		viewParent = getWindow().getDecorView().findViewById(android.R.id.content);
		Bundle extras = getIntent().getExtras();
		if (extras != null) {
		    imagePosition = extras.getInt("imagePosition");
		}
		
		action = getIntent().getAction();
		if (action == null) {
			finish();
		}
		initImageLoader();
		init();
	}

	
	private void initImageLoader() {
		try {
			String CACHE_DIR = Environment.getExternalStorageDirectory()
					.getAbsolutePath() + "/.temp_tmp";
			new File(CACHE_DIR).mkdirs();

			File cacheDir = StorageUtils.getOwnCacheDirectory(getBaseContext(),
					CACHE_DIR);

			DisplayImageOptions defaultOptions = new DisplayImageOptions.Builder()
					.cacheOnDisc(true).imageScaleType(ImageScaleType.EXACTLY)
					.bitmapConfig(Bitmap.Config.RGB_565).build();
			ImageLoaderConfiguration.Builder builder = new ImageLoaderConfiguration.Builder(
					getBaseContext())
					.defaultDisplayImageOptions(defaultOptions)
					.discCache(new UnlimitedDiscCache(cacheDir))
					.memoryCache(new WeakMemoryCache());

			ImageLoaderConfiguration config = builder.build();
			imageLoader = ImageLoader.getInstance();
			imageLoader.init(config);

		} catch (Exception e) {

		}
	}

	private void init() {

		handler = new Handler();
		gridGallery = (GridView) findViewById(R.id.gridGallery);
		gridGallery.setFastScrollEnabled(true);
		adapter = new GalleryAdapter(getApplicationContext(), imageLoader);
		PauseOnScrollListener listener = new PauseOnScrollListener(imageLoader,
				true, true);
		gridGallery.setOnScrollListener(listener);

		if(action.equalsIgnoreCase(Action.SHOW_SINGLE_LIST_PROCESSED_IMAGES))
		{
			findViewById(R.id.llBottomContainer).setVisibility(View.GONE);
			thisContext = this;
			gridGallery.setOnItemLongClickListener(mItemLongClickListener);
			gridGallery.setOnItemClickListener(mItemSingleClickListener);

			adapter.setMultiplePick(false);
		}

		gridGallery.setAdapter(adapter);
		imgNoMedia = (ImageView) findViewById(R.id.imgNoMedia);

		btnGalleryOk = (Button) findViewById(R.id.btnGalleryOk);
		btnGalleryOk.setOnClickListener(mOkClickListener);

		new Thread() {

			@Override
			public void run() {
				Looper.prepare();
				handler.post(new Runnable() {

					@Override
					public void run() {
						adapter.addAll(getGalleryPhotos());
						checkImageStatus();
					}
				});
				Looper.loop();
			};

		}.start();

	}

	private void checkImageStatus() {
		if (adapter.isEmpty()) {
			imgNoMedia.setVisibility(View.VISIBLE);
		} else {
			imgNoMedia.setVisibility(View.GONE);
		}
	}

	View.OnClickListener mOkClickListener = new View.OnClickListener() {

		@Override
		public void onClick(View v) {
			ArrayList<CustomGallery> selected = adapter.getSelected();

			String[] allPath = new String[selected.size()];
			for (int i = 0; i < allPath.length; i++) {
				allPath[i] = selected.get(i).sdcardPath;
			}

			Intent data = new Intent().putExtra("all_path", allPath);
			setResult(RESULT_OK, data);
			finish();

		}
	};

	AdapterView.OnItemClickListener mItemSingleClickListener = new AdapterView.OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> l, View v, int position, long id) {

			Log.i("clicked image", Integer.toString(position));
			Log.i("clicked image", galleryList.get(position).sdcardPath);
			Intent intent = new Intent(thisContext, ImagePagerActivity.class);
			String[] galleryListToString = (String[]) galleryListString.toArray(new String[galleryListString.size()]);
			intent.putExtra("images", galleryListToString);
			intent.putExtra("pagerPosition", imagePosition);
			startActivity(intent);
		}
	};

	AdapterView.OnItemLongClickListener mItemLongClickListener = new AdapterView.OnItemLongClickListener() {

		@Override
		public boolean onItemLongClick(AdapterView<?> l, View v, final int position, long id) {

			Log.i("long clicked image", Integer.toString(position));
			Log.i("long clicked image", galleryList.get(position).sdcardPath);
			Log.i("view", viewParent.toString());
			android.app.AlertDialog.Builder alert = new AlertDialog.Builder(thisContext);
	        alert.setTitle("Test");
	        alert.setMessage("Test");
	        alert.setPositiveButton("Choose this image!", new OnClickListener() {
				
				@SuppressWarnings("resource")
				@Override
				public void onClick(DialogInterface arg0, int arg1) {
					// TODO Auto-generated method stub
					CustomGallery imageToCopy =  galleryList.get(position);
					int lastSlash = imageToCopy.sdcardPath.lastIndexOf("/");
					String filename = imageToCopy.sdcardPath.substring(lastSlash);
					String folderPath = Environment.getExternalStorageDirectory()+"/Pictures/Perfaakt/";
					String newImagepath = folderPath+filename;
			        File source= new File(imageToCopy.sdcardPath);
			        File destination= new File(newImagepath);
			        File dir = new File(folderPath);
			        if(!dir.exists())
			        {
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
			        ProcessGCMBundle.removeArraylist(imagePosition);
					finish();
				}
			});
	        alert.setNegativeButton("Cancel", null);
	        alert.show();
			return true;
		}
	};
	
	
	
	private ArrayList<CustomGallery> getGalleryPhotos() {
		galleryList = ProcessGCMBundle.getListOfImages(imagePosition);
		galleryListString = ProcessGCMBundle.getArrayListStrings(imagePosition);
		return galleryList;
	}

}