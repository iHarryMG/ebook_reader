package mn.mapps.bookreader.connection;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

import mn.mapps.bookreader.BookDetailActivity;
import mn.mapps.bookreader.R;
import mn.mapps.bookreader.util.Util;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;

import android.app.NotificationManager;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.v4.app.NotificationCompat;
import android.transition.Visibility;
import android.util.Log;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

public class ServerRequestHandler {
	private static final String TAG = ServerRequestHandler.class.getSimpleName();
	private Util util = new Util();
	private Context context;
	public onFileDownloadListener mListener;
	private RequestExecutionTask requestExecutionTask;
	
	public ServerRequestHandler(Context context) {
		this.context = context;
	}
	
	@SuppressWarnings("unchecked")
	public void doGetRequest(Context context, String apiUrl, List<NameValuePair> params, AsyncCallBack<String> asyncCallBack){
		setRequestExecutionTask(new RequestExecutionTask(context, apiUrl, params, "GET"));
		getRequestExecutionTask().execute(asyncCallBack);
	}
	
	@SuppressWarnings("unchecked")
	public void doPostRequest(Context context, String apiUrl, List<NameValuePair> params, AsyncCallBack<String> asyncCallBack){
		setRequestExecutionTask(new RequestExecutionTask(context, apiUrl, params, "POST"));
		getRequestExecutionTask().execute(asyncCallBack);
	}
	
	@SuppressWarnings("unchecked")
	public void doPostStreamRequest(Context context, String book_name, String book_id, String apiUrl, List<NameValuePair> params, AsyncCallBack<String> asyncCallBack){
		setRequestExecutionTask(new RequestExecutionTask(context, book_name, book_id, apiUrl, params, "POST"));
		getRequestExecutionTask().execute(asyncCallBack);
	}
	
	public class RequestExecutionTask extends AsyncTask<AsyncCallBack<String>, Void, String> {
		private String book_name = null;
		private String book_id = null;
		private String apiUrl = null;
		private List<NameValuePair> params = null;
		private AsyncCallBack<String> callback;
		private String METHOD;
		
		public RequestExecutionTask(Context context, String apiUrl, List<NameValuePair> params, String METHOD) {
			this.apiUrl = apiUrl;
			this.params = params;
			this.METHOD = METHOD;
		}
		
		public RequestExecutionTask(Context context, String book_name, String book_id, String apiUrl, List<NameValuePair> params, String METHOD) {
			this.setBook_name(book_name);
			this.setBook_id(book_id);
			this.apiUrl = apiUrl;
			this.params = params;
			this.METHOD = METHOD;
		}

		@Override
		protected String doInBackground(AsyncCallBack<String>... param) {
			this.callback = param[0];
			
			String requestResult = "";
			try {

				Log.d(TAG, "[0] Trying to do \""+METHOD+"\" REQUEST data from: [ " + apiUrl + " ]\n\nPARAMS:" +params);
				
				if(ServerApiListContainer.isDummyDataTest){
					Log.d(TAG, "[0] Setting up DummyDataTest @RequestExecutionTask");
					
					if(ServerApiListContainer.getUrl("getMarkedList") == this.apiUrl)
						requestResult = util.getJsonBookListForTest();
					else if(ServerApiListContainer.getUrl("getNewBooks") == this.apiUrl)
						requestResult = util.getJsonNewBookList();
					else if(ServerApiListContainer.getUrl("getMoreList") == this.apiUrl)
						requestResult = util.getJsonBookListForTest();
					else if(ServerApiListContainer.getUrl("getEbookDetail") == this.apiUrl)
						requestResult = util.getJsonBookDetail();
					else if(ServerApiListContainer.getUrl("getPurchasedBooks") == this.apiUrl)
						requestResult = util.getJsonPurchasedBookList();
					else if(ServerApiListContainer.getUrl("search") == this.apiUrl)
						requestResult = util.getJsonBookListForTest();
					
					Log.d(TAG, "[2] DummyDataTest set successfully!");
					
				}else{
		    		if(ServerApiListContainer.getUrl("download") == apiUrl){
		    			requestResult = executeStreamRequest(getBook_name(), getBook_id(), apiUrl, params, METHOD);
		    		}else{
		    			requestResult = executeStringRequest(apiUrl, params, METHOD);
		    		}
				}
				
			} catch (Exception e) {
				e.printStackTrace();
				param[0].onFailure(new Throwable("String Request Execution Exception!"));
			}
			
			return requestResult;
		}
		
