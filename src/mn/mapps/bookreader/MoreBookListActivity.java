package mn.mapps.bookreader;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import mn.mapps.bookreader.adapter.ArrayAdapterForBookmarkedList;
import mn.mapps.bookreader.controller.ActionController;
import mn.mapps.bookreader.service.BookService;
import mn.mapps.bookreader.vo.BookVO;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

public class MoreBookListActivity extends Activity {

	private static final String TAG = MoreBookListActivity.class.getSimpleName();
	private Context context;
	private ActionController actionController;
	private String moreBookListResult;
	private String activityTitle;
	private BookService bookService;
	private BookVO book;
	private JSONObject bookObject;
	private ArrayAdapterForBookmarkedList adapterItem;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.parent_marked_list);
		context = this;
		actionController = new ActionController(this);
		
		getActionBar().setDisplayShowTitleEnabled(false);
		getActionBar().setDisplayHomeAsUpEnabled(true);

		Bundle extras = getIntent().getExtras();
		if (extras != null) {
		    moreBookListResult = extras.getString("More_Book_List_Result");
		    
		    try{
		    	activityTitle = extras.getString("Category_Name");
		    }catch(Exception ex){
		    	
		    }
		    
		    try{
		    	activityTitle = "Нийт үр дүн: " + extras.getString("Search_Result_Items");
		    }catch(Exception ex){
		    	
		    }
		    
		    if(moreBookListResult != null && !moreBookListResult.isEmpty()){
		    	Log.i(TAG, "category_name: " + activityTitle);
		    	viewList(moreBookListResult);
		    }
		}else{
			Log.e(TAG, "More Book List Result not found!");
			Toast.makeText(this, "Номны жагсаалт харуулахад алдаа гарлаа", Toast.LENGTH_SHORT).show();
			onBackPressed();
		}
	}
	
	public void viewList(String result){
		ArrayList<BookVO> bookList = new ArrayList<BookVO>();
		adapterItem = new ArrayAdapterForBookmarkedList(this, R.layout.bookitem, bookList);

		if(result != null){
			findViewById(R.id.tv_markedList_empty).setVisibility(View.INVISIBLE);
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
			findViewById(R.id.tv_markedList_empty).setVisibility(View.VISIBLE);
		}
		
		
		ListView listView = (ListView)findViewById(R.id.lv_booklist);
		listView.setAdapter(adapterItem);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    if (item.getItemId() == android.R.id.home) {
	        onBackPressed();
	    }
	    return super.onOptionsItemSelected(item);
	}
}
