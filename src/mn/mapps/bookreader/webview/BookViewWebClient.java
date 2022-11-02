package mn.mapps.bookreader.webview;

import android.net.Uri;
import android.util.Log;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class BookViewWebClient extends WebViewClient {
	
	private static final String TAG = BookViewWebClient.class.getSimpleName();

	@Override
	public void onPageFinished(WebView view, String url) {
		super.onPageFinished(view, url);
		
		int contentHeight = view.getContentHeight();
		
		Log.d(TAG, "contentHeight:"+contentHeight);
	}
	
	@Override
	public boolean shouldOverrideUrlLoading(WebView view, String url) {
		Log.i(TAG, "shouldOverrideUrlLoading - Loaded url: " + url);
		Log.i(TAG, "Host length: " + Uri.parse(url).getHost().length());
//		if(Uri.parse(url).getHost().length() == 0) {
//            return false;
//        }
		
		return super.shouldOverrideUrlLoading(view, url);

//		html5rocks.com -s uur url baival shine tab-d neej uzuuleh jishee
//		if(Uri.parse(url).getHost().endsWith("html5rocks.com")) {
//             return false;
//        }
//        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
//        view.getContext().startActivity(intent);
//        return true;
	}
}
