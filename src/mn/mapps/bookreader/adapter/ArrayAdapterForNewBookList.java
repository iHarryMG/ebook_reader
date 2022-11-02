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
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class ArrayAdapterForNewBookList extends ArrayAdapter<ArrayList<BookVO>> {

	public static final String TAG = ArrayAdapterForNewBookList.class.getSimpleName();
	private Context context;
	private int layoutResourceId;
	private Util util = new Util();
	private ArrayList<ArrayList<BookVO>> bookArrayList;

	public ArrayAdapterForNewBookList(Context context, int layoutResourceId, ArrayList<ArrayList<BookVO>> bookArrayList){ 
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
		
		ArrayList<BookVO> bookArray = bookArrayList.get(position);
		if(bookArray != null & !bookArray.isEmpty())
			setNewbookSectionItemValues(convertView, bookArray);
		
		return convertView;
	}

	private void setNewbookSectionItemValues(View bookShelfView, ArrayList<BookVO> bookArray) {
		
		LinearLayout newlistRow = (LinearLayout) bookShelfView.findViewById(R.id.ll_nl_row);
		newlistRow.removeAllViews();
		
		for(int i = 0; i < bookArray.size(); i++){
		
			BookVO book = null;
			try{
				book = bookArray.get(i);
				if(book != null){ // Set category name
					RelativeLayout v_categoryTitle = (RelativeLayout)bookShelfView.findViewById(R.id.rl_nb_titleCategory);
					
					if(null != book.getCategory_name()){
						TextView categoryName = (TextView) v_categoryTitle.findViewById(R.id.tv_nb_categoryName);
						categoryName.setText(book.getCategory_name());
						
						ImageView btnMoreList = (ImageView) bookShelfView.findViewById(R.id.iv_nb_btnOther);
						btnMoreList.setOnClickListener(new OnBookItemClickListener(context, TabbedActivity.NewBookMoreList, book.getCategory_id(), book.getCategory_name()));

						if(null != book.getSubText()){
							TextView subText = (TextView) v_categoryTitle.findViewById(R.id.tv_nb_subText);
							subText.setText(book.getSubText());
						}
					}
				}else{
					Log.e(TAG, "BookVO is null");
					return;
				}
					
			}catch(Exception ex){
				ex.printStackTrace();
				return;
			}
			
			LayoutInflater vi = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			View v_book = vi.inflate(R.layout.newbookitem, null);
			
			LinearLayout bookItem = (LinearLayout) v_book.findViewById(R.id.ll_nbi_bookItem);
			bookItem.setOnClickListener(new OnBookItemClickListener(context, book.getBook_id(), TabbedActivity.NewBookSection));

			int heightBookCover = bookItem.getBackground().getIntrinsicHeight() + bookItem.getPaddingTop() + bookItem.getPaddingBottom();
			int widthBookCover = (int) (heightBookCover / 1.4);
			
			ImageView bookCover = (ImageView) v_book.findViewById(R.id.iv_nbi_bookCover);
			
			if(ServerApiListContainer.isDummyDataTest)
				bookCover.setImageResource(R.drawable.cover6); // API URL image-r zasah shaardlagatai
			else
				util.displayImageEbook(book.getBook_id(), bookCover, Integer.toString(widthBookCover));
			
			TextView bookName = (TextView) v_book.findViewById(R.id.nbi_tv_bookName);
			TextView bookAuthor = (TextView) v_book.findViewById(R.id.tv_nbi_authorName);
			TextView price = (TextView) v_book.findViewById(R.id.tv_nbi_price);
			
			int txtLengthLimit = 0;
			
			if(bookArray.size() < 3)
				txtLengthLimit = 22;
			else if(bookArray.size() >= 3)
				txtLengthLimit =15;
			
			String book_name = book.getBook_name();
			if(txtLengthLimit <= book_name.length()) 
				book_name = book_name.substring(0, txtLengthLimit-2) + "..";
			bookName.setText(book_name);
			String author_name = book.getAuthor_name();
			if(txtLengthLimit <= author_name.length())
				author_name = author_name.substring(0, txtLengthLimit-2) + "..";
			bookAuthor.setText(author_name);
			
			if(0 < book.getPrice())
				price.setText("Үнэ: "+book.getPrice()+"₮");
			else
				price.setText("Үнэгүй");
			
			int deviceScreenWidth = util.getScreenWidth((Activity) context);
			int deviceScreenHeight = util.getScreenHeight((Activity) context);
			int layoutHeight = deviceScreenHeight / bookArray.size();
			
			newlistRow.setClickable(false);
			newlistRow.addView(v_book, new LinearLayout.LayoutParams(0, layoutHeight, 1F));
		}
		
	}

}
