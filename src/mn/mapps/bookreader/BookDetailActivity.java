package mn.mapps.bookreader;

import java.text.DecimalFormat;
import java.text.ParseException;

import mn.mapps.bookreader.connection.AsyncCallBack;
import mn.mapps.bookreader.connection.ServerRequestHandler.onFileDownloadListener;
import mn.mapps.bookreader.controller.ActionController;
import mn.mapps.bookreader.fragment.ConfirmDialogFragment;
import mn.mapps.bookreader.fragment.MyLibrarySectionFragment;
import mn.mapps.bookreader.fragment.RateDialogFragment;
import mn.mapps.bookreader.fragment.RateDialogFragment.onRateSubmitListener;
import mn.mapps.bookreader.service.BookService;
import mn.mapps.bookreader.util.Util;
import mn.mapps.bookreader.vo.BookVO;
import mn.mapps.bookreader.fragment.ConfirmDialogFragment.onSubmitListener;  
import mn.mapps.bookreader.listener.OnBookItemClickListener;

import org.json.JSONObject;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.RatingBar.OnRatingBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;

public class BookDetailActivity extends FragmentActivity implements onSubmitListener, onFileDownloadListener, onRateSubmitListener{

	private static final String TAG = BookDetailActivity.class.getSimpleName();
	private static Context context;
	private static Util util = new Util();
	private BookService bookService;
	private String bookDetailResult;
	private JSONObject bookObject;
	private ActionController actionController;
	private static BookVO book;
	private TextView bdFileDownloadSize;
	private TextView bdFileDownloadPercent;
	private ProgressBar bdFileDownloadProgress;
	private Button btnStopDownload;
	private static LinearLayout bdDownloadButtons;
	private static LinearLayout bdDownloadProgress;
	private static NotificationManager notificationManager;
	private static NotificationCompat.Builder builder;

	public static final int BOOK_DOWNLOAD_COMPLETED = 2000;

	public static final Handler handler = new Handler() {
		public void handleMessage(Message msg) {

			if (msg.what == BOOK_DOWNLOAD_COMPLETED){
				completedBookDownload();
			}
		}
	};
	
	private static Button btnOpenBook;
	private static Button btnDownloadSample;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.bookdetail);
		context = this;
		actionController = new ActionController(this);
		
		getActionBar().setDisplayShowTitleEnabled(false);
		getActionBar().setDisplayHomeAsUpEnabled(true);

		
		Bundle extras = getIntent().getExtras();
		if (extras != null) {
			util.showProgressDialog(context);

			bookDetailResult = extras.getString("Ebook_Detail_Result");
		    Log.i(TAG, "Ebook_Detail_Result ID = " + bookDetailResult);
		    
		    if(bookDetailResult != null && !bookDetailResult.isEmpty()){
		    	BookVO book = getBookDetail(bookDetailResult);
		    	
		    	if(book != null){
		    		setViewBookInfo(book);
		    	}
		    }
		    
		    util.hideProgressDialog();
		}else{
			Log.e(TAG, "Ebook Detail Result not found!");
			Toast.makeText(this, "Номны мэдээлэл харуулахад алдаа гарлаа", Toast.LENGTH_SHORT).show();
			onBackPressed();
		}
	}
	
	private void setViewBookInfo(final BookVO book) {
		
		this.book = book;
		ImageView iv_backgroundImage = (ImageView) findViewById(R.id.iv_backgroundImage);
		ImageView iv_bookCover = (ImageView) findViewById(R.id.iv_fileIcon);
		btnDownloadSample = (Button) findViewById(R.id.btn_bd_downloadSample);
		Button btnPurchaseBook = (Button) findViewById(R.id.btn_bd_purchaseBook);
		btnOpenBook = (Button) findViewById(R.id.btn_bd_openBook);
		
		TextView tv_bookName = (TextView) findViewById(R.id.tv_bookName);
		TextView tv_author = (TextView) findViewById(R.id.tv_author);
		TextView tv_pubDate_bookDetail = (TextView) findViewById(R.id.tv_pubDate_bookDetail);
		TextView tv_price_bookDetail = (TextView) findViewById(R.id.tv_price_bookDetail);
		
		TextView tv_totalRateCount = (TextView) findViewById(R.id.tv_totalRateCount);
		TextView tv_bd_totalRate = (TextView) findViewById(R.id.tv_bd_totalRate);
		TextView tv_bookDescription = (TextView) findViewById(R.id.tv_bookDescription);
		
		RatingBar rb_bookRatingBar = (RatingBar) findViewById(R.id.rb_bookRatingBar);
		RatingBar rb_rateBook = (RatingBar) findViewById(R.id.rb_rateBook_dialog);
		
		rb_bookRatingBar.setRating(book.getRating());
		
		rb_rateBook.setOnRatingBarChangeListener(new OnRatingBarChangeListener() {
			@Override
			public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
				FragmentTransaction ft = getFragmentManager().beginTransaction();
    		    Fragment prev = getFragmentManager().findFragmentByTag("ratedialog");
    		    if (prev != null) {
    		        ft.remove(prev);
    		    }
    		    ft.addToBackStack(null);
    		    
				RateDialogFragment newFragment = RateDialogFragment.newInstance(rating);
    		    newFragment.mListener = BookDetailActivity.this;
    		    newFragment.show(ft, "ratedialog");
			}
		});
		
		util.displayImageEbook(book.getBook_id(), iv_backgroundImage, "600");
		util.displayImageEbook(book.getBook_id(), iv_bookCover, "300");
		
		tv_bookName.setText(book.getBook_name());
		tv_author.setText("Зохиогчийн нэр: " + book.getAuthor_name());
		tv_pubDate_bookDetail.setText("Хэвлэгдсэн огноо: " + book.getPublished_year() + "он");
		if(0 < book.getPrice())
			tv_price_bookDetail.setText("Үнэ: " + book.getPrice() + "₮");
		else
			tv_price_bookDetail.setText("Үнэгүй");
		
		tv_totalRateCount.setText(Integer.toString(book.getRating_count()));
		tv_bd_totalRate.setText(Double.toString(book.getRating()));
		tv_bookDescription.setText(book.getDescription());
		
		bdFileDownloadSize = (TextView) findViewById(R.id.tv_bdFileDownloadSize);
		bdFileDownloadPercent = (TextView) findViewById(R.id.tv_bdFileDownloadPercent);
		bdFileDownloadProgress = (ProgressBar) findViewById(R.id.pb_bdFileDownloadProgress);
		bdFileDownloadProgress.setMax(100);
		btnStopDownload = (Button) findViewById(R.id.btnStopDownload);
		bdDownloadButtons = (LinearLayout) findViewById(R.id.ll_bdDownloadButtons);
		bdDownloadProgress = (LinearLayout) findViewById(R.id.ll_bdDownloadProgress);
		
