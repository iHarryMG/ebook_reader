package mn.mapps.bookreader.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import mn.mapps.bookreader.R;
import mn.mapps.bookreader.app.AppController;
import mn.mapps.bookreader.connection.ServerApiListContainer;
import mn.mapps.bookreader.fragment.ConfirmDialogFragment;
import mn.mapps.bookreader.fragment.ConfirmDialogFragment.onSubmitListener;
import mn.mapps.bookreader.vo.BookVO;
import nl.siegmann.epublib.domain.Book;
import nl.siegmann.epublib.domain.Resource;
import nl.siegmann.epublib.epub.EpubReader;
import nl.siegmann.epublib.service.MediatypeService;
import android.accounts.AccountManager;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.app.Notification.Builder;
import android.app.NotificationManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.BitmapDrawable;
import android.net.ConnectivityManager;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.telephony.TelephonyManager;
import android.text.format.Formatter;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.android.volley.Cache;
import com.android.volley.VolleyError;
import com.android.volley.Cache.Entry;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.ImageLoader.ImageContainer;
import com.android.volley.toolbox.ImageLoader.ImageListener;

//import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
//import com.nostra13.universalimageloader.cache.memory.impl.WeakMemoryCache;
//import com.nostra13.universalimageloader.core.DisplayImageOptions;
//import com.nostra13.universalimageloader.core.ImageLoader;
//import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
//import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
//import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
//import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
//import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
//import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;


public class Util {
	
	private static final String TAG = Util.class.getSimpleName();

//	Directory structure:
//	--------------------
//	_bks 				(root folder)
//		_tmp 			(for download epub, pdf, etc..)
//		_bs_fl 			(swiper page folder)
//			stl 		(css)
//			js 			(javascript)
//			sht 		(html)
//		bk_151208_0001 	(last part is book id)
//			fl 			(epub)
//			thmb 		(imgs)
//			stl 		(css)
//		bk_151208_0002
//		bk_151208_0003
//		bk_151209_0001
//		bk_151209_0002
//		...
	
	private static final String assetsRootWwwDirName = "file:///android_assets/www";
	
	private static final String rootDirName = "_bks";
//	private static final String fileDownloadTmpDirName = rootDirName+"/"+			"_tmp";
	
	public String rootEpubDirName = ""; // Ex: Replace fields to bk_20151209_01
	private static final String epub_StyleDirName = "stl";
	private static final String epub_ImgDirName = "thmb";
	private static final String epub_FileDirName = "fl";

//	private static final String swiperDirName = rootDirName+"/"+				"_bs_fl";
//	private static final String swiper_StyleDirName = swiperDirName+"/"+		"stl";
//	private static final String swiper_JavascriptDirName = swiperDirName+"/"+	"js";
//	private static final String swiper_HtmlDirName = swiperDirName+"/"+			"sht";
	
	private SharedPreferences sp;
	private Editor edit;
	private ProgressDialog mProgressDialog;


