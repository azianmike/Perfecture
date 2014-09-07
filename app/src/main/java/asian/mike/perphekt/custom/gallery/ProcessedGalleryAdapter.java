package asian.mike.perphekt.custom.gallery;
import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import java.util.ArrayList;

import asian.mike.perphekt.R;
import asian.mike.perphekt.constants.ProcessGCMBundle;

public class ProcessedGalleryAdapter extends BaseAdapter {

	private Context mContext;
	private LayoutInflater infalter;
	private ArrayList<CustomGallery> data = new ArrayList<CustomGallery>();
	ImageLoader imageLoader;

	private boolean isActionMultiplePick;
	private int count = 0;

	public ProcessedGalleryAdapter(Context c, ImageLoader imageLoader) {
		infalter = (LayoutInflater) c
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		mContext = c;
		this.imageLoader = imageLoader;
		clearCache();
	}

	@Override
	public int getCount() {
		return data.size();
	}

	@Override
	public CustomGallery getItem(int position) {
		return data.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	public void setMultiplePick(boolean isMultiplePick) {
		this.isActionMultiplePick = isMultiplePick;
	}

	public void selectAll(boolean selection) {
		for (int i = 0; i < data.size(); i++) {
			data.get(i).isSelected = selection;

		}
		notifyDataSetChanged();
	}

	public boolean isAllSelected() {
		boolean isAllSelected = true;

		for (int i = 0; i < data.size(); i++) {
			if (!data.get(i).isSelected) {
				isAllSelected = false;
				break;
			}
		}

		return isAllSelected;
	}

	public boolean isAnySelected() {
		boolean isAnySelected = false;

		for (int i = 0; i < data.size(); i++) {
			if (data.get(i).isSelected) {
				isAnySelected = true;
				break;
			}
		}

		return isAnySelected;
	}

	public ArrayList<CustomGallery> getSelected() {
		ArrayList<CustomGallery> dataT = new ArrayList<CustomGallery>();

		for (int i = 0; i < data.size(); i++) {
			if (data.get(i).isSelected) {
				dataT.add(data.get(i));
			}
		}

		return dataT;
	}

	public void addAll(ArrayList<CustomGallery> files) {

		try {
			clear();
			this.data.addAll(files);

		} catch (Exception e) {
			e.printStackTrace();
		}

		notifyDataSetChanged();
	}

	public void changeSelection(View v, int position) {

		if (data.get(position).isSelected) {
			data.get(position).isSelected = false;
		} else {
			data.get(position).isSelected = true;
		}

		((ViewHolder) v.getTag()).imgQueueMultiSelected.setSelected(data
				.get(position).isSelected);
	}

	@SuppressLint("NewApi")
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		final ViewHolder holder;

		if (convertView != null && ((ViewHolder) convertView.getTag()).textSelected.getText().equals(Integer.toString(ProcessGCMBundle.getLengthOfArrayList(position))))
        {
            holder = (ViewHolder) convertView.getTag();
		} else {
            convertView = infalter.inflate(R.layout.gallery_item, null);
            holder = new ViewHolder();
            holder.imgQueue = (ImageView) convertView
                    .findViewById(R.id.imgQueue);

            holder.imgQueueMultiSelected = (ImageView) convertView
                    .findViewById(R.id.imgQueueMultiSelected);

            holder.textSelected = (TextView)convertView
                    .findViewById(R.id.textSelected);

            holder.imgQueueMultiSelected.setVisibility(View.VISIBLE);
            holder.textSelected.setVisibility(View.VISIBLE);

            int numberProcessed = ProcessGCMBundle.getLengthOfArrayList(position);
            holder.textSelected.setText(Integer.toString(numberProcessed));

            holder.textSelected.measure(0,0);
            holder.imgQueueMultiSelected.measure(0, 0);
            holder.textSelected.setX(calculateCenterX(holder.imgQueueMultiSelected, holder.textSelected));
            holder.textSelected.setY(getCenterHorizontal(holder.imgQueueMultiSelected));

            convertView.setTag(holder);
		}
		holder.imgQueue.setTag(position);

		try {

			imageLoader.displayImage("file://" + data.get(position).sdcardPath,
					holder.imgQueue, new SimpleImageLoadingListener() {
						@Override
						public void onLoadingStarted(String imageUri, View view) {
							holder.imgQueue
									.setImageResource(R.drawable.no_media);
							super.onLoadingStarted(imageUri, view);
						}
					});

			if (isActionMultiplePick) {

				holder.imgQueueMultiSelected
						.setSelected(data.get(position).isSelected);

			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return convertView;
	}
	
	private float getCenterHorizontal(ImageView img)
	{
		float centerXOnImage=img.getWidth()/2;
		float centerXOfImageOnScreen=img.getLeft()+centerXOnImage;
		
		return centerXOfImageOnScreen;
	}
	
	private float calculateCenterX(ImageView img, TextView txt)
	{
		//float centerVertical = getCenterVertical(img);
		float centerHorizontal = getCenterHorizontal(img);
		
		float txtHorizontal = txt.getMeasuredWidth()/2;
		
		if (txt.getText().length()>=2)
		{
			txtHorizontal = 0f;
		}

		return centerHorizontal - txtHorizontal;
		
	}
	
	public class ViewHolder {
		ImageView imgQueue;
		ImageView imgQueueMultiSelected;
		TextView textSelected;
	}

	public void clearCache() {
		imageLoader.clearDiscCache();
		imageLoader.clearMemoryCache();
	}

	public void clear() {
		data.clear();
		notifyDataSetChanged();
	}
}