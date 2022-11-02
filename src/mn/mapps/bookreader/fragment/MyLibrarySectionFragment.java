package mn.mapps.bookreader.fragment;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import mn.mapps.bookreader.R;
import mn.mapps.bookreader.adapter.ArrayAdapterForLibraryList;
import mn.mapps.bookreader.service.BookService;
import mn.mapps.bookreader.util.Util;
import mn.mapps.bookreader.vo.BookVO;
import nl.siegmann.epublib.domain.Book;
import nl.siegmann.epublib.domain.Resource;
import nl.siegmann.epublib.epub.EpubReader;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

public class MyLibrarySectionFragment extends Fragment {

	public static final String TAG = MyLibrarySectionFragment.class.getSimpleName();
	public static int UPDATE_LIBRARY_LIST = 100;
	public static int totalBookNumberOnAShelf = 3;
	private BookService bookService;
	private ArrayList<HashMap<String, String>> bookArray;
	private static Context context;
	private static ArrayList<BookVO> bookVoArray;
	private static BookVO bookVO;
	private static Util util;
	private static ArrayList<ArrayList<BookVO>> bookArrayList;
	private static ArrayAdapterForLibraryList adapterItem;
	private static ListView listView;

	public MyLibrarySectionFragment() {
	}
	
	public static final Handler handler = new Handler() {

		public void handleMessage(Message msg) {

			if (msg.what == UPDATE_LIBRARY_LIST){
				viewDownloadedBookList();
			}
		}
	};
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.parentlist, container, false);
		listView = (ListView)rootView.findViewById(R.id.lv_booklist);
		listView.setBackgroundResource(R.drawable.bookshelf_background_withoutshelf);
		
		context = this.getActivity();
		bookService = new BookService();
		util = new Util();
		
		util.showProgressDialog(context);
		viewDownloadedBookList();
//		viewLibraryBookList_backup(listView, adapterItem, util.getJsonBookListForTest());
		util.hideProgressDialog();
		
		Log.i(TAG, "MyLibrarySectionFragment -> onCreateView");
		
		return rootView;
	}

	private static void viewDownloadedBookList() {
		bookArrayList = new ArrayList<ArrayList<BookVO>>();
		adapterItem = new ArrayAdapterForLibraryList(context, R.layout.bookshelf, bookArrayList);
		
		String rootDir = util.getRootDirName();
		File dir = new File(Environment.getExternalStorageDirectory() + "/" + rootDir);
		
		if(dir.exists()){
			File[] files = dir.listFiles();
			
			int counter = 0;
			int fileLength = files.length;
			
			for (File file : files) {
			    if (file.isDirectory()) {
			    	
			    	if(0 == counter){
						bookVoArray = new ArrayList<BookVO>();
			    	}
			    	
		    		bookVO = new BookVO();		    		
		    		String[] splitedValue = file.getName().split("#ID#");
		    		
		    		String book_name = splitedValue[0];
					bookVO.setBook_name(book_name);
		    		bookVO.setBook_id(splitedValue[1]);
		    		
		    		try {
			    		File bookFile= new File(Environment.getExternalStorageDirectory() + "/" + rootDir +"/"+file.getName()+"/"+util.getEpub_FileDirName()+"/"+book_name +".epub");
			    		FileInputStream fileInputStream = new FileInputStream(bookFile); 
						Book bookEpub = (new EpubReader()).readEpub(fileInputStream);
						
						Resource coverImage = bookEpub.getCoverImage();
						
						Bitmap bmp = BitmapFactory.decodeStream(coverImage.getInputStream());
						bookVO.setCover_pic(bmp);
						
					} catch (IOException e) {
						e.printStackTrace();
					}
		    		
		    		bookVoArray.add(bookVO);
		    		counter++;
		    		--fileLength;
			    		
		    		Log.i(TAG, "files.length: " + fileLength);
					if(counter == totalBookNumberOnAShelf  || fileLength == 0){
						adapterItem.add(bookVoArray);
						counter = 0;
					}
			    }
			}
			if(files.length <= 3){
				bookVoArray = new ArrayList<BookVO>();
				adapterItem.add(bookVoArray);
			}
		}else{
			bookVoArray = new ArrayList<BookVO>();
			adapterItem.add(bookVoArray);
			bookVoArray = new ArrayList<BookVO>();
			adapterItem.add(bookVoArray);
		}
		
		listView.setAdapter(adapterItem);
	}
	
	private void viewLibraryBookList_backup(ListView listView, ArrayAdapterForLibraryList adapterItem, String jsonBookList) {
		try {
			JSONArray bookListArray = new JSONArray(jsonBookList);
					
			double totalBookCount = bookListArray.length();
			int shelfCount = (int) Math.ceil(totalBookCount / totalBookNumberOnAShelf); 
			Log.i(TAG, "bookshelf count: " + shelfCount);
			
			if(totalBookCount <= 3){
				shelfCount += 1;
			}
			
			int totalCount = (int) totalBookCount;
			int position = 0; 
			int counter = 0;
			
			while(totalCount != 0){
				bookVoArray = new ArrayList<BookVO>();
				ArrayList<HashMap<String, String>> bookArray = new ArrayList<HashMap<String, String>>();
				
				for(int j = position; j < totalBookCount; j++){
					
					if(totalBookNumberOnAShelf == counter){
						counter = 0;
						break;
					}
					HashMap<String, String> map = new HashMap<String, String>();
					JSONObject c = null;
					
					try{
						c = bookListArray.getJSONObject(j);
					}catch(Exception ex){
						break;
					}
					
					@SuppressWarnings("unchecked")
					Iterator<String> iter = c.keys();
					while(iter.hasNext()){
						String currentKey = iter.next();
						map.put(currentKey, c.getString(currentKey));
					}

					bookArray.add(map);
					bookVoArray.add(new BookVO());							
					totalCount--;
					position = j+1;
					counter++;
				}
				
				bookService.setBookArrayItemValues(bookVoArray, bookArray);
				adapterItem.add(bookVoArray);
			}
				
			listView.setAdapter(adapterItem);

		} catch (JSONException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
