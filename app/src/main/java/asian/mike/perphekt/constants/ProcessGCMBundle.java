package asian.mike.perphekt.constants;

import android.os.Bundle;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.HashMap;

import asian.mike.perphekt.custom.gallery.CustomGallery;

public class ProcessGCMBundle {
	private static ArrayList<ArrayList<CustomGallery>> uris;
	private static ArrayList<CustomGallery> firstURIS;
	private static ArrayList<ArrayList<String>> arrayURIS;
	private static HashMap<String, Boolean> mapOfCurrentImages;
	
	public static void setURIS(Bundle bundle) throws JSONException
	{
		if(uris == null)
		{
			makeNewURI(bundle);
		}
		else
		{
			useOldURI(bundle);
		}
		
		setFirstURIS();

	}
	
	private static void useOldURI(Bundle bundle) throws JSONException
	{
		String data = bundle.getString("data");
		JSONArray dataArray= new JSONArray(data);
		
		for(int i=0; i<dataArray.length(); i++)
		{
			JSONArray elementArray = dataArray.getJSONArray(i);  //each element is a JSONArray that has all the similar images
			ArrayList<CustomGallery> tempList = new ArrayList<CustomGallery>(5);
			ArrayList<String> tempListString = new ArrayList<String>(5);
			for(int j=0; j<elementArray.length(); j++)
			{
				String tempPath =  elementArray.getString(j);
				if(!mapOfCurrentImages.containsKey(tempPath))
				{
					CustomGallery temp = new CustomGallery();
					temp.sdcardPath = tempPath;
					tempList.add(temp);
					tempListString.add("file://"+temp.sdcardPath);
                    mapOfCurrentImages.put(temp.sdcardPath, true);
				}else
				{
					continue;
				}
				
			}
			if(tempListString.size() != 0 )
			{
				arrayURIS.add(tempListString);
				uris.add(tempList);
			}
		}
	}

	private static void makeNewURI(Bundle bundle) throws JSONException {
		uris = new ArrayList<ArrayList<CustomGallery>>();
		arrayURIS = new ArrayList<ArrayList<String>>();
		mapOfCurrentImages = new HashMap<String, Boolean>();
		
		String data = bundle.getString("data");
		JSONArray dataArray;
		dataArray = new JSONArray(data);

		for(int i=0; i<dataArray.length(); i++)
		{
			JSONArray elementArray = dataArray.getJSONArray(i);  //each element is a JSONArray that has all the similar images
			ArrayList<CustomGallery> tempList = new ArrayList<CustomGallery>(5);
			ArrayList<String> tempListString = new ArrayList<String>(5);
			for(int j=0; j<elementArray.length(); j++)
			{
				CustomGallery temp = new CustomGallery();
				temp.sdcardPath = elementArray.getString(j);
				tempList.add(temp);
				tempListString.add("file://"+temp.sdcardPath);
				mapOfCurrentImages.put(temp.sdcardPath, true);
			}
			arrayURIS.add(tempListString);
			uris.add(tempList);
		}
	}
	
	public static void setFirstURIS()
	{
		firstURIS = new ArrayList<CustomGallery>();
		
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
        if(uris.get(index).size()>1)
            Log.i("size of array", Integer.toString(index) + " " + Integer.toString(uris.get(index).size()));
		return uris.get(index).size();
	}
	
	public static ArrayList<String> getArrayListStrings(int position)
	{
		return arrayURIS.get(position);
	}
	
	public static void removeArraylist(int position)
	{
		ArrayList<CustomGallery> removedURIS = uris.remove(position);
		for(CustomGallery temp : removedURIS)
		{
			mapOfCurrentImages.remove(temp.sdcardPath);
		}
		firstURIS.remove(position);
		arrayURIS.remove(position);
	}

    public static int getSize()
    {
        if(uris==null)
        {
            return 0;
        }
        return uris.size();
    }

    public static void removeUnsimilarImage(int listPosition, int imageToRemove)
    {
        ArrayList<CustomGallery> listToRemoveFrom = uris.get(listPosition);
        CustomGallery tempRemoved = listToRemoveFrom.remove(imageToRemove);

        ArrayList<CustomGallery> tempListToAdd = new ArrayList<CustomGallery>();
        tempListToAdd.add(tempRemoved);

        uris.add(tempListToAdd);
        firstURIS.add(tempRemoved);
    }
}