	private String jsonList = "["
			+ "{'book_id': 1,'book_name': 'Spring in Action 4th edition','author_name': 'Э.Баярмагнай','cover_pic': 'https://www.mapps.mn/stat/img/logo-head.png','price': 5000,'rating': 3.5,'rating_count': 15,'is_marked': true,'is_new': true}"+
			",{'book_id': 3,'book_name': 'Code Complete','author_name': 'Э.Баярмагнай','cover_pic': 'https://www.mapps.mn/stat/img/logo-head.png','price': 3000,'rating': 3.0,'rating_count': 10,'is_marked': false,'is_new': false}"+
			",{'book_id': 1,'book_name': 'Spring in Action 4th edition','author_name': 'Э.Баярмагнай','cover_pic': 'https://www.mapps.mn/stat/img/logo-head.png','price': 4000,'rating': 4.5,'rating_count': 20,'is_marked': true,'is_new': true}"+
			",{'book_id': 1,'book_name': 'Spring in Action 4th edition','author_name': 'Э.Баярмагнай','cover_pic': 'https://www.mapps.mn/stat/img/logo-head.png','price': 4000,'rating': 4.5,'rating_count': 20,'is_marked': false,'is_new': true}"+
			",{'book_id': 3,'book_name': 'Code Complete','author_name': 'Э.Баярмагнай','cover_pic': 'https://www.mapps.mn/stat/img/logo-head.png','price': 4000,'rating': 4.5,'rating_count': 20,'is_marked': true,'is_new': false}"+
			",{'book_id': 1,'book_name': 'Spring in Action 4th edition','author_name': 'Э.Баярмагнай','cover_pic': 'https://www.mapps.mn/stat/img/logo-head.png','price': 4000,'rating': 4.5,'rating_count': 20,'is_marked': false,'is_new': true}"+
			",{'book_id': 3,'book_name': 'Code Complete','author_name': 'Э.Баярмагнай','cover_pic': 'https://www.mapps.mn/stat/img/logo-head.png','price': 4000,'rating': 4.5,'rating_count': 20,'is_marked': true,'is_new': false}"+
			",{'book_id': 1,'book_name': 'Spring in Action 4th edition','author_name': 'Э.Баярмагнай','cover_pic': 'https://www.mapps.mn/stat/img/logo-head.png','price': 4000,'rating': 4.5,'rating_count': 20,'is_marked': true,'is_new': true}"+
			",{'book_id': 3,'book_name': 'Code Complete','author_name': 'Э.Баярмагнай','cover_pic': 'https://www.mapps.mn/stat/img/logo-head.png','price': 4000,'rating': 4.5,'rating_count': 20,'is_marked': false,'is_new': true}"+
			",{'book_id': 12,'book_name': 'Амжилтын эзэн','author_name': 'Э.Баярмагнай','cover_pic': 'https://www.mapps.mn/stat/img/logo-head.png','price': 4000,'rating': 4.5,'rating_count': 20,'is_marked': true,'is_new': true}"+
			",{'book_id': 13,'book_name': 'Гайхамшигт өглөө','author_name': 'Э.Баярмагнай','cover_pic': 'https://www.mapps.mn/stat/img/logo-head.png','price': 4000,'rating': 4.5,'rating_count': 20,'is_marked': true,'is_new': false}"+
			"]";
	
	public String jsonNewBookList = "{list: ["+
			"{"+
			  "category_name: 'Танд санал болгож байна',"+
			  "category_id: '3',"+
			  "sub_text: '',"+
			  "book_list: [{'price':5000.0,'book_name':'Code Complete','book_id':3,'author_name':'Steve McConnell','is_marked':true},{'price': 5300.0,'book_name':'Code Complete','book_id':3,'author_name':'Craig Walls','is_marked':false}]"+
			"},"+
			"{"+
			  "category_name: 'Эрэлттэй',"+
			  "category_id: '1',"+
			  "sub_text: 'Хамгийн их таталттай 10 ном',"+
			  "book_list: [{'price':4000.0,'book_name':'Spring in Action 4th edition','book_id':1,'author_name':'Craig Walls','is_marked':false},{'price':4100.0,'book_name':'Spring in Action 4th edition','book_id':1,'author_name':'Steve McConnell','is_marked':false},{'price': 4500.0,'book_name':'Spring in Action 4th edition','book_id':1,'author_name':'Steve McConnell','is_marked':true}]"+
			"},"+
			"{"+
			  "category_name: 'Шилдэг',"+
			  "category_id: '2',"+
			  "sub_text: 'Улирлын шилдэг 10 ном',"+
			  "book_list: [{'price':3000.0,'book_name':'ABC','book_id':6,'author_name':'Craig Walls','is_marked':true},{'price':3300.0,'book_name':'DEF','book_id':7,'author_name':'Steve McConnell','is_marked':false}]"+
			"}"+
		"]}";
	
	private String jsonBookDetail = "{"
			+ "'book_id': '3',"
			+ "'book_name': 'Code Complete',"
			+ "'author_name': 'Эрдэнэбатын Баярмагнай',"
			+ "'is_marked': false,"
			+ "'rating': 3.5,"
			+ "'rating_count': 15,"
			+ "'description': 'Wondering what to put on your shopping list this fall? Cosmo scours the runways for the chicest trends to keep you looking fabulous as the weather turns chilly. Get our fashion editors&apos; picks from the hottest boots and bags, to jackets and jewelry.',"
			+ "'publisher': 'BMG',"
			+ "'published_year': 2015,"
			+ "'added_to_mapps_date': '20151222',"
			+ "'page_count': 50,"
			+ "'genre': 'business',"
			+ "'language': 'Монгол',"
			+ "'is_new': true,"
			+ "'price': 0"
			+ "}";
	
