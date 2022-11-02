package mn.mapps.bookreader;

import java.util.HashMap;
import java.util.Iterator;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import mn.mapps.bookreader.connection.AsyncCallBack;
import mn.mapps.bookreader.connection.ServerApiListContainer;
import mn.mapps.bookreader.controller.ActionController;
import mn.mapps.bookreader.util.Util;
import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class PurchasedBookActivity extends Activity {

	private static final String TAG = PurchasedBookActivity.class.getSimpleName();
	private Util util = new Util();
	private ActionController actionController = null;
	private ViewGroup mContainerView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_layout_changes);
		actionController = new ActionController(getApplicationContext());
		
		getActionBar().setDisplayShowTitleEnabled(false);
		getActionBar().setDisplayHomeAsUpEnabled(true);
		
		mContainerView = (ViewGroup) findViewById(R.id.container);
		
		try {
			util.showProgressDialog(this);
			
			String userEmail = util.getCacheData(getApplication(), "user_email", "");
			actionController.getPurchasedBooks(userEmail, 0, 10, new AsyncCallBack<String>(){

				@Override
				public void onSuccess(final String result) {
					Log.i(TAG, "getPurchasedBooks Succeeded: \n\n" + result.toString());
					
					runOnUiThread(new Runnable(){
						public void run(){
							viewList(result);
							util.hideProgressDialog();
	                    }
	                });
				}

				@Override
				public void onFailure(Throwable th) {
					Log.i(TAG, "Error on isDownloadFree..");
					util.hideProgressDialog();
					findViewById(android.R.id.empty).setVisibility(View.VISIBLE);
					th.printStackTrace();
				}
			});
			
		} catch (JSONException e) {
			util.hideProgressDialog();
			e.printStackTrace();
		}
	}
	
	public void viewList(String result){
		JSONArray bookListArray = null;
		try{
			bookListArray = new JSONArray(result);
			if(bookListArray.isNull(0))
				bookListArray = null;
		}catch(Exception ex){
			bookListArray = null;
		}
		
		if(bookListArray != null){
			findViewById(android.R.id.empty).setVisibility(View.GONE);
			try{
				
				for(int i = 0; i < bookListArray.length(); i++){
					
					HashMap<String, String> map = new HashMap<String, String>();
					JSONObject c = bookListArray.getJSONObject(i);
		
					Iterator<String> iter = c.keys();
					while(iter.hasNext()){
						String currentKey = iter.next();
						map.put(currentKey, c.getString(currentKey));
					}
	
					addItem(map);
				}
				
			}catch(Exception ex){
				ex.printStackTrace();
			}
		}else{
			findViewById(android.R.id.empty).setVisibility(View.VISIBLE);
		}
	}
	
	private void addItem(final HashMap<String,String> map) {
        final ViewGroup newView = (ViewGroup) LayoutInflater.from(this).inflate(R.layout.purchased_book_list_item, mContainerView, false);

        ImageView bookCover = (ImageView) newView.findViewById(R.id.iv_fileIcon);
        if(ServerApiListContainer.isDummyDataTest){ // API URL image-r zasah shaardlagatai
        	bookCover.setImageResource(R.drawable.cover6); // API URL image-r zasah shaardlagatai
		}else
			util.displayImageEbook(map.get("book_id"), bookCover, "300");
        
        String book_name = map.get("book_name");
		if(22 <= book_name.length()) 
			book_name = book_name.substring(0, 20) + "..";
		((TextView) newView.findViewById(R.id.tv_bookName)).setText(book_name);

		String author_name = map.get("author_name");
		if(32 <= author_name.length())
			author_name = author_name.substring(0, 30) + "..";
        ((TextView) newView.findViewById(R.id.tv_date)).setText("Зохиогч: "+author_name);

        newView.findViewById(R.id.iv_btnDownload).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            	
            	boolean[] networkState = util.getNetworkState(getApplicationContext());
            	
            	if(networkState[0] == false && networkState[1] == false){
            		Toast.makeText(getApplicationContext(), "Интернэтэд холбогдоогүй байна!", Toast.LENGTH_SHORT).show();
            	}else{
            		if(networkState[0] == false && networkState[1] == true){
            			Toast.makeText(getApplicationContext(), "3G интернэт холбогдсон байна!", Toast.LENGTH_SHORT).show();
            		}else{
            			
            			String userEmail = util.getCacheData(getApplication(), "user_email", "");
            			String token = util.getCacheData(getApplication(), "token", "");
            			actionController.downloadSample(userEmail, map.get("book_id"), token, util.getDgn(getApplicationContext()), map.get("book_name"));
            			
            			mContainerView.removeView(newView);
            			
            			if (mContainerView.getChildCount() == 0) {
            				findViewById(android.R.id.empty).setVisibility(View.VISIBLE);
            			}
            		}
            	}
            	
            }
        });

        mContainerView.addView(newView, 0);
    }

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    if (item.getItemId() == android.R.id.home) {
	        onBackPressed();
	    }
	    return super.onOptionsItemSelected(item);
	}

}
