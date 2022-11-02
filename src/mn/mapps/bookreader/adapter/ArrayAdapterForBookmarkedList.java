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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class ArrayAdapterForBookmarkedList extends ArrayAdapter<BookVO> {

	private String TAG = ArrayAdapterForBookmarkedList.class.getSimpleName();
	private Context context;
	private int layoutResourceId;
	private Util util = new Util();
	private ArrayList<BookVO> bookList;

	public ArrayAdapterForBookmarkedList(Context context, int layoutResourceId, ArrayList<BookVO> bookList){ 
		super(context, layoutResourceId, bookList);
		this.context = context;
		this.layoutResourceId = layoutResourceId;
		this.bookList = bookList;
	}
	
	@Override
	public void add(BookVO book) {
		super.add(book);
		notifyDataSetChanged();
	}
	
	@Override
	public int getCount() {
		return bookList.size();
	}

	@Override
	public BookVO getItem(int position) {
		return bookList.get(position);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if(convertView == null){
			LayoutInflater inflater = ((Activity)context).getLayoutInflater();
			convertView = inflater.inflate(layoutResourceId, parent, false);
		}
		
		BookVO book = bookList.get(position);
		setBookmarkedSectionItemValues(convertView, book);
		
		return convertView;
	}
	
	private void setBookmarkedSectionItemValues(View convertView, BookVO book) {
		TextView bookName = (TextView)convertView.findViewById(R.id.tv_bookName);
		TextView bookAuthor = (TextView)convertView.findViewById(R.id.tv_date);
		TextView bookPrice = (TextView)convertView.findViewById(R.id.tv_fileSize);
		ImageView bookCover = (ImageView)convertView.findViewById(R.id.iv_fileIcon);
		ImageView bookMark = (ImageView)convertView.findViewById(R.id.iv_bookmarkItem);
		RatingBar bookRating = (RatingBar)convertView.findViewById(R.id.rb_bookRatingBar);
		
		String book_name = book.getBook_name();
		if(22 <= book_name.length()) 
			book_name = book_name.substring(0, 20) + "..";
		bookName.setText(book_name);
		String author_name = book.getAuthor_name();
		if(32 <= author_name.length())
			author_name = author_name.substring(0, 30) + "..";
		bookAuthor.setText("Зохиогч: "+author_name);
		
		if(0 < book.getPrice())
			bookPrice.setText("Үнэ: "+book.getPrice()+"₮");
		else
			bookPrice.setText("Үнэгүй");
		
		LinearLayout bookItem = (LinearLayout)convertView.findViewById(R.id.ll_bookItem);
		bookItem.setOnClickListener(new OnBookItemClickListener(context, book.getBook_id(), TabbedActivity.BookmarkedSection));
		
		int heightBookCover = bookItem.getBackground().getIntrinsicHeight() + bookItem.getPaddingTop() + bookItem.getPaddingBottom();
		int widthBookCover = (int) (heightBookCover / 1.4);
		
		Log.d(TAG, "widthBookCover: " + widthBookCover);
		
		if(ServerApiListContainer.isDummyDataTest){ // API URL image-r zasah shaardlagatai
			Bitmap imgBookCover = util.editImageSize(R.drawable.cover3, widthBookCover, heightBookCover, getContext().getResources());
			bookCover.setImageBitmap(imgBookCover); 
		}else
			util.displayImageEbook(book.getBook_id(), bookCover, Integer.toString(widthBookCover));
		
		if(book.isBookmarked() == true)
			bookMark.setVisibility(View.VISIBLE);
		else
			bookMark.setVisibility(View.INVISIBLE);
		
	}

}
