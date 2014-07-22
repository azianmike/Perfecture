package asian.mike.perfak.constants;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;

import android.os.Bundle;
import android.util.Log;
import asian.mike.perfak.custom.gallery.CustomGallery;

public class ProcessGCMBundle {
	private static ArrayList<ArrayList<CustomGallery>> uris;
	private static ArrayList<CustomGallery> firstURIS;
	private static ArrayList<ArrayList<String>> arrayURIS;
	
	public static void setURIS(Bundle bundle) throws JSONException
	{
		if(uris == null)
		{
			uris = new ArrayList<ArrayList<CustomGallery>>();
			arrayURIS = new ArrayList<ArrayList<String>>();
		}
		String data = bundle.getString("data");
		JSONArray dataArray;
		dataArray = new JSONArray(data);

		for(int i=0; i<dataArray.length(); i++)
		{
			JSONArray elementArray = dataArray.getJSONArray(i);  //each element is a JSONArray that has all the similar images
			ArrayList<CustomGallery> tempList = new ArrayList<CustomGallery>();
			ArrayList<String> tempListString = new ArrayList<String>();
			for(int j=0; j<elementArray.length(); j++)
			{
				CustomGallery temp = new CustomGallery();
				temp.sdcardPath = elementArray.getString(j);
				tempList.add(temp);
				tempListString.add("file://"+elementArray.getString(j));
			}
			arrayURIS.add(tempListString);
			uris.add(tempList);
		}
		
		setFirstURIS();

	}
	
	public static void setFirstURIS()
	{
		if(firstURIS == null)
		{
			firstURIS = new ArrayList<CustomGallery>();
		}
		for(int i=0; i<uris.size(); i++)
		{
			ArrayList<CustomGallery> tempList = uris.get(i);
			firstURIS.add(tempList.get(0));  //puts the first entry of every similar list into firstURIS
		}
		
	}
	
	public static ArrayList<CustomGallery> getListOfImages(int index)
	{
		return uris.get(index);
	}
	
	public static ArrayList<CustomGallery> getFirstURI()
	{
		return firstURIS;
	}
	
	public static ArrayList<ArrayList<CustomGallery>> getAllURIS()
	{
		return uris;
	}
	
	public static int getLengthOfArrayList(int index)
	{
		return uris.get(index).size();
	}
	
	public static ArrayList<String> getArrayListStrings(int position)
	{
		return arrayURIS.get(position);
	}
	
	public static void removeArraylist(int position)
	{
		uris.remove(position);
		firstURIS.remove(position);
		arrayURIS.remove(position);
	}
}
