package mn.mapps.bookreader.fragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import mn.mapps.bookreader.R;
import mn.mapps.bookreader.adapter.ArrayAdapterForNewBookList;
import mn.mapps.bookreader.connection.AsyncCallBack;
import mn.mapps.bookreader.controller.ActionController;
import mn.mapps.bookreader.service.BookService;
import mn.mapps.bookreader.util.Util;
import mn.mapps.bookreader.vo.BookVO;

import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

public class NewBookSectionFragment extends Fragment {
	
	public static final String TAG = NewBookSectionFragment.class.getSimpleName();
	private BookService bookService;
	private ArrayList<BookVO> bookVoArray;
	private ArrayAdapterForNewBookList adapterItem;
	private ActionController actionController;
	private Util util;
	private Context context;

	public NewBookSectionFragment() {
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		
		View rootView = inflater.inflate(R.layout.parentfornewlist, container, false);
		actionController = new ActionController(getActivity());
		util = new Util();
		context = this.getActivity();
		
		util.showProgressDialog(context);
		String userEmail = util.getCacheData(getActivity(), "user_email", "");
		actionController.getNewBooks(userEmail, 0, 10, new AsyncCallBack<String>(){

			@Override
			public void onSuccess(final String result) {
				Log.i(TAG, "getNewBooks Succeeded: \n\n" + result);
				
				FragmentActivity activity = getActivity();
				if(activity != null){
					activity.runOnUiThread(new Runnable(){
						public void run(){
							viewList(result);
							util.hideProgressDialog();
	                    }
	                });
				}else{
					Log.i(TAG, "[@ getNewBooks] activity is NULL!");
					util.hideProgressDialog();
				}
			}

			@Override
			public void onFailure(Throwable th) {
				Log.i(TAG, "Error on isDownloadFree..");
				util.hideProgressDialog();
				th.printStackTrace();
			}
		});
		
		ArrayList<ArrayList<BookVO>> bookArrayList = new ArrayList<ArrayList<BookVO>>();
		adapterItem = new ArrayAdapterForNewBookList(getActivity(), R.layout.newlist, bookArrayList);

		ListView listView = (ListView)rootView.findViewById(R.id.lv_newbookslist);
		listView.setAdapter(adapterItem);
		
		return rootView;
	}
	
	public void viewList(String result){
		if(result != null){
			
			try {
				JSONObject jsonObject = new JSONObject(result);
				
				if(jsonObject == null || jsonObject.isNull("list"))
					return;
					
				JSONArray bookListArray = jsonObject.getJSONArray("list");
				
				for(int i=0; i < bookListArray.length(); i++){ // makes a row as much as list length 
					bookVoArray = new ArrayList<BookVO>();
					
					JSONObject objectList = bookListArray.getJSONObject(i);
					
					if(objectList == null || objectList.isNull("book_list"))
						return;
					
					JSONArray arrayBookList = objectList.getJSONArray("book_list");
					
					if(arrayBookList == null || arrayBookList.isNull(0))
						return;
					
					ArrayList<HashMap<String, String>> bookArray = new ArrayList<HashMap<String, String>>();
					
					for(int j=0; j < arrayBookList.length(); j++){ // makes a column as much as bookList length 
		
						HashMap<String, String> map = new HashMap<String, String>();
						JSONObject c = arrayBookList.getJSONObject(j);;
						
						@SuppressWarnings("unchecked")
						Iterator<String> iter = c.keys();
						while(iter.hasNext()){
							String currentKey = iter.next();
							map.put(currentKey, c.getString(currentKey));
						}
		
						if(null != objectList.getString("category_name")){
							map.put("category_name", objectList.getString("category_name"));
							map.put("category_id", objectList.getString("category_id"));
							
							if(null != objectList.getString("sub_text"))
								map.put("subText", objectList.getString("sub_text"));
						}
						
						bookArray.add(map);
						bookVoArray.add(new BookVO());		
					}
					
					bookService = new BookService();
					bookService.setBookArrayItemValues(bookVoArray, bookArray);
					adapterItem.add(bookVoArray);
				}
			} catch (Exception e) {
				e.printStackTrace();
			} 
		}else{
			
		}
	}
}