		@Override
		protected void onPostExecute(String requestResult) {
			if(ServerApiListContainer.isDummyDataTest){
				callback.onSuccess(requestResult);
			}else{
				if(ServerApiListContainer.getUrl("download") == apiUrl){
					int resultCode = Integer.parseInt(requestResult);
					switch(resultCode){
	    				case 2000:
	    					callback.onSuccess(requestResult);
	    					break;
	    				case 407:
	    					callback.onFailure(new Throwable("Book Download Stream Exception!"));
	    					break;
	    			}
				}else{
					callback.onSuccess(requestResult);
				}
			}
		}

		public String getBook_name() {
			return book_name;
		}

		public void setBook_name(String book_name) {
			this.book_name = book_name;
		}

		public String getBook_id() {
			return book_id;
		}

		public void setBook_id(String book_id) {
			this.book_id = book_id;
		}
		
    }
	
	@SuppressWarnings("deprecation")
	private String executeStringRequest(String url, List<NameValuePair> nameValuePairs, String METHOD) throws Exception{
		
		Log.d(TAG, "[0] Setting up HttpConnection @executeStringRequest");
		String requestResult = null;
    	DefaultHttpClient httpclient = new DefaultHttpClient();
    	HttpResponse httpresponse = null;
    	
    	if("POST".equals(METHOD)){
    		HttpPost httpPostReq = new HttpPost(url);
    		
    		if(nameValuePairs != null){
    			httpPostReq.setEntity(new UrlEncodedFormEntity(nameValuePairs));
    		}
    		httpPostReq.setHeader("Accept", "application/json");
    		httpPostReq.setHeader("Content-Encoding", "UTF-8");
    		
    		Log.d(TAG, "[1] Executing HttpConnection ");
    		httpresponse = httpclient.execute(httpPostReq);
    		Log.d(TAG, "[2] HttpConnection executed successfully!");
    	}else if("GET".equals(METHOD)){
    		HttpGet httpGetReq = new HttpGet(url);
    		
    		if(nameValuePairs != null){
    			
    			for(int i = 0; i < nameValuePairs.size(); i++){
    				NameValuePair nameValuePair = nameValuePairs.get(i);
    				httpGetReq.getParams().setParameter(nameValuePair.getName(), nameValuePair.getValue());
    			}
    		}
    		httpGetReq.setHeader("Accept", "application/json");
    		httpGetReq.setHeader("Content-Encoding", "UTF-8");
    		
    		Log.d(TAG, "[1] Executing HttpConnection ");
    		httpresponse = httpclient.execute(httpGetReq);
    		Log.d(TAG, "[2] HttpConnection executed successfully!");
    	}else{
    		return "407";
    	}
    	
    	
    	HttpEntity resultentity = httpresponse.getEntity();
    	if(resultentity != null) {
    		Log.d(TAG, "[3] HttpEntity is NOT empty: ContentLength=" + resultentity.getContentLength());
    		InputStream inputstream = resultentity.getContent();
    		requestResult = convertStreamToString(inputstream);
    		Log.d(TAG, "[4] EntityStream converted to STRING successfully!");
    		inputstream.close();
    		Log.i(TAG, requestResult);
    	}
		
		return requestResult;
	}
	
    private String convertStreamToString(InputStream is) {
        String line = "";
        StringBuilder total = new StringBuilder();
        BufferedReader rd = new BufferedReader(new InputStreamReader(is));
        try {
            while ((line = rd.readLine()) != null) {
                total.append(line);
            }
        } catch (Exception e) {
            Log.e(TAG, "Stream Exception");
            e.printStackTrace();
        }
        return total.toString();
    }
    