	private String jsonPurchasedBookList = "["
			+ "{"
			+ "'book_id': '1',"
			+ "'book_name': 'Spring in Action 4th edition',"
			+ "'author_name': 'Э.Баярмагнай'"
			+"},"
			+ "{"
			+ "'book_id': '3',"
			+ "'book_name': 'Code Complete',"
			+ "'author_name': 'Э.Баярмагнай'"
			+"}"
		+ "]";
	
	
	
	private String fontSize = "["
			+"{ 'Points' : '6pt', 'Pixels' : '8px', 'Ems' : '0.5em', 'Percent' : '50%' },"
			+"{ 'Points' : '7pt', 'Pixels' : '9px', 'Ems' : '0.55em', 'Percent' : '55%' },"
			+"{ 'Points' : '7.5pt', 'Pixels' : '10px', 'Ems' : '0.625em', 'Percent' : '62.50%' },"
			+"{ 'Points' : '8pt', 'Pixels' : '11px', 'Ems' : '0.7em', 'Percent' : '70%' },"
			+"{ 'Points' : '9pt', 'Pixels' : '12px', 'Ems' : '0.75em', 'Percent' : '75%' },"
			+"{ 'Points' : '10pt', 'Pixels' : '13px', 'Ems' : '0.8em', 'Percent' : '80%' },"
			+"{ 'Points' : '10.5pt', 'Pixels' : '14px', 'Ems' : '0.875em', 'Percent' : '87.50%' },"
			+"{ 'Points' : '11pt', 'Pixels' : '15px', 'Ems' : '0.95em', 'Percent' : '95%' },"
			+"{ 'Points' : '12pt', 'Pixels' : '16px', 'Ems' : '1em', 'Percent' : '100%' },"
			+"{ 'Points' : '13pt', 'Pixels' : '17px', 'Ems' : '1.05em', 'Percent' : '105%' },"
			+"{ 'Points' : '13.5pt', 'Pixels' : '18px', 'Ems' : '1.125em', 'Percent' : '112.50%' },"
			+"{ 'Points' : '14pt', 'Pixels' : '19px', 'Ems' : '1.2em', 'Percent' : '120%' },"
			+"{ 'Points' : '14.5pt', 'Pixels' : '20px', 'Ems' : '1.25em', 'Percent' : '125%' },"
			+"{ 'Points' : '15pt', 'Pixels' : '21px', 'Ems' : '1.3em', 'Percent' : '130%' },"
			+"{ 'Points' : '16pt', 'Pixels' : '22px', 'Ems' : '1.4em', 'Percent' : '140%' },"
			+"{ 'Points' : '17pt', 'Pixels' : '23px', 'Ems' : '1.45em', 'Percent' : '145%' },"
			+"{ 'Points' : '18pt', 'Pixels' : '24px', 'Ems' : '1.5em', 'Percent' : '150%' },"
			+"{ 'Points' : '20pt', 'Pixels' : '26px', 'Ems' : '1.6em', 'Percent' : '160%' },"
			+"{ 'Points' : '22pt', 'Pixels' : '29px', 'Ems' : '1.8em', 'Percent' : '180%' },"
			+"{ 'Points' : '24pt', 'Pixels' : '32px', 'Ems' : '2em', 'Percent' : '200%' },"
			+"{ 'Points' : '26pt', 'Pixels' : '35px', 'Ems' : '2.2em', 'Percent' : '220%' },"
			+"{ 'Points' : '27pt', 'Pixels' : '36px', 'Ems' : '2.25em', 'Percent' : '225%' },"
			+"{ 'Points' : '28pt', 'Pixels' : '37px', 'Ems' : '2.3em', 'Percent' : '230%' },"
			+"{ 'Points' : '29pt', 'Pixels' : '38px', 'Ems' : '2.35em', 'Percent' : '235%' },"
			+"{ 'Points' : '30pt', 'Pixels' : '40px', 'Ems' : '2.45em', 'Percent' : '245%' },"
			+"{ 'Points' : '32pt', 'Pixels' : '42px', 'Ems' : '2.55em', 'Percent' : '255%' },"
			+"{ 'Points' : '34pt', 'Pixels' : '45px', 'Ems' : '2.75em', 'Percent' : '275%' },"
			+"{ 'Points' : '36pt', 'Pixels' : '48px', 'Ems' : '3em', 'Percent' : '300%' }"
			+ "]";

