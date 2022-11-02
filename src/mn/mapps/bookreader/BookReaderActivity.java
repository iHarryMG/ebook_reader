package mn.mapps.bookreader;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Arrays;
import java.util.List;

import mn.mapps.bookreader.util.Util;
import mn.mapps.bookreader.webview.BookReaderWebView;
import mn.mapps.bookreader.webview.BookViewWebClient;
import mn.mapps.bookreader.webview.WebClientJsInterface;
import nl.siegmann.epublib.domain.Book;
import nl.siegmann.epublib.domain.Guide;
import nl.siegmann.epublib.domain.MediaType;
import nl.siegmann.epublib.domain.Resource;
import nl.siegmann.epublib.domain.Spine;
import nl.siegmann.epublib.domain.SpineReference;
import nl.siegmann.epublib.domain.TOCReference;
import nl.siegmann.epublib.epub.EpubReader;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.webkit.WebView;
import android.widget.Toast;

public class BookReaderActivity extends Activity {

	public static final String TAG = BookReaderActivity.class.getSimpleName();
	private int book_id;
	public Book bookEpub;
	private BookReaderWebView webview;
	private Util util = new Util();
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.bookdisplay);
	    
		try{
			String outputPath = util.getRootDirName() +"/"+ util.getRootEpubDirName() +"/"+ util.getEpub_FileDirName();
			String fileName = "preview.epub";
			File file = new File(Environment.getExternalStorageDirectory() + "/"+ outputPath + "/"+ fileName);
			FileInputStream fileInputStream = new FileInputStream(file); 
			bookEpub = (new EpubReader()).readEpub(fileInputStream);
			
		    
	        String linez = "";
		    Spine spine = bookEpub.getSpine();
		    List<SpineReference> spineList = spine.getSpineReferences();
		    StringBuilder string =  new StringBuilder();

		    for(int i = 0; i < spineList.size(); i++){
		    	Resource res = spine.getResource(i);
		    	
		    	InputStream is = res.getInputStream();
		    	BufferedReader reader = new BufferedReader(new InputStreamReader(is));
		    	
		    	String line = "";
				while((line = reader.readLine()) != null){
		    		linez = string.append(line + "\n").toString();
		    	}
				
//				reader.
		    }
		    
		    linez = linez.replace("../", "");
		    linez = linez.replace("href=\"pdlmsr.css\"", "href=\"stl/pdlmsr.css\"");
		    linez = linez.replace("src=\"Images/", "src=\"thmb/");
		    
		    webview = (BookReaderWebView) findViewById(R.id.wv_screen);
		    webview.getSettings().setJavaScriptEnabled(true);
		    webview.getSettings().setBuiltInZoomControls(true);
		    webview.addJavascriptInterface(new WebClientJsInterface(this), "Android");
		    webview.setWebViewClient(new BookViewWebClient());
		    
		    
//		    webview.loadDataWithBaseURL("file://"+ epubBasePath +"/", linez, "text/html", "utf-8", null);

//		    String url = "file://"+Environment.getExternalStorageDirectory() +"/"+ util.getSwiper_HtmlDirName() +"/" + "swiper.html";
//			webview.loadUrl(url);
		    

		} catch (IOException e) {  
			e.printStackTrace();
		}
		
		
	}
	
	private String getHTMLData(String bookHtml) {
        StringBuilder html = new StringBuilder();
        try {
            AssetManager assetManager = getAssets();

            InputStream input = assetManager.open(bookHtml);
            BufferedReader  br = new BufferedReader(new InputStreamReader(input));
            String line;
            while ((line = br.readLine()) != null) {
                html.append(line);
            }
            br.close();
        } catch (Exception e) {
        	e.printStackTrace();
        }

        return html.toString();
    }
	
	@Override
	public void onBackPressed() {
		if(webview.canGoBack())
			webview.goBack();
		else
			super.onBackPressed();
	}
	
	private void logTableOfContents(List<TOCReference> tocReferences, int depth) {
		if (tocReferences == null)
	      return;

		for (TOCReference tocReference : tocReferences) {
			
			StringBuilder tocString = new StringBuilder();
		    
			for (int i = 0; i < depth; i++){
				tocString.append("\t");
			}

		    tocString.append(tocReference.getTitle());
		    Log.i("epublib", tocString.toString());

		    logTableOfContents(tocReference.getChildren(), depth + 1);
		}
		
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.book_reader, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