    @SuppressWarnings("deprecation")
	private String executeStreamRequest(String book_name, String book_id, String url, List<NameValuePair> nameValuePairs, String METHOD) throws Exception{
    	Log.d(TAG, "[0] Setting up HttpConnection @executeStringRequest");
    	
		String requestResult = null;
    	DefaultHttpClient httpclient = new DefaultHttpClient();
    	HttpResponse httpresponse = null;
    	
    	if("POST".equals(METHOD)){
    		HttpPost httpPostReq = new HttpPost(url);
    		
    		if(nameValuePairs != null){
    			httpPostReq.setEntity(new UrlEncodedFormEntity(nameValuePairs));
    		}
    		httpPostReq.setHeader("Accept", "application/json");
    		httpPostReq.setHeader("Content-Encoding", "UTF-8");
    		
    		Log.d(TAG, "[1] Executing HttpConnection ");
    		httpresponse = httpclient.execute(httpPostReq);
    		Log.d(TAG, "[2] HttpConnection executed successfully!");
    	}else if("GET".equals(METHOD)){
    		HttpGet httpGetReq = new HttpGet(url);
    		
    		if(nameValuePairs != null){
    			
    			for(int i = 0; i < nameValuePairs.size(); i++){
    				NameValuePair nameValuePair = nameValuePairs.get(i);
    				httpGetReq.getParams().setParameter(nameValuePair.getName(), nameValuePair.getValue());
    			}
    		}
    		httpGetReq.setHeader("Accept", "application/json");
    		httpGetReq.setHeader("Content-Encoding", "UTF-8");
    		
    		Log.d(TAG, "[1] Executing HttpConnection ");
    		httpresponse = httpclient.execute(httpGetReq);
    		Log.d(TAG, "[2] HttpConnection executed successfully!");
    	}else{
    		return "407";
    	}
    	
    	HttpEntity resultentity = httpresponse.getEntity();
    	if(resultentity != null) {
    		Log.d(TAG, "[3] HttpEntity is NOT empty: ContentLength=" + resultentity.getContentLength());
    		InputStream inputstream = resultentity.getContent();
    		
    		requestResult = convertStreamToFile(inputstream, resultentity.getContentLength(), book_name, book_id);
    		Log.d(TAG, "[4] EntityStream converted to FILE successfully!");
    	
    		inputstream.close();
    	}else{
    		requestResult = "407";
    	}
		
		return requestResult;
	}
    

	public interface onFileDownloadListener{
		void setOnFileDownloadListener(long fileSizeInMb, long totalSizeInMb, long downloadProgressInPercentage);
	}

    private String convertStreamToFile(InputStream inputStream, long fileSize, String book_name, String book_id) {
    	FileOutputStream outputStream;
    	
        try {
        	mListener = (onFileDownloadListener) context;

        	int BUFFER_SIZE = 4096;
        	String epubFolderName = book_name +"#ID#"+book_id;
	        String saveFilePath =Environment.getExternalStorageDirectory()
	        						+"/" +util.getRootDirName() 
	        						+"/" +epubFolderName
	        						+"/" +util.getEpub_FileDirName() 
	        						+"/" +book_name + ".epub";
	        
			outputStream = new FileOutputStream(saveFilePath);
			
			long totalDownloadedSize = 0;
			int bytesRead = -1;
			byte[] buffer = new byte[BUFFER_SIZE];
			while ((bytesRead = inputStream.read(buffer)) != -1) {
				
				if(getRequestExecutionTask().isCancelled()){
					inputStream.close();
					outputStream.close();
					Log.e(TAG, "Cancelling Download Process");
					break;
				}
				
				outputStream.write(buffer, 0, bytesRead);
				totalDownloadedSize = totalDownloadedSize + bytesRead;
				long downloadProgressInPercentage = (long)((float)totalDownloadedSize/fileSize * 100f);
				mListener.setOnFileDownloadListener(totalDownloadedSize, fileSize, downloadProgressInPercentage);
			}
			
			inputStream.close();
			outputStream.close();
			
			return "2000";
        } catch (Exception e) {
            Log.e(TAG, "Stream Exception");
            e.printStackTrace();
            return "407";
        }
    }

	public RequestExecutionTask getRequestExecutionTask() {
		return requestExecutionTask;
	}

	public void setRequestExecutionTask(RequestExecutionTask requestExecutionTask) {
		this.requestExecutionTask = requestExecutionTask;
	}
    
}
