package mn.mapps.bookreader.connection;

import java.util.HashMap;
import java.util.Map;

public class ServerApiListContainer {
	
	public static final boolean isDummyDataTest = true;
	public static final boolean isLocal = true;
	public static final boolean isTestServer = false;
	public static final String localDomain = "10.1.120.175";
	public static final String testServerDomain = "10.1.90.65";
	public static final String domain = "developer.mapps.mn";
	public static final String localPort = "9091";
	public static final String port = "443";
	public static final String sub_dir = "/amp-app";
	private static Map<String, String> api;
	private static String url = "";
	private static String googleClientId = "55189899399-p5spmesoh54kb41o5o4f6luahvkd1p4s.apps.googleusercontent.com";
	private static String googleClientIdDebug = "55189899399-p5spmesoh54kb41o5o4f6luahvkd1p4s.apps.googleusercontent.com";
	
	public static String getGoogleClientIdDebug() {
		return googleClientIdDebug;
	}

	public static String getGoogleClientId() {
		return googleClientId;
	}

	public static String getUrl(String key) {
		return api.get(key);
	}
	
	static{
		api = new HashMap<String, String>();
		
		if(isLocal){
			url = "http://"+localDomain+":"+localPort+sub_dir;
		}else{
			url = "https://"+domain+":"+port;
		}
			
		api.put("getMoreFromCategory", 	url+"/ebook-api/ebook-list/by-category");
		api.put("getPurchasedBooks", 	url+"/ebook-api/user/purchased-ebook-list");
		api.put("getEbookDetail", 		url+"/ebook-api/ebook/full-summary");
		api.put("getUserInfo", 			url+"/ebook-api/user/info");
		api.put("getNewBooks", 			url+"/ebook-api/ebook-list/new-ebooks");
		api.put("getMoreList", 			url+"/ebook-api/ebook-list/new-ebooks/more");
		api.put("getMarkedList", 		url+"/ebook-api/ebook-list/marked");
		api.put("search", 				url+"/ebook-api/ebook-list/search");
		api.put("isDownloadFree", 		url+"/ebook-api/ebook/is-free");
		api.put("download", 			url+"/ebook-api/ebook/download");
		api.put("rateEbook", 			url+"/ebook-api/ebook/rate");
		api.put("sendFeedback", 		url+"/ebook-api/app/feedback");
		api.put("getVersion", 			url+"/ebook-api/app/check-update");
		api.put("getReaderVersion", 	"https://www.mapps.mn/mobile/download-reader");
		
	}
}
