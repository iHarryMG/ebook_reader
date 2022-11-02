package mn.mapps.bookreader.webview;

import android.app.Activity;
import android.content.Context;
import android.webkit.JavascriptInterface;
import android.widget.Toast;

public class WebClientJsInterface {
	
	Context activity;

	public WebClientJsInterface(Context activity) {
        this.activity = activity;
    }

    @JavascriptInterface
    public void showToast(String toast) {
        Toast.makeText(activity, toast, Toast.LENGTH_SHORT).show();
    }

    @JavascriptInterface
    public void closeActivity() {
        ((Activity) activity).finish();
    }
    
}
