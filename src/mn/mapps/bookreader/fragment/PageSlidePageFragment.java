package mn.mapps.bookreader.fragment;

import java.net.URLEncoder;

import mn.mapps.bookreader.R;
import mn.mapps.bookreader.R.id;
import mn.mapps.bookreader.R.layout;
import mn.mapps.bookreader.util.Util;
import mn.mapps.bookreader.webview.BookReaderWebView;
import mn.mapps.bookreader.webview.BookViewWebClient;
import mn.mapps.bookreader.webview.WebClientJsInterface;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Fragment;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnDrawListener;
import android.view.ViewTreeObserver.OnPreDrawListener;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.RelativeLayout;
import android.widget.Toast;

public class PageSlidePageFragment extends Fragment {
	
	private static final String TAG = PageSlidePageFragment.class.getSimpleName();
	private Util util = new Util();
	public static final String ARG_PAGE = "page";
	private int pageNumber;
	private BookReaderWebView webview;
	private static String pageContent;
	private static String bookFolderName;

	public PageSlidePageFragment() {
	}
	
	public static PageSlidePageFragment create(int pageNumber, String pageContent, String bookFolderName){
		PageSlidePageFragment.pageContent = pageContent;
		PageSlidePageFragment.bookFolderName = bookFolderName;
		PageSlidePageFragment fragment = new PageSlidePageFragment();
		Bundle args = new Bundle();
		args.putInt(ARG_PAGE, pageNumber);
		fragment.setArguments(args);
		return fragment;
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		pageNumber = getArguments().getInt(ARG_PAGE);
	}
	
	@SuppressLint("SetJavaScriptEnabled")
	@TargetApi(Build.VERSION_CODES.KITKAT)
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

	    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
	    	WebView.setWebContentsDebuggingEnabled(true);
		}
	    
		final ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.bookdisplay, container, false);
		webview = (BookReaderWebView) rootView.findViewById(R.id.wv_screen);
	    webview.getSettings().setJavaScriptEnabled(true);
	    webview.addJavascriptInterface(new WebClientJsInterface(getActivity()), "Android");
	    webview.setWebViewClient(new BookViewWebClient());
	    
	    String epubBasePath = Environment.getExternalStorageDirectory() + "/" + util.getRootDirName() +"/"+ bookFolderName  +"/";
	    
	    ViewTreeObserver viewTreeObserver  = webview.getViewTreeObserver();
	    viewTreeObserver.addOnPreDrawListener(new OnPreDrawListener() {
	                       @Override
	                       public boolean onPreDraw() {                                
	                               int width = webview.getMeasuredWidth();
	                               
	                               if( width != 0 ){
	                                       Toast.makeText(getActivity(), "width:"+width,Toast.LENGTH_SHORT).show();
	                                       webview.getViewTreeObserver().removeOnPreDrawListener(this);
	                               }
	                               return false;
	                       }
	                       
	               });

//	    webview.loadDataWithBaseURL("file://"+ epubBasePath, sampleText, "text/html", "utf-8", null);
	    webview.loadDataWithBaseURL("file://"+ epubBasePath, pageContent, "text/html", "utf-8", null);
	    return rootView;
	}
	 
	public int getPageNumber() {
		return pageNumber;
	}
	
}
