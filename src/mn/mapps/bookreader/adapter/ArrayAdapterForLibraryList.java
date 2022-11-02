package mn.mapps.bookreader.adapter;

import java.util.ArrayList;
import java.util.List;

import mn.mapps.bookreader.R.drawable;
import mn.mapps.bookreader.R.id;
import mn.mapps.bookreader.TabbedActivity;
import mn.mapps.bookreader.connection.ServerApiListContainer;
import mn.mapps.bookreader.listener.OnBookItemClickListener;
import mn.mapps.bookreader.util.Util;
import mn.mapps.bookreader.vo.BookVO;
import mn.mapps.bookreader.R;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.opengl.Visibility;
import android.print.PrintDocumentAdapter.LayoutResultCallback;
import android.support.v4.app.Fragment;
import android.text.Layout;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class ArrayAdapterForLibraryList extends ArrayAdapter<ArrayList<BookVO>> {

	public static final String TAG = ArrayAdapterForLibraryList.class.getSimpleName();
	private Context context;
	private int layoutResourceId;
	private ArrayList<ArrayList<BookVO>> bookArrayList;

	public ArrayAdapterForLibraryList(Context context, int layoutResourceId, ArrayList<ArrayList<BookVO>> bookArrayList){ 
		super(context, layoutResourceId, bookArrayList);
		this.context = context;
		this.layoutResourceId = layoutResourceId;
		this.bookArrayList = bookArrayList;
	}
	
	@Override
	public void add(ArrayList<BookVO> bookArray) {
		super.add(bookArray);
		notifyDataSetChanged();
	}
	
	@Override
	public int getCount() {
		return bookArrayList.size();
	}

	@Override
	public ArrayList<BookVO> getItem(int position) {
		return bookArrayList.get(position);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if(convertView == null){
			LayoutInflater inflater = ((Activity)context).getLayoutInflater();
			convertView = inflater.inflate(layoutResourceId, parent, false);
		}
		
		ArrayList<BookVO> book = bookArrayList.get(position);		
		
		setMyLibrarySectionItemValues(convertView, book);
		
		return convertView;
	}

	private void setMyLibrarySectionItemValues(View bookShelfView, ArrayList<BookVO> bookArray) {
		
		LinearLayout shelfRow = (LinearLayout) bookShelfView.findViewById(R.id.ll_pv_bookshelfItem);
		shelfRow.removeAllViews();
			
		for(int i = 0; i < bookArray.size(); i++){
		
			BookVO book = bookArray.get(i);
			LayoutInflater vi = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			View v_book = vi.inflate(R.layout.book, null);
			
			ImageView bookCover = (ImageView) v_book.findViewById(R.id.iv_bv_bookCover);
			ImageView tagNew = (ImageView) v_book.findViewById(R.id.iv_bv_bookTag);
			
//			if(ServerApiListContainer.isDummyDataTest)
//				bookCover.setImageResource(R.drawable.cover1);
//			else
				bookCover.setImageBitmap(book.getCover_pic());
			bookCover.setOnClickListener(new OnBookItemClickListener(context, book.getBook_id(), book.getBook_name(), TabbedActivity.MyLibrarySection));
			
			if(book.isNew() == true)
				tagNew.setVisibility(View.VISIBLE);
			else
				tagNew.setVisibility(View.INVISIBLE);

			LinearLayout.LayoutParams param = null;
			
//			if(bookArray.size() == 2 && i == (bookArray.size()-1))
//				param = new LinearLayout.LayoutParams(0,LinearLayout.LayoutParams.MATCH_PARENT, 2F);
//			else
				param = new LinearLayout.LayoutParams(0,LinearLayout.LayoutParams.MATCH_PARENT, 1F);
			
			shelfRow.setClickable(false);
			shelfRow.addView(v_book, param);
		}
		
	}

}
