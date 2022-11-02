package mn.mapps.bookreader.listener;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.util.List;

import net.sf.andpdf.pdfviewer.PdfViewerActivity;
import mn.mapps.bookreader.BookDetailActivity;
import mn.mapps.bookreader.MoreBookListActivity;
import mn.mapps.bookreader.BookReaderActivity;
import mn.mapps.bookreader.PageSlideActivity;
import mn.mapps.bookreader.PdfViewer;
import mn.mapps.bookreader.TabbedActivity;
import mn.mapps.bookreader.connection.AsyncCallBack;
import mn.mapps.bookreader.connection.ServerApiListContainer;
import mn.mapps.bookreader.connection.ServerRequestHandler;
import mn.mapps.bookreader.controller.ActionController;
import mn.mapps.bookreader.fragment.PageSlidePageFragment;
import mn.mapps.bookreader.util.Util;
import android.app.Activity;
import android.app.SearchableInfo;
import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Toast;
import nl.siegmann.epublib.domain.Book;
import nl.siegmann.epublib.domain.Guide;
import nl.siegmann.epublib.domain.Resource;
import nl.siegmann.epublib.domain.Spine;
import nl.siegmann.epublib.domain.TOCReference;
import nl.siegmann.epublib.epub.EpubReader;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;


public class OnBookItemClickListener implements OnClickListener {

	private static final String TAG = OnBookItemClickListener.class.getSimpleName(); 
	
	private ActionController actionController;
	private Util util = new Util();
	private Context context;
	private int tabSection;
	private String book_id;
	private String book_name;
	private String category_id;
	private String category_name;


	public OnBookItemClickListener(Context context, String book_id, int tabSection) {
		this.context = context;
		this.setBook_name(book_name);
		this.setTabSection(tabSection);
		this.setBook_id(book_id);
		actionController = new ActionController(context);
	}
	
	public OnBookItemClickListener(Context context, int tabSection, String category_id, String categoryName) {
		this.context = context;
		this.setCategoryName(categoryName);
		this.setCategory_id(category_id);
		this.setBook_name(book_name);
		this.setTabSection(tabSection);
		actionController = new ActionController(context);
	}
	
	public OnBookItemClickListener(Context context, String book_id, String book_name, int tabSection) {
		this.context = context;
		this.setBook_name(book_name);
		this.setTabSection(tabSection);
		this.setBook_id(book_id);
		actionController = new ActionController(context);
	}

	@Override
	public void onClick(View v) {
		Log.i(TAG, "Book ID: " + getBook_id());
		
		if(tabSection == TabbedActivity.MyLibrarySection){
			Intent intent = new Intent(context, PageSlideActivity.class);
		    intent.putExtra("Book_ID", getBook_id());
		    intent.putExtra("Book_NAME", getBook_name());
		    context.startActivity(intent);
		    
		}else if(tabSection == TabbedActivity.NewBookMoreList){
			if(getCategory_id() != null && !getCategory_id().isEmpty()){
				
				util.showProgressDialog(context);
				
				String userEmail = util.getCacheData(context, "user_email", "");
				actionController.getMoreList(userEmail, 0, 10, Integer.parseInt(getCategory_id()), new AsyncCallBack<String>(){
	
					@Override
					public void onSuccess(final String result) {
						Log.i(TAG, "getMoreList Succeeded: \n\n" + result.toString());
						util.hideProgressDialog();
						if(result != null){
							Intent intent = new Intent(context, MoreBookListActivity.class);
							intent.putExtra("More_Book_List_Result", result);
							intent.putExtra("Category_Name", getCategory_name());
							context.startActivity(intent);
						}
					}
	
					@Override
					public void onFailure(Throwable th) {
						Log.i(TAG, "Error on getMoreList..");
						util.hideProgressDialog();
						th.printStackTrace();
					}
				});
			}else{
				Log.i(TAG, "Category id is empty");
			}
			
		}else{
			
			if(getBook_id() != null && !getBook_id().isEmpty()){
				util.showProgressDialog(context);
				
				String userEmail = util.getCacheData(context, "user_email", "");
				actionController.getEbookDetail(userEmail, Integer.parseInt(getBook_id()), new AsyncCallBack<String>(){
	
					@Override
					public void onSuccess(final String result) {
						Log.i(TAG, "getEbookDetail Succeeded: \n\n" + result.toString());
						util.hideProgressDialog();
						
						if(result != null){
							Intent intent = new Intent(context, BookDetailActivity.class);
							intent.putExtra("Ebook_Detail_Result", result);
							context.startActivity(intent);
						}
					}
	
					@Override
					public void onFailure(Throwable th) {
						Log.i(TAG, "Error on isDownloadFree..");
						util.hideProgressDialog();
						th.printStackTrace();
					}
				});
			}else{
				Log.i(TAG, "Book id is empty");
			}
		}
	}

	public String getBook_id() {
		return book_id;
	}

	public void setBook_id(String book_id) {
		this.book_id = book_id;
	}

	public int getTabSection() {
		return tabSection;
	}

	public void setTabSection(int tabSection) {
		this.tabSection = tabSection;
	}

	public String getBook_name() {
		return book_name;
	}

	public void setBook_name(String book_name) {
		this.book_name = book_name;
	}

	public String getCategory_id() {
		return category_id;
	}

	public void setCategory_id(String category_id) {
		this.category_id = category_id;
	}

	public String getCategory_name() {
		return category_name;
	}

	public void setCategoryName(String category_name) {
		this.category_name = category_name;
	}

}