	public String getJsonPurchasedBookList() {
		return jsonPurchasedBookList;
	}
	
	public String getJsonBookDetail() {
		return jsonBookDetail;
	}

	public String getJsonNewBookList() {
		return jsonNewBookList;
	}
	
	public String getJsonBookListForTest() {
		return jsonList;
	}
	
	public Bitmap editImageSize(int drawableImg, int width, int height, Resources resources) {
		Bitmap bmp;
		bmp=BitmapFactory.decodeResource(resources, drawableImg);                                                            
		bmp=Bitmap.createScaledBitmap(bmp, width, height, true);
		return bmp;
	}
	
	public int getScreenWidth(Activity activity){
		DisplayMetrics dm = new DisplayMetrics();
		activity.getWindowManager().getDefaultDisplay().getMetrics(dm);
		return dm.widthPixels;
	}
	
	public int getScreenHeight(Activity activity){
		DisplayMetrics dm = new DisplayMetrics();
		activity.getWindowManager().getDefaultDisplay().getMetrics(dm);
		return dm.heightPixels;
	}
	
	public boolean createDirectory(String dirName) {   
		boolean result = false;
		String dirPath = new String(Environment.getExternalStorageDirectory()+"/"+dirName);
	    File file = new File(dirPath);
	    boolean exists = file.exists();
		if (!exists) {
	        result = file.mkdirs();
	        if(result)
	        	Log.i(TAG, "Directory created SUCCESSFULLY at: " + dirPath );
	        else
	        	Log.e(TAG, "Directory creation FAILED at: " + dirPath );
	    }else {
	    	Log.i(TAG, "Unable to create directory at: " + dirPath );
	    	Log.i(TAG, "Directory named \""+dirName+"\" exists");
	    	result = true;        
	    }
		
		return result;
	}
	
	public void copyFileToDevice(Context context, String fileName, String inputPath, String outputPath) {     
	    try {
	    	String outPath = Environment.getExternalStorageDirectory() + "/" + outputPath + "/" + fileName;

	    	if(!isFileExist(outPath)){
		        InputStream localInputStream = context.getAssets().open(inputPath + "/" + fileName);
				FileOutputStream localFileOutputStream = new FileOutputStream(outPath);
		
		        byte[] arrayOfByte = new byte[1024];
		        int offset;
		        while ((offset = localInputStream.read(arrayOfByte))>0){
		            localFileOutputStream.write(arrayOfByte, 0, offset);                  
		        }
		        
		        localFileOutputStream.close();
		        localInputStream.close();
		        Log.i(TAG, fileName + " file copied to " + outputPath);   
	    	}else
	    		Log.i(TAG, fileName + " file exists at " + outputPath);
	    }
	    catch (IOException localIOException){
	        localIOException.printStackTrace();
	        Log.e(TAG, "failed to copy file " + fileName);
	        return;
	    }
	}
	

