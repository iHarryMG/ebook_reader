package mn.mapps.bookreader.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import mn.mapps.bookreader.TabbedActivity;
import mn.mapps.bookreader.vo.BookVO;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

public class BookService {

	public static final String TAG = BookService.class.getSimpleName();
	private BookVO book;
	
	public BookService() {

	}
	
	public void setBookItemValues(BookVO book, JSONObject bookObjectList) throws JSONException {
		
		HashMap<String, String> map = new HashMap<String, String>();
		JSONObject c = bookObjectList;
		
		@SuppressWarnings("unchecked")
		Iterator<String> iter = c.keys();
		while(iter.hasNext()){
			String currentKey = iter.next();
			map.put(currentKey, c.getString(currentKey));
		}
		
		book.setBook_id(map.get("book_id"));
		book.setBook_name(map.get("book_name"));
		book.setAuthor_name(map.get("author_name"));
		book.setPrice(Double.parseDouble(map.get("price")));
		book.setRating(Float.parseFloat(map.get("rating")));
		book.setRating_count(Integer.parseInt(map.get("rating_count")));
		book.setBookmarked(Boolean.parseBoolean(map.get("is_marked")));
		book.setNew(Boolean.parseBoolean(map.get("is_new")));
		book.setDescription(map.get("description"));
		book.setPublished_year(map.get("published_year"));
	}

	public void setBookArrayItemValues(ArrayList<BookVO> bookVoArray, ArrayList<HashMap<String,String>> bookArray) throws JSONException {

		for(int i = 0; i < bookArray.size(); i++){
			HashMap<String, String> map = bookArray.get(i);
			book = bookVoArray.get(i);
			
			book.setBook_id(map.get("book_id"));
			book.setBook_name(map.get("book_name"));
			
			if(null != map.get("category_name")){
				book.setCategory_name(map.get("category_name"));
				book.setCategory_id(map.get("category_id"));
			}
			if(null != map.get("subText"))
				book.setSubText(map.get("subText"));
			if(null != map.get("author_name"))
				book.setAuthor_name(map.get("author_name"));
			if(null != map.get("price"))
				book.setPrice(Double.parseDouble(map.get("price")));
			if(null != map.get("is_new"))
				book.setNew(Boolean.parseBoolean(map.get("is_new")));// API deer utgiig nemj oruulah
		}
		
	}
}
