package mn.mapps.bookreader.webview;

import java.util.Map;

import nl.siegmann.epublib.domain.Book;
import mn.mapps.bookreader.BookReaderActivity;
import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.webkit.WebView;

public class BookReaderWebView extends WebView{

	private static final String TAG = BookReaderWebView.class.getSimpleName();;

	public BookReaderWebView(Context context) {
		super(context);
	}
	
	public BookReaderWebView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	
	public BookReaderWebView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}
	
	public BookReaderWebView(Context context, AttributeSet attrs, int defStyle, boolean privateBrowsing) {
		super(context, attrs, defStyle, privateBrowsing);
	}

	@Override
	public void loadData(String data, String mimeType, String encoding) {
		super.loadData(data, mimeType, encoding);
	}
	
	@Override
	public void loadDataWithBaseURL(String baseUrl, String data, String mimeType, String encoding, String historyUrl) {
		super.loadDataWithBaseURL(baseUrl, data, mimeType, encoding, historyUrl);
	}
	
	@Override
	public void loadUrl(String url) {
		super.loadUrl(url);
	}
	
	@Override
	public void loadUrl(String url, Map<String, String> additionalHttpHeaders) {
		super.loadUrl(url, additionalHttpHeaders);
	}


}