	public void downloadResource(Book book, String directory) {
	    try {
	
	        nl.siegmann.epublib.domain.Resources rst = book.getResources();
	        Collection<Resource> clrst = rst.getAll();
	        Iterator<Resource> itr = clrst.iterator();
	        
	        while (itr.hasNext()) {
	            Resource resource = itr.next();
	
	            if ((resource.getMediaType() == MediatypeService.JPG) || (resource.getMediaType() == MediatypeService.PNG) || (resource.getMediaType() == MediatypeService.GIF)) {
	
	                String href = resource.getHref();
					Log.i(TAG, href);
					int indexOfSymbol = href.indexOf("/");
					Log.i(TAG, "IMAGE's index of / is: " + indexOfSymbol);
					
					String hrefName = resource.getHref().replace("OEBPS/", "");
					hrefName = hrefName.replace("IMAGES/", "");
					hrefName = hrefName.replace("IMAGE/", "");
					hrefName = hrefName.replace("Images/", "");
					hrefName = hrefName.replace("Image/", "");
					hrefName = hrefName.replace("images/", "");
					hrefName = hrefName.replace("image/", "");
					hrefName = hrefName.replace("imgs/", "");
					hrefName = hrefName.replace("img/", "");
					File file = new File(directory, epub_ImgDirName+"/"+hrefName);    
	
	                file.getParentFile().mkdirs();
	                file.createNewFile();
	
	                System.out.println("Path : "+file.getParentFile().getAbsolutePath());
	
	                FileOutputStream outputStream = new FileOutputStream(file);
	                outputStream.write(resource.getData());
	                outputStream.close();
	
	            } else if (resource.getMediaType() == MediatypeService.CSS) {
	            	
	            	String href = resource.getHref();
					Log.i(TAG, href);
					int indexOfSymbol = href.indexOf("/");
					Log.i(TAG, "CSS's index of / is: " + indexOfSymbol);
	            	
	            	
	                String hrefName = resource.getHref().replace("OEBPS/", "");
	                hrefName = hrefName.replace("CSS/", "");
	                hrefName = hrefName.replace("css/", "");
					File file = new File(directory, epub_StyleDirName+"/"+hrefName);
	
	                file.getParentFile().mkdirs();
	                file.createNewFile();
	                
	                System.out.println("Path : "+file.getParentFile().getAbsolutePath());
	
	                FileOutputStream outputStream = new FileOutputStream(file);
	                outputStream.write(resource.getData());
	                outputStream.close();
	
	            }
	
	        }
	
	    } catch (Exception e) {
	    	e.printStackTrace();
	    }
	}
	
	public boolean isFileExist(String path){
		File file = new File(path);
		if(file.exists()){
			if (file.isFile())
				return true;
			else
				return false;
		}else
			return false;
	}
	
	@SuppressWarnings("deprecation")
	public boolean[] getNetworkState(Context context) {
		boolean[] networkState = new boolean[2];
    	
    	ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

		boolean isWifi = manager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).isConnectedOrConnecting();
    	boolean is3g = manager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).isConnectedOrConnecting();

    	networkState[0] = isWifi;
    	networkState[1] = is3g;
    	
    	Log.d(TAG, "Wifi state:" + networkState[0] + ", Data state:" + networkState[1]);
		return networkState;
	}
	
	/*@SuppressWarnings("deprecation")
	public void displayImageEbook(String ebookId, ImageView image, String imageSize) {
		
		String url = null;
		if(ServerApiListContainer.isLocal){
			 url = "http://" + ServerApiListContainer.localDomain + ":" + ServerApiListContainer.localPort + ServerApiListContainer.sub_dir + "/amp/repo/resource_ebook/10/"
					+ ebookId + "/10/" + imageSize;
		}else{
			if(ServerApiListContainer.isTestServer){
				 url = "http://" + ServerApiListContainer.testServerDomain + "/dev" +"/amp/repo/resource_ebook/1/"
						+ ebookId + "/10/" + imageSize;
			}else{
				 url = "https://" + ServerApiListContainer.domain + ":" + ServerApiListContainer.port + "/amp/repo/resource_ebook/1/"
						+ ebookId + "/10/" + imageSize;
			}
		}
		
//		 init(image.getContext());

		try {
 			ImageLoader.getInstance().displayImage(url, image, animateFirstListener); //options, 
		} catch (IllegalStateException e) {
			init(image.getContext());
			ImageLoader.getInstance().displayImage(url, image, animateFirstListener); //options, 
		}
	}
	
	static ImageLoadingListener animateFirstListener = null;
	static DisplayImageOptions options;
	
	private void init(Context context){

		options = new DisplayImageOptions.Builder().cacheInMemory()
						.cacheOnDisc().displayer(new RoundedBitmapDisplayer(8)).build();
						
		animateFirstListener = new AnimateFirstDisplayListener();
		
		ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(context).threadPriority(Thread.NORM_PRIORITY - 2)
				.memoryCache(new WeakMemoryCache())
				.denyCacheImageMultipleSizesInMemory()
				.discCacheFileNameGenerator(new Md5FileNameGenerator())
				.tasksProcessingOrder(QueueProcessingType.FIFO)// .enableLogging()
																// // Not
				.build();
		// Initialize ImageLoader with configuration.
		ImageLoader.getInstance().init(config);
	}
	
	private static class AnimateFirstDisplayListener extends SimpleImageLoadingListener {

		static final List<String> displayedImages = Collections .synchronizedList(new LinkedList<String>());

		@Override
		public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
			if (loadedImage != null) {
				ImageView imageView = (ImageView) view;
				boolean firstDisplay = !displayedImages.contains(imageUri);
				if (firstDisplay) {
					FadeInBitmapDisplayer.animate(imageView, 100);
					displayedImages.add(imageUri);
				}
			}
		}
	}*/
	
	public String getDgn(Context context){
		String dgn = null;
		try{
			String fn = "idx-file.idx";
			InputStream inputStream = context.openFileInput(fn);
			BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
			dgn = bufferedReader.readLine();
			inputStream.close();
		}catch(Exception ex){
			ex.printStackTrace();
		}
		return dgn;
	}
	
	public String getCarrier(Context context){
		TelephonyManager telephonyManager = ((TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE));
		return  telephonyManager.getNetworkOperatorName();
	}
	
	@SuppressWarnings("deprecation")
	public String getIp(Context context){
		WifiManager wm = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
		String ip = Formatter.formatIpAddress(wm.getConnectionInfo().getIpAddress());
		return ip;
	}
	
