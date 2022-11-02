package mn.mapps.bookreader.fragment;

import java.util.ArrayList;

import mn.mapps.bookreader.R;
import mn.mapps.bookreader.adapter.ArrayAdapterForBookmarkedList;
import mn.mapps.bookreader.connection.AsyncCallBack;
import mn.mapps.bookreader.controller.ActionController;
import mn.mapps.bookreader.service.BookService;
import mn.mapps.bookreader.util.Util;
import mn.mapps.bookreader.vo.BookVO;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ListView;
import android.widget.Toast;

public class BookmarkedSectionFragment extends Fragment {

	private static int listStart = 0;
	private static int listEnd = 9;
	public static final String TAG = BookmarkedSectionFragment.class.getSimpleName();
	public static final String ARG_SECTION_NUMBER = "section_number";
	private BookVO book = null;
	private BookService bookService;
	private Util util;
	private ActionController actionController;
	private JSONObject bookObject;
	private ArrayAdapterForBookmarkedList adapterItem;
	private Context context; 
	
	public BookmarkedSectionFragment() {
	}
	

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.parent_marked_list, container, false);
		actionController = new ActionController(getActivity());
		util = new Util();
		context = this.getActivity();
		
		getList(listStart, listEnd);
		
		ArrayList<BookVO> bookList = new ArrayList<BookVO>();
		adapterItem = new ArrayAdapterForBookmarkedList(getActivity(), R.layout.bookitem, bookList);
		
		ListView listView = (ListView)rootView.findViewById(R.id.lv_booklist);
		listView.setAdapter(adapterItem);
		
		listView.setOnScrollListener(new OnScrollListener() {
			
			private int preLast;

			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				Log.i(TAG, "scrollState: " + scrollState);
			}
			
			@Override
			public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
				
				final int lastItem = firstVisibleItem + visibleItemCount;
				if(lastItem == totalItemCount) {
					if(preLast!=lastItem){ //to avoid multiple calls for last item
						preLast = lastItem;
						listStart += 10;
						listEnd += 10;
						Log.d(TAG, "preLast: " + preLast + ", listStart: " + listStart + ", listEnd: " + listEnd);
						getList(listStart, listEnd);
					}
				}
			}
		});
		
		return rootView;
	}


	private void getList(int start, int end) {
		util.showProgressDialog(context);
		
		String userEmail = util.getCacheData(getActivity(), "user_email", "");
		actionController.getMarkedList(userEmail, start, end, new AsyncCallBack<String>(){

			@Override
			public void onSuccess(final String result) {
				Log.i(TAG, "getMarkedList Succeeded: \n\n" + result.toString());
				
				FragmentActivity activity = getActivity();
				if(activity != null){
					getActivity().runOnUiThread(new Runnable(){
						public void run(){
							viewList(result);
							util.hideProgressDialog();
	                    }
	                });
				}else{
					Log.i(TAG, "[@ getMarkedList] activity is NULL!");
					util.hideProgressDialog();
				}
			}

			@Override
			public void onFailure(Throwable th) {
				Log.i(TAG, "Error on isDownloadFree..");
				th.printStackTrace();
			}
		});
	}

	public void viewList(String result){
		if(result != null && !result.isEmpty()){
			
			try{
				JSONArray array = new JSONArray(result);
				if(array.isNull(0)){
					getActivity().findViewById(R.id.tv_markedList_empty).setVisibility(View.VISIBLE);
					return;
				}
			}catch(Exception ex){
				getActivity().findViewById(R.id.tv_markedList_empty).setVisibility(View.VISIBLE);
				return;
			}
			
			getActivity().findViewById(R.id.tv_markedList_empty).setVisibility(View.GONE);
			try {
				JSONArray bookListArray = new JSONArray(result);
				bookService = new BookService();
				
				for(int i=0; i < bookListArray.length();i++){
					book = new BookVO();
					bookObject = bookListArray.getJSONObject(i);
					bookService.setBookItemValues(book, bookObject);
					adapterItem.add(book);
				}
			} catch (Exception e) {
				e.printStackTrace();
			} 
		}else{
			getActivity().findViewById(R.id.tv_markedList_empty).setVisibility(View.VISIBLE);
		}
	}
	
}
