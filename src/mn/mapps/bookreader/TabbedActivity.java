package mn.mapps.bookreader;

import java.util.Locale;

import mn.mapps.bookreader.fragment.BookmarkedSectionFragment;
import mn.mapps.bookreader.fragment.MyLibrarySectionFragment;
import mn.mapps.bookreader.fragment.NewBookSectionFragment;
import mn.mapps.bookreader.service.BookService;
import mn.mapps.bookreader.R;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

public class TabbedActivity extends Fragment {

	public static final String TAG = TabbedActivity.class.getSimpleName();
	
	public static final int MyLibrarySection= 101;
	public static final int NewBookSection= 102;
	public static final int BookmarkedSection = 103;
	public static final int NewBookMoreList= 104;
	
	public static boolean isPortrait = true;
	public static BookService bookService = null;
	
	private SectionsPagerAdapter mSectionsPagerAdapter;
	private ViewPager mViewPager;

	private MyLibrarySectionFragment myLibrarySectionFragment;

	private NewBookSectionFragment newBookSectionFragment;

	private BookmarkedSectionFragment bookmarkedSectionFragment;

	public static TabbedActivity newInstance() {
		return new TabbedActivity();
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.activity_item_one, container, false);
		mSectionsPagerAdapter = new SectionsPagerAdapter(getChildFragmentManager());
		mViewPager = (ViewPager) v.findViewById(R.id.pager);
		mViewPager.setAdapter(mSectionsPagerAdapter);
		
		myLibrarySectionFragment = new MyLibrarySectionFragment();
		newBookSectionFragment = new NewBookSectionFragment();
		bookmarkedSectionFragment = new BookmarkedSectionFragment();
		
		return v;
	}
	
	public class SectionsPagerAdapter extends FragmentPagerAdapter {

		public SectionsPagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int position) {
			switch(position){
				
				case 0:{
					return myLibrarySectionFragment;
				}case 1:{
					return newBookSectionFragment;
				}case 2:{
					return bookmarkedSectionFragment;
				}default:{
					return myLibrarySectionFragment;
				}
			}
		}

		@Override
		public int getCount() {
			return 3;
		}

		@Override
		public CharSequence getPageTitle(int position) {
			Locale l = Locale.getDefault();
			switch (position) {
			case 0:
				return getString(R.string.title_section1).toUpperCase(l);
			case 1:
				return getString(R.string.title_section2).toUpperCase(l);
			case 2:
				return getString(R.string.title_section3).toUpperCase(l);
			}
			return null;
		}
	}
	
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		
		if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
			isPortrait = false;
			Log.i(TAG, "LANDSCAPE");
			Toast.makeText(getActivity(), "Landscape", Toast.LENGTH_LONG).show();
		} else{
			isPortrait = true;
			Log.i(TAG, "PORTRAIT");
			Toast.makeText(getActivity(), "Portrait", Toast.LENGTH_LONG).show();
		}
	}
	
}
