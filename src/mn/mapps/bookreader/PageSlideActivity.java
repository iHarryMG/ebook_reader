package mn.mapps.bookreader;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

import nl.siegmann.epublib.domain.Book;
import nl.siegmann.epublib.domain.Spine;
import nl.siegmann.epublib.domain.SpineReference;
import nl.siegmann.epublib.epub.EpubReader;
import mn.mapps.bookreader.adapter.PageSlidePagerAdapter;
import mn.mapps.bookreader.util.Util;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.RelativeLayout;

public class PageSlideActivity extends FragmentActivity {

	private static final String TAG = PageSlideActivity.class.getSimpleName();
	private Util util = new Util();
	private Book bookEpub;
	private ViewPager pager;
	private int NUM_PAGES;
	private String book_id;
	private String book_name;
	private PageSlideActivity thisActivity;
	private String bookFolderName;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_page_slide);
		
		thisActivity = this;
		
		Bundle extras = getIntent().getExtras();
		if (extras != null) {
			book_id = extras.getString("Book_ID");
			book_name = extras.getString("Book_NAME");
			bookFolderName = book_name +"#ID#"+book_id;
			
			Log.i(TAG, "Showing Book " + bookFolderName);
		}
		
		try{
			
			String outputPath = util.getRootDirName() +"/"+ bookFolderName +"/"+ util.getEpub_FileDirName();
			File file = new File(Environment.getExternalStorageDirectory() + "/"+ outputPath + "/"+ book_name + ".epub");
			FileInputStream fileInputStream = new FileInputStream(file); 
			bookEpub = (new EpubReader()).readEpub(fileInputStream);
		    
			pager = (ViewPager) findViewById(R.id.pager);
			pager.setAdapter(new PageSlidePagerAdapter(getFragmentManager(), bookEpub, bookFolderName)); 
			pager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener(){
				@Override
				public void onPageSelected(int position) {
					super.onPageSelected(position);
				}
			});
			
			pager.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					Log.d(TAG, "webview clicked: " + v.getId());
					RelativeLayout dScreen_header = (RelativeLayout)thisActivity.findViewById(R.id.rl_dScreen_header);
					RelativeLayout dScreen_footer = (RelativeLayout)thisActivity.findViewById(R.id.rl_dScreen_footer);
					
					if(dScreen_header.getVisibility() == RelativeLayout.VISIBLE && dScreen_footer.getVisibility() == RelativeLayout.VISIBLE){
						dScreen_header.setVisibility(RelativeLayout.INVISIBLE);
						dScreen_footer.setVisibility(RelativeLayout.INVISIBLE);
					}else{
						dScreen_header.setVisibility(RelativeLayout.VISIBLE);
						dScreen_footer.setVisibility(RelativeLayout.VISIBLE);
					}
				}
			});
			
			
		}catch (IOException e) {  
			e.printStackTrace();
		}
		
	}
}