/*	Номыг номын санруу оруулах дараалал:
		1. createBookDirectories-г ашиглаж номын localFolder үүсгэнэ
		2. parseBook-г ашиглаж номын epub эхээс зураг, css файлуудыг задлаж хадгалах*/
		
	public void createBookDirectories(String book_name, String book_id) {
        setRootEpubDirName(book_name + "#ID#" + book_id);
        Log.i(TAG, "epubDirName: " + getRootEpubDirName());
        createDirectory(getRootDirName() +"/"+ getRootEpubDirName());
        createDirectory(getRootDirName() +"/"+ getRootEpubDirName() + "/" +getEpub_FileDirName());
        createDirectory(getRootDirName() +"/"+ getRootEpubDirName() + "/" +getEpub_ImgDirName());
        createDirectory(getRootDirName() +"/"+ getRootEpubDirName() + "/" +getEpub_StyleDirName());
	}
	
	public void parseBook(String book_name) {
		try {
	        String outputPath = getRootDirName() +"/"+ getRootEpubDirName() +"/"+ getEpub_FileDirName();
			File file = new File(Environment.getExternalStorageDirectory() + "/"+ outputPath + "/"+ book_name +".epub");
			FileInputStream fileInputStream = new FileInputStream(file); 
			Book bookEpub = (new EpubReader()).readEpub(fileInputStream);
			
			// Step 5. Extract css, img files to the dir
			String epubBasePath = Environment.getExternalStorageDirectory() + "/" + getRootDirName() +"/"+ getRootEpubDirName();
	        downloadResource(bookEpub, epubBasePath);
        
		} catch (IOException e) {  
			e.printStackTrace();
		}
	}
	
	public void copyFile(String inputPath, String inputFile, String outputPath) {

	    InputStream in = null;
	    OutputStream out = null;
	  
	    try {

	        in = new FileInputStream(inputPath + inputFile);        
	        out = new FileOutputStream(outputPath + inputFile);

	        byte[] buffer = new byte[1024];
	        int read;
	        while ((read = in.read(buffer)) != -1) {
	            out.write(buffer, 0, read);
	        }
	        in.close();
	        in = null;

	        // write the output file (You have now copied the file)
	        out.flush();
	        out.close();
	        out = null;        

	    }catch (FileNotFoundException fnfe1) {
	    	Log.e(TAG, fnfe1.getMessage());
	    	
	    }catch (Exception e) {
	        Log.e(TAG, e.getMessage());
	    }

	}
	
	public ArrayList<BookVO> getLibraryList(){
		ArrayList<BookVO> bookVoArray = null;
		String rootDir = getRootDirName();
		File dir = new File(Environment.getExternalStorageDirectory() + "/" + rootDir);
		
		if(dir.exists()){
			bookVoArray = new ArrayList<BookVO>();
			File[] files = dir.listFiles();
			
			for (File file : files) {
			    if (file.isDirectory()) {
			    	
		    		BookVO bookVO = new BookVO();		    		
		    		String[] splitedValue = file.getName().split("#ID#");
		    		
		    		String book_name = splitedValue[0];
		    		bookVO.setBook_name(book_name);
		    		bookVoArray.add(bookVO);
			    }
			}
		}
		
		return bookVoArray;
	}
	
	public String getPhoneManufacturer(){
		return Build.MANUFACTURER;
	}
	
	public String getPhoneModel(){
		return Build.MODEL;
	}
	
	public String getPhoneSdk(){
		return Build.VERSION.RELEASE;
	}
	
	public String getRootDirName() {
		return rootDirName;
	}

