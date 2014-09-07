package asian.mike.perphekt.custom.processed.gallery;


import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;

import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiscCache;
import com.nostra13.universalimageloader.cache.memory.impl.WeakMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.listener.PauseOnScrollListener;
import com.nostra13.universalimageloader.utils.StorageUtils;

import java.io.File;
import java.util.ArrayList;

import asian.mike.perphekt.BaseActivity;
import asian.mike.perphekt.R;
import asian.mike.perphekt.constants.Action;
import asian.mike.perphekt.constants.ProcessGCMBundle;
import asian.mike.perphekt.custom.gallery.CustomGallery;
import asian.mike.perphekt.custom.gallery.GalleryAdapter;

public class SingleListProcessedActivity extends BaseActivity {

	GridView gridGallery;
	Handler handler;
	GalleryAdapter adapter;

	private ImageView imgNoMedia;
	Button btnGalleryOk;

	String action;
	private ImageLoader imageLoader;
	private int listPosition;
	private ArrayList<CustomGallery> galleryList;
	private String[] galleryListArray;
	private Context thisContext;
	private ArrayList<String> galleryListString;
	private View viewParent;
    private Menu menu;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        getActionBar().setDisplayShowTitleEnabled(false);
		setContentView(R.layout.processed_gallery);
		
		viewParent = getWindow().getDecorView().findViewById(android.R.id.content);
		Bundle extras = getIntent().getExtras();
		if (extras != null) {
		    listPosition = extras.getInt("listPosition");
		}
		
		action = getIntent().getAction();
		if (action == null) {
			finish();
		}
		initImageLoader();
		init();
	}

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == 10 || requestCode == 10)
        {
            Log.i("image picked", "image picked");
            finish();
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.processed_gallery_menu, menu);
        this.menu=menu;
        MenuItem clearAll = menu.findItem(R.id.clearAll);
        clearAll.setVisible(true);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.clearAll:
                clearAll();
                break;
            default:
                break;
        }

        return true;
    }

    private void clearAll()
    {
        ProcessGCMBundle.removeArraylist(listPosition);
        finish();
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

			Intent intent = new Intent(thisContext, ImagePagerActivity.class);
			String[] galleryListToString = (String[]) galleryListString.toArray(new String[galleryListString.size()]);
			intent.putExtra("images", galleryListToString);
			intent.putExtra("pagerPosition", position);
            intent.putExtra("arrayListPosition", listPosition);
			startActivityForResult(intent, 10);
		}
	};

	AdapterView.OnItemLongClickListener mItemLongClickListener = new AdapterView.OnItemLongClickListener() {

		@Override
		public boolean onItemLongClick(AdapterView<?> l, View v, final int position, long id) {
            String[] tempOptions = {"Choose Image", "Not a similar image"};
            AlertDialog.Builder builder = new AlertDialog.Builder(thisContext);
            builder.setTitle("Pick option")
                    .setItems(tempOptions, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            if(which == 0)
                            {
                                Log.i("image chosen", "image chosen");
                                saveChosenImage(galleryList, position, listPosition);
                            }else if(which == 1)
                            {
                                Log.i("unsimilar image removed", "unsimilar image removed");
                                ProcessGCMBundle.removeUnsimilarImage(listPosition, position);
                                finish();
                            }
                        }
                    });

            builder.create();
            builder.show();
			return true;
		}

    };
	
	
	
	private ArrayList<CustomGallery> getGalleryPhotos() {
		galleryList = ProcessGCMBundle.getListOfImages(listPosition);
		galleryListString = ProcessGCMBundle.getArrayListStrings(listPosition);
		return galleryList;
	}

}