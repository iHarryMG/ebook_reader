package mn.mapps.bookreader.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import mn.mapps.bookreader.BookDetailActivity;
import mn.mapps.bookreader.connection.AsyncCallBack;
import mn.mapps.bookreader.connection.ServerApiListContainer;
import mn.mapps.bookreader.connection.ServerRequestHandler;
import mn.mapps.bookreader.connection.ServerRequestHandler.RequestExecutionTask;
import mn.mapps.bookreader.util.Util;
import mn.mapps.bookreader.vo.BookVO;
import nl.siegmann.epublib.domain.Book;
import nl.siegmann.epublib.epub.EpubReader;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

public class ActionController {
	
	private String TAG = ActionController.class.getSimpleName();
	
	private ServerRequestHandler requestHandler;
	private Util util = new Util();
	private Context context;
	private String email;
	private String book_id;
	private String book_name;
	private String token;
	private String dgn;
	private static final Integer downloadFreeSuccess = 200; 

	public ActionController(Context context) {
		this.context = context;
		requestHandler = new ServerRequestHandler(context);
	}
	
	final Handler handler = new Handler() {
		public void handleMessage(Message msg) {

			if (msg.what == downloadFreeSuccess){
				downloadBook();
			}
		}
	};

	public void getPurchasedBooks(String email, int start, int end, AsyncCallBack<String> asyncCallBack) throws JSONException {
		List<NameValuePair> params = new ArrayList<NameValuePair>(2);  
		params.add(new BasicNameValuePair("email", email));  // value must be dynamic
		params.add(new BasicNameValuePair("start", Integer.toString(start)));    // value must be dynamic
		params.add(new BasicNameValuePair("end", Integer.toString(end)));  // value must be dynamic
		
		String apiUrl = ServerApiListContainer.getUrl("getPurchasedBooks");
		requestHandler.doPostRequest(context, apiUrl, params, asyncCallBack);
	}
	
	public void downloadSample(String email, final String book_id, String token, String dgn, String book_name) {
		this.email = email;
		this.book_id = book_id;
		this.token = token;
		this.dgn = dgn;
		this.book_name = book_name;
		
		List<NameValuePair> params = new ArrayList<NameValuePair>(2);  
		params.add(new BasicNameValuePair("email", email));  
		params.add(new BasicNameValuePair("ebook-id", book_id));  
		params.add(new BasicNameValuePair("token", token));
		params.add(new BasicNameValuePair("dgn", dgn));
		
		String apiUrl = ServerApiListContainer.getUrl("isDownloadFree");
		requestHandler.doPostRequest(context, apiUrl, params, new AsyncCallBack<String>(){

			@Override
			public void onSuccess(String result) {
				int respond = Integer.parseInt(result);
				switch(respond){
					case 201:
						Log.e(TAG, "Payment has to be done!");
						break;
					case 200:
						Log.i(TAG, "isDownloadFree Succeeded: " + result);
						handler.sendEmptyMessage(respond);
						break;
					case 199:
						Log.e(TAG, "Couldn't find sample book id: " + book_id +" from Database");
						Toast.makeText(context, "Уг номонд бүртгэлтэй амтлагаа байхгүй байна.", Toast.LENGTH_SHORT).show();
						break;
					case 198:	
						Log.e(TAG, "Device limit exceeded!");
						Toast.makeText(context, "2 хүртэлх төхөөрөмж дээр татагдсан байна.", Toast.LENGTH_SHORT).show();
						break;
				}
			}

			@Override
			public void onFailure(Throwable th) {
				Log.i(TAG, "Error on isDownloadFree..");
				th.printStackTrace();
			}
		});
	}
	
	public void downloadBook(){
		
		List<NameValuePair> params = new ArrayList<NameValuePair>(2);  
		params.add(new BasicNameValuePair("email", email));  
		params.add(new BasicNameValuePair("ebook-id", book_id));  
		params.add(new BasicNameValuePair("token", token));
		params.add(new BasicNameValuePair("dgn", dgn));
		params.add(new BasicNameValuePair("carrier", util.getCarrier(context)));
		params.add(new BasicNameValuePair("ip", util.getIp(context)));
		params.add(new BasicNameValuePair("manufacturer", util.getPhoneManufacturer()));
		params.add(new BasicNameValuePair("model", util.getPhoneModel()));
		params.add(new BasicNameValuePair("sdk", util.getPhoneSdk()));
		
		util.createBookDirectories(this.book_name, this.book_id);
		String apiUrl = ServerApiListContainer.getUrl("download");
		requestHandler.doPostStreamRequest(context, book_name, book_id, apiUrl, params, new AsyncCallBack<String>(){

			@Override
			public void onSuccess(String result) {
				if(BookDetailActivity.BOOK_DOWNLOAD_COMPLETED == Integer.parseInt(result)){
					BookDetailActivity.handler.sendEmptyMessage(BookDetailActivity.BOOK_DOWNLOAD_COMPLETED);
					
					util.parseBook(book_name);
				}else{
					Log.i(TAG, "Error on downloading a book..");
				}
			}

			@Override
			public void onFailure(Throwable th) {
				Log.i(TAG, "Error on downloading a book..");
				th.printStackTrace();
			}
		});
	}