//	public String getFileDownloadTmpDirName() {
//		return fileDownloadTmpDirName;
//	}

//	public String getSwiperDirName() {
//		return swiperDirName;
//	}
//
//	public String getSwiper_StyleDirName() {
//		return swiper_StyleDirName;
//	}
//
//	public String getSwiper_JavascriptDirName() {
//		return swiper_JavascriptDirName;
//	}
//
//	public String getSwiper_HtmlDirName() {
//		return swiper_HtmlDirName;
//	}

	public void setRootEpubDirName(String bookName) {
		this.rootEpubDirName = bookName;
	}
	
	public String getRootEpubDirName() {
		return rootEpubDirName;
	}

	public String getEpub_StyleDirName() {
		return epub_StyleDirName;
	}

	public String getEpub_ImgDirName() {
		return epub_ImgDirName;
	}

	public String getEpub_FileDirName() {
		return epub_FileDirName;
	}

	public static String getAssetsRootWwwDirName() {
		return assetsRootWwwDirName;
	}
	
	/* Checks if external storage is available for read and write */
	public boolean isExternalStorageWritable() {
	    String state = Environment.getExternalStorageState();
	    if (Environment.MEDIA_MOUNTED.equals(state)) {
	        return true;
	    }
	    return false;
	}

	/* Checks if external storage is available to at least read */
	public boolean isExternalStorageReadable() {
	    String state = Environment.getExternalStorageState();
	    if (Environment.MEDIA_MOUNTED.equals(state) ||
	        Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
	        return true;
	    }
	    return false;
	}

	public void removeDirectory(String bookPath) {
		String path = Environment.getExternalStorageDirectory() + "/" +getRootDirName() + "/" + bookPath;
		File file = new File(path);
		if(file.exists()){
			deleteRecursive(file);
		}
		File check = new File(path);
		if(!check.exists())
			Log.i(TAG, path + " directory deleted.");
	}
	
	private void deleteRecursive(File fileOrDirectory) {
	    if (fileOrDirectory.isDirectory())
	        for (File child : fileOrDirectory.listFiles())
	        {
	            child.delete();
	            deleteRecursive(child);
	        }

	    fileOrDirectory.delete();
	}

	public boolean isDirectoryExist(String path) {
		String filePath = Environment.getExternalStorageDirectory() + "/" +getRootDirName() + "/" + path;
		File file = new File(filePath);
		return file.exists();
	}
	
	public void cacheData(Context context, String name, String value) {
		sp = PreferenceManager.getDefaultSharedPreferences(context);
		edit = sp.edit();
		edit.putString(name, value);
		edit.commit();
	}
	
	public String getCacheData(Context context, String name, String defaultValue) {
		sp = PreferenceManager.getDefaultSharedPreferences(context);
		return sp.getString(name, defaultValue);
	}
	
	public void removeCacheData(Context context, String name) {
		sp = PreferenceManager.getDefaultSharedPreferences(context);
		edit = sp.edit();
		edit.remove(name);
		edit.commit();
	}

	public void showProgressDialog(Context context) {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(context);
            mProgressDialog.setMessage("Ачааллаж байна..");
            mProgressDialog.setIndeterminate(true);
        }

        mProgressDialog.show();
    }

	public void hideProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.hide();
        }
    }
	
	public void displayImageEbook(String ebookId, final ImageView imageView, String imageSize) {
		
		String url = null;
		if(ServerApiListContainer.isLocal){
			 url = "http://" + ServerApiListContainer.localDomain + ":" + ServerApiListContainer.localPort + ServerApiListContainer.sub_dir + "/amp/repo/resource_ebook/10/"
					+ ebookId + "/10/" + imageSize;
		}else{
			if(ServerApiListContainer.isTestServer){
				 url = "http://" + ServerApiListContainer.testServerDomain + "/dev" +"/amp/repo/resource_ebook/1/"
						+ ebookId + "/10/" + imageSize;
			}else{
				 url = "https://" + ServerApiListContainer.domain + ":" + ServerApiListContainer.port + "/amp/repo/resource_ebook/1/"
						+ ebookId + "/10/" + imageSize;
			}
		}
		
		ImageLoader imageLoader = AppController.getInstance().getImageLoader();

		// If you are using normal ImageView
		imageLoader.get(url, new ImageListener() {

			@Override
			public void onErrorResponse(VolleyError error) {
				Log.e(TAG, "Image Load Error: " + error.getMessage());
			}

			@Override
			public void onResponse(ImageContainer response, boolean arg1) {
				if (response.getBitmap() != null) {
					// load image into imageview
					imageView.setImageBitmap(response.getBitmap());
				}
			}
		});

		// Loading image with placeholder and error image
		imageLoader.get(url, ImageLoader.getImageListener(
				imageView, R.drawable.loading, R.drawable.ico_error));
		
//		TODO: Use this for cached images
		Cache cache = AppController.getInstance().getRequestQueue().getCache();
		Entry entry = cache.get(url);
		if(entry != null){
			try {
				String data = new String(entry.data, "UTF-8");
				// handle data, like converting it to xml, json, bitmap etc.,
			} catch (UnsupportedEncodingException e) {		
				e.printStackTrace();
			}
		}else{
			// cached response doesn't exists. Make a network call here
		}

	}
	
	public String humanReadableByteCount(long bytes, boolean si) {
	    int unit = si ? 1000 : 1024;
	    if (bytes < unit) return bytes + "B";
	    int exp = (int) (Math.log(bytes) / Math.log(unit));
	    String pre = (si ? "kMGTPE" : "KMGTPE").charAt(exp-1) + (si ? "" : "i");
	    return String.format("%.1f %sB", bytes / Math.pow(unit, exp), pre);
	}
	
	public String convertTime(long time){
	    Date date = new Date(time);
	    Format format = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
	    return format.format(date);
	}
	
	public void crawlDirectories(File dir, String pattern, ArrayList<BookVO> fileList) throws IOException {

	    File listFile[] = dir.listFiles();

	    if (listFile != null) {
	        for (int i = 0; i < listFile.length; i++) {

	            if(listFile[i].isDirectory()) {
	            	crawlDirectories(listFile[i], pattern, fileList);
	            }else{
	            	
	            	if (listFile[i].getName().endsWith(pattern)){
	            		BookVO book = new BookVO();
	            		String name = listFile[i].getName();
	            		name = name.replace(".epub", "");
	            		book.setBook_name(name);
	            		String path = listFile[i].getPath();
	            		path = path.replace(name+".epub", "");
	            		book.setPath(path);
	            		
	            		long fileLength = listFile[i].getAbsoluteFile().length();
	            		book.setFileSize(humanReadableByteCount(fileLength, true));
	            		
	            		long lastModified = listFile[i].getAbsoluteFile().lastModified();
	            		book.setLastModifiedDate(convertTime(lastModified));
	            		
	            		fileList.add(book);
	              }
	            }
	        }
	    }    
	}
	
}