//		book.getRating_all();
		
		btnDownloadSample.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				boolean[] networkState = util.getNetworkState(getApplicationContext());
            	
            	if(networkState[0] == false && networkState[1] == false){
            		Toast.makeText(getApplicationContext(), "Интернэтэд холбогдоогүй байна!", Toast.LENGTH_SHORT).show();
            	}else{
            		if(networkState[0] == false && networkState[1] == true){
//            			TODO: Confirm dialog гаргах
            			Toast.makeText(getApplicationContext(), "3G интернэт холбогдсон байна!", Toast.LENGTH_SHORT).show();
            		}else{
            			String book_name = book.getBook_name();
            			if(22 < book_name.length())
            				book_name = (book_name.substring(0, 20)) + ".."; 

            			showConfirmDialog("dialog", book_name, "татаж авах", "Файлын хэмжээ:", "3MB");
            		}
            	}
			}

			private void showConfirmDialog(String dialogTag, String title1, String title2, String label1, String label2) {
				FragmentTransaction ft = getFragmentManager().beginTransaction();
				Fragment prev = getFragmentManager().findFragmentByTag(dialogTag);
				if (prev != null) {
				    ft.remove(prev);
				}
				ft.addToBackStack(null);
					
				ConfirmDialogFragment newFragment = ConfirmDialogFragment.newInstance(title1, title2, label1, label2);
				newFragment.mListener = BookDetailActivity.this;
				newFragment.show(ft, dialogTag);
			}
		});
		
		btnPurchaseBook.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				
			}
		});
		
		btnStopDownload.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				actionController.stopDownload();
				
				bdFileDownloadSize.setText("");
				bdFileDownloadPercent.setText("");
				bdFileDownloadProgress.setProgress(0);

				bdDownloadButtons.setVisibility(LinearLayout.VISIBLE);
				bdDownloadProgress.setVisibility(LinearLayout.GONE);

				builder.setContentText("Татан авалт цуцлагдсан.").setProgress(0, 0, false);
				notificationManager.notify(0, builder.build());
				
				Log.e(TAG, "Download Process Cancelled");
				Toast.makeText(context, book.getBook_name() + " татан авалт цуцлагдлаа.", Toast.LENGTH_SHORT).show();
			}
		});
		
		btnOpenBook.setOnClickListener(new OnBookItemClickListener(context, book.getBook_id(), book.getBook_name(), TabbedActivity.MyLibrarySection));
