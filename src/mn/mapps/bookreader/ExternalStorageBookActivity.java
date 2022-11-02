package mn.mapps.bookreader;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import mn.mapps.bookreader.InternalStorageBookActivity.RequestExecutionTask;
import mn.mapps.bookreader.connection.AsyncCallBack;
import mn.mapps.bookreader.controller.ActionController;
import mn.mapps.bookreader.util.Util;
import mn.mapps.bookreader.vo.BookVO;
import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class ExternalStorageBookActivity extends Activity {
	
	private static final String TAG = ExternalStorageBookActivity.class.getSimpleName();
	private Util util = null;
	private ActionController actionController = null;
	private ViewGroup mContainerView;
	private static Map<String, Integer> selectedFiles = null;
	private ArrayList<BookVO> storageFiles;
	private ArrayList<BookVO> importBooks;
	private ViewGroup newView;
	private static final int REFRESH_LIST = 100;
	
	final Handler handler = new Handler() {

		public void handleMessage(Message msg) {

			if (msg.what == REFRESH_LIST){
				refreshList();
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_layout_changes);
		
		util = new Util();
		
		if(util.isExternalStorageReadable()){
		
			actionController = new ActionController(getApplicationContext());
			util = new Util();
			selectedFiles = new HashMap<String, Integer>();
			importBooks = new ArrayList<BookVO>();
			
			getActionBar().setDisplayShowTitleEnabled(false);
			getActionBar().setDisplayHomeAsUpEnabled(true);
			mContainerView = (ViewGroup) findViewById(R.id.container);

			String filepath = System.getenv("SECONDARY_STORAGE"); // SDCARD LOCATION
			File directory = new File(filepath);
			
			storageFiles = actionController.getStorageFile(".epub", directory);
			viewList(storageFiles);
		
		}else{
			Log.i(TAG, "[@ onCreate] SDcard is not readable!");
			finish();
		}
	}
	

	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.activity_internal_storage, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                NavUtils.navigateUpTo(this, new Intent(this, MainActivity.class));
                return true;

            case R.id.action_add_file:
            	
            	if(selectedFiles.size() < 1)
            		Toast.makeText(getApplicationContext(), "Номын санд оруулах номоо сонгоно уу!", Toast.LENGTH_SHORT).show();
            	else
            		new RequestExecutionTask().execute();
            	return true;
        }

        return super.onOptionsItemSelected(item);
    }
    
    public class RequestExecutionTask extends AsyncTask<Void, Void, Void> {
    	
    	@Override
    	protected void onPreExecute() {
    		util.showProgressDialog(getApplicationContext());
    		super.onPreExecute();
    	}
    	
		@Override
		protected Void doInBackground(Void... params) {
			addFileToLibrary(selectedFiles);
			return null;
		}
		
		@Override
		protected void onPostExecute(Void result) {
			util.hideProgressDialog();
			super.onPostExecute(result);
		}
	}
	
	private void addFileToLibrary(final Map<String, Integer> selectedFiles) {
		for (String key: selectedFiles.keySet()) {
			int index = selectedFiles.get(key);
			System.out.println("key : " + key + ", value : " + index);

			BookVO book = storageFiles.get(index);
			importBooks.add(book);
		}
		
		actionController.importBook(importBooks, new AsyncCallBack<String>(){

			@Override
			public void onSuccess(String result) {
				if(!result.isEmpty()){
					handler.sendEmptyMessage(REFRESH_LIST);
				}
			}

			@Override
			public void onFailure(Throwable th) {
				Log.i(TAG, "Error on importing a book..");
				th.printStackTrace();
			}
		});
	}
	
	private void refreshList(){
		for (String key: selectedFiles.keySet()) {
			int index = selectedFiles.get(key);
			
			for(int i = 0; i < mContainerView.getChildCount(); i++){
				int tag = (Integer) mContainerView.getChildAt(i).getTag();
				if(index == tag)
					mContainerView.removeView(mContainerView.getChildAt(i));
			}
		}
		
		if(mContainerView.getChildCount() < 1)
			findViewById(android.R.id.empty).setVisibility(View.VISIBLE);
		
		Toast.makeText(getApplicationContext(), "Номын санд амжилттай нэмэгдлээ", Toast.LENGTH_SHORT).show();
	}

	public void viewList(ArrayList<BookVO> storageFiles){
		util.showProgressDialog(this);
		
		findViewById(android.R.id.empty).setVisibility(View.GONE);
		try{
			for(int i = 0; i < storageFiles.size(); i++){
				
				boolean isDuplicated = false;
				ArrayList<BookVO> libraryList = util.getLibraryList();
				for(int j = 0; j < libraryList.size(); j++){
					BookVO bookVO = libraryList.get(j);
					
					if(storageFiles.get(i).getBook_name().contains(bookVO.getBook_name())){
						isDuplicated = true;
					}
				}
				
				if(isDuplicated != true)
					addItem(storageFiles.get(i), i);
			}
			
		}catch(Exception ex){
			ex.printStackTrace();
		}
		
		util.hideProgressDialog();
		
		if(mContainerView.getChildCount() < 1)
			findViewById(android.R.id.empty).setVisibility(View.VISIBLE);
	}
	
	private void addItem(final BookVO bookVO, int tag) {
        newView = (ViewGroup) LayoutInflater.from(this).inflate(R.layout.fileitem, mContainerView, false);
        
        newView.setTag(tag);

        ImageView bookCover = (ImageView) newView.findViewById(R.id.iv_fileIcon);
        
        bookCover.setImageResource(R.drawable.epub_icon);
        
        String book_name = bookVO.getBook_name();
		if(22 <= book_name.length()) 
			book_name = book_name.substring(0, 20) + "..";
		((TextView) newView.findViewById(R.id.tv_bookName)).setText(book_name);

        ((TextView) newView.findViewById(R.id.tv_date)).setText(bookVO.getLastModifiedDate());
        ((TextView) newView.findViewById(R.id.tv_fileSize)).setText(bookVO.getFileSize());
        
        
        String rootPath = Environment.getExternalStorageDirectory().toString();
        String path = bookVO.getPath();
		path = path.replace(rootPath, "");
		TextView filePath = (TextView) newView.findViewById(R.id.tv_filePath);
		
		if(37 <= path.length()) 
			path = path.substring(0, 35) + "..";
		filePath.setText(path);
        
        newView.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				int tag = (Integer) v.getTag();
				CheckBox fileSelect = (CheckBox) v.findViewById(R.id.cb_fileSelect);

				if(!fileSelect.isChecked()){
					fileSelect.setChecked(true);

					selectedFiles.put(Integer.toString(tag), tag);
					Log.d(TAG, "Selected tag: " + tag);
				}else{
					fileSelect.setChecked(false);
					
					selectedFiles.remove(Integer.toString(tag));
					Log.d(TAG, "Removed tag: " + tag);
				}
			}
		});
        
        mContainerView.addView(newView, 0);
    }
	
}
