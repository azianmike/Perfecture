package asian.mike.perphekt.custom.processed.gallery;


import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;

import com.nostra13.universalimageloader.cache.memory.impl.WeakMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.listener.PauseOnScrollListener;
import com.nostra13.universalimageloader.utils.StorageUtils;

import java.io.File;
import java.util.ArrayList;

import asian.mike.perphekt.R;
import asian.mike.perphekt.constants.Action;
import asian.mike.perphekt.constants.ProcessGCMBundle;
import asian.mike.perphekt.custom.gallery.CustomGallery;
import asian.mike.perphekt.custom.gallery.ProcessedGalleryAdapter;

public class ProcessedGalleryActivity extends Activity {

	GridView gridGallery;
	Handler handler;
	ProcessedGalleryAdapter adapter;

	ImageView imgNoMedia;
	Button btnGalleryOk;

	String action;
	private ImageLoader imageLoader;
    private Context thisContext;
    private ArrayList<CustomGallery> galleryList;
    private Menu menu;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//requestWindowFeature(Window.FEATURE_NO_TITLE);
        //getActionBar().setDisplayShowTitleEnabled(false);
		setContentView(R.layout.gallery);
        setActionBarTitle();
		action = getIntent().getAction();
		Log.i("action", action);
		if (action == null) {
			finish();
		}
        setActionBarTitle();
        thisContext = this;
		initImageLoader();
		init();
	}

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.change_folder, menu);
        this.menu=menu;
        MenuItem clearAll = menu.findItem(R.id.changeFolder);
        clearAll.setVisible(true);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.changeFolder:
                changeFolder();
                break;
            default:
                break;
        }

        return true;
    }

	@Override
	public void onResume()
	{
		Log.i("on resume", "on resume");
		super.onResume();
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
		adapter = new ProcessedGalleryAdapter(getApplicationContext(), imageLoader);
		PauseOnScrollListener listener = new PauseOnScrollListener(imageLoader,
				true, true);
		gridGallery.setOnScrollListener(listener);

		if(action.equalsIgnoreCase(Action.ACTION_SHOW_PROCESSED))
		{
			
			findViewById(R.id.llBottomContainer).setVisibility(View.GONE);
			gridGallery.setOnItemClickListener(mItemMulClickListener);
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
	AdapterView.OnItemClickListener mItemMulClickListener = new AdapterView.OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> l, View v, int position, long id) {
			//adapter.changeSelection(v, position);
			//Log.i("position", Integer.toString(position));
			Log.i("position", Integer.toString(position));
			Intent i = new Intent(Action.SHOW_SINGLE_LIST_PROCESSED_IMAGES);
			i.putExtra("listPosition", position);
			startActivityForResult(i, 200);
		}
	};

	private ArrayList<CustomGallery> getGalleryPhotos() {
		galleryList = ProcessGCMBundle.getFirstURI();

		return galleryList;
	}

    private void setActionBarTitle()
    {
        ActionBar thisBar = getActionBar();
        final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);

        String folderName = prefs.getString("folderName", "Perphekt");
        thisBar.setTitle("Folder: "+folderName);
    }

    private void changeFolder()
    {
        final AlertDialog.Builder alert = new AlertDialog.Builder(this);
        final EditText input = new EditText(this);
        alert.setMessage("Change the folder you save your images to. The default is \"Perphekt\". Images can be viewed in your gallery");
        alert.setView(input);
        alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                String folderName = input.getText().toString().trim();
                Toast.makeText(getApplicationContext(), "Folder set to " + folderName, Toast.LENGTH_SHORT).show();
                setFolderPref(folderName);
                setActionBarTitle();
            }
        });

        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                dialog.cancel();
            }
        });
        alert.show();
    }

    private void setFolderPref(String folderName)
    {
        final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(thisContext);
        SharedPreferences.Editor edit = prefs.edit();
        edit.putString("folderName", folderName);
        edit.commit();
    }
}