//		btnOpenBook.setOnClickListener(new OnBookItemClickListener(context, "1", "Spring in Action 4th edition", TabbedActivity.MyLibrarySection));
		
		checkViewState(book.getBook_name() +"#ID#"+ book.getBook_id());
//		checkViewState("Spring in Action 4th edition_1");
	}
	
	private static void checkViewState(String path){
		if(util.isDirectoryExist(path)){
			btnOpenBook.setVisibility(Button.VISIBLE);
			btnDownloadSample.setVisibility(Button.GONE);
		}else{
			btnOpenBook.setVisibility(Button.GONE);
			btnDownloadSample.setVisibility(Button.VISIBLE);
		}
	}
	
	@Override
	public void setOnSubmitListener() {
		bdFileDownloadSize.setText("Татан авалт эхэлж байна..");
		bdFileDownloadPercent.setText("");
		bdFileDownloadProgress.setProgress(0);
		
		bdDownloadButtons.setVisibility(LinearLayout.GONE);
		bdDownloadProgress.setVisibility(LinearLayout.VISIBLE);
		
		notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
		builder = new NotificationCompat.Builder(context);
		builder.setContentTitle(book.getBook_name()).setContentText("Файлыг татаж эхэллээ..").setSmallIcon(R.drawable.downloading);
		notificationManager.notify(0, builder.build());
		
		String userEmail = util.getCacheData(context, "user_email", "");
		String token = util.getCacheData(context, "token", "");
		actionController.downloadSample(userEmail, book.getBook_id(), token, 
				util.getDgn(getApplicationContext()), book.getBook_name());
	}
	
	private static void completedBookDownload() {
		checkViewState(book.getBook_name() +"#ID#"+ book.getBook_id());
		bdDownloadButtons.setVisibility(LinearLayout.VISIBLE);
		bdDownloadProgress.setVisibility(LinearLayout.GONE);
		
		builder.setContentText("Файлыг амжилттай татаж дууслаа.").setProgress(0, 0, false);
		builder.setSmallIcon(R.drawable.download_completed_white);
		notificationManager.notify(0, builder.build());
		
		MyLibrarySectionFragment.handler.sendEmptyMessage(MyLibrarySectionFragment.UPDATE_LIBRARY_LIST);
		Toast.makeText(context, book.getBook_name() + " номын амталгаа амжилттай татагдлаа.", Toast.LENGTH_SHORT).show();
	}

	private BookVO getBookDetail(String result){
		try {
			BookVO book = new BookVO();
			bookObject = new JSONObject(result);
			bookService = new BookService();
			bookService.setBookItemValues(book, bookObject);
			
			return book;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    if (item.getItemId() == android.R.id.home) {
	        onBackPressed();
	    }
	    return super.onOptionsItemSelected(item);
	}

	@Override
	public void setOnFileDownloadListener(final long totalSizeInMb, final long fileSizeInMb, final long downloadProgressInPercentage) {
		builder.setProgress(100, (int) downloadProgressInPercentage, false);
		builder.setContentText(downloadProgressInPercentage + "% татагдлаа..");
		notificationManager.notify(0, builder.build());
		
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				bdFileDownloadPercent.setText(downloadProgressInPercentage + "%");
				bdFileDownloadSize.setText(util.humanReadableByteCount(totalSizeInMb, true) + "/" + util.humanReadableByteCount(fileSizeInMb, true));
				bdFileDownloadProgress.setProgress((int) downloadProgressInPercentage);
			}
		});
	}

	@Override
	public void setOnRateSubmitListener(int rating, String comment) {
		String userEmail = util.getCacheData(context, "user_email", "");
		actionController.rateEbook(userEmail, book.getBook_id(), rating, 
				comment, util.getPhoneManufacturer(), util.getPhoneModel(), new AsyncCallBack<String>(){

			@Override
			public void onSuccess(final String result) {
				Log.i(TAG, "rating Books Succeeded: \n\n" + result);
				
				if("1".equals(result))
					Toast.makeText(getApplication(), "Үнэлгээ өгсөнд баярлалаа!", Toast.LENGTH_SHORT).show();
				else if("-1".equals(result))
					Toast.makeText(getApplication(), "Та MAPPS-д бүртгэлгүй байгаа тул үнэлгээ өгөх боломжгүй байна!", Toast.LENGTH_SHORT).show();
				else
					Toast.makeText(getApplication(), "Үнэлгээ өгөхөд алдаа гарлаа!", Toast.LENGTH_SHORT).show();
			}

			@Override
			public void onFailure(Throwable th) {
				Log.i(TAG, "Error on Rating book");
				th.printStackTrace();
			}
		});
	}

}