	public void getMarkedList(String userEmail, Integer start, Integer end, AsyncCallBack<String> asyncCallBack) {
		List<NameValuePair> params = new ArrayList<NameValuePair>(2);  
		params.add(new BasicNameValuePair("email", userEmail));
		params.add(new BasicNameValuePair("start", Integer.toString(start)));
		params.add(new BasicNameValuePair("end", Integer.toString(end)));
		
		String apiUrl = ServerApiListContainer.getUrl("getMarkedList");
		requestHandler.doPostRequest(context, apiUrl, params, asyncCallBack);
	}

	public void getEbookDetail(String userEmail, int book_id, AsyncCallBack<String> asyncCallBack) {
		List<NameValuePair> params = new ArrayList<NameValuePair>(2);  
		params.add(new BasicNameValuePair("email", userEmail));
		params.add(new BasicNameValuePair("ebook-id", Integer.toString(book_id)));
		
		String apiUrl = ServerApiListContainer.getUrl("getEbookDetail");
		requestHandler.doPostRequest(context, apiUrl, params, asyncCallBack);
	}

	public void getNewBooks(String userEmail, int start, int end, AsyncCallBack<String> asyncCallBack) {
		List<NameValuePair> params = new ArrayList<NameValuePair>(2);  
		params.add(new BasicNameValuePair("email", userEmail));
		params.add(new BasicNameValuePair("start", Integer.toString(start)));
		params.add(new BasicNameValuePair("end", Integer.toString(end)));
		
		String apiUrl = ServerApiListContainer.getUrl("getNewBooks");
		requestHandler.doPostRequest(context, apiUrl, params, asyncCallBack);
	}

	public void getMoreList(String userEmail, int start, int end, int category_id, AsyncCallBack<String> asyncCallBack) {
		List<NameValuePair> params = new ArrayList<NameValuePair>(2);  
		params.add(new BasicNameValuePair("email", userEmail));
		params.add(new BasicNameValuePair("start", Integer.toString(start)));
		params.add(new BasicNameValuePair("end", Integer.toString(end)));
		params.add(new BasicNameValuePair("list-id", Integer.toString(category_id)));
		
		String apiUrl = ServerApiListContainer.getUrl("getMoreList");
		requestHandler.doPostRequest(context, apiUrl, params, asyncCallBack);
	}

	public void stopDownload() {
		RequestExecutionTask requestExecutionTask = requestHandler.getRequestExecutionTask();
		String book_name = requestExecutionTask.getBook_name();
		String book_id = requestExecutionTask.getBook_id();
		requestExecutionTask.cancel(true);
		
		util.removeDirectory(book_name + "#ID#" + book_id);
	}

	public void search(String userEmail, int start, int end, String query, AsyncCallBack<String> asyncCallBack) {
		List<NameValuePair> params = new ArrayList<NameValuePair>(2);  
		params.add(new BasicNameValuePair("email", userEmail));
		params.add(new BasicNameValuePair("start", Integer.toString(start)));
		params.add(new BasicNameValuePair("end", Integer.toString(end)));
		params.add(new BasicNameValuePair("keyword", query));
		
		String apiUrl = ServerApiListContainer.getUrl("search");
		requestHandler.doPostRequest(context, apiUrl, params, asyncCallBack);
	}

	public void rateEbook(String userEmail, String book_id, int rating, String comment, String manufacturer, String model, AsyncCallBack<String> asyncCallBack) {
		List<NameValuePair> params = new ArrayList<NameValuePair>(2);  
		params.add(new BasicNameValuePair("email", userEmail));
		params.add(new BasicNameValuePair("ebook-id", book_id));
		params.add(new BasicNameValuePair("rate", Integer.toString(rating)));
		params.add(new BasicNameValuePair("comment", comment));
		params.add(new BasicNameValuePair("manufacturer", manufacturer));
		params.add(new BasicNameValuePair("model", model));
		
		String apiUrl = ServerApiListContainer.getUrl("rateEbook");
		requestHandler.doPostRequest(context, apiUrl, params, asyncCallBack);
	}

	public void getUserInfo(String userEmail, AsyncCallBack<String> asyncCallBack) {
		List<NameValuePair> params = new ArrayList<NameValuePair>(2);  
		params.add(new BasicNameValuePair("email", userEmail));
		
		String apiUrl = ServerApiListContainer.getUrl("getUserInfo");
		requestHandler.doPostRequest(context, apiUrl, params, asyncCallBack);
	}

	public void getVersion(AsyncCallBack<String> asyncCallBack) {
		String apiUrl = ServerApiListContainer.getUrl("getVersion");
		requestHandler.doGetRequest(context, apiUrl, null, asyncCallBack);
	}

	public ArrayList<BookVO> getStorageFile(String pattern, File directory) {
		try {
			ArrayList<BookVO> fileList = new ArrayList<BookVO>();
			util.crawlDirectories(directory, pattern, fileList);
			return fileList;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}

	public void importBook(ArrayList<BookVO> importBooks, AsyncCallBack<String> asyncCallBack) {
		
		try{
			for(int i = 0; i < importBooks.size(); i++){
				BookVO book = importBooks.get(i);
				
				util.createBookDirectories(book.getBook_name(), "-1");
				util.copyFile(book.getPath(), book.getBook_name() + ".epub", Environment.getExternalStorageDirectory() + "/"+ util.getRootDirName() +"/"+ util.getRootEpubDirName() + "/" +util.getEpub_FileDirName() + "/");
				util.parseBook(book.getBook_name());
			}
			
			asyncCallBack.onSuccess(Integer.toString(importBooks.size()));
		}catch(Exception ex){
			asyncCallBack.onFailure(ex);
		}
		
		
		
	}
	
	
	
}