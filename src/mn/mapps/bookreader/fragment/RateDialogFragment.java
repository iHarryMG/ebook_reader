package mn.mapps.bookreader.fragment;

import mn.mapps.bookreader.R;
import mn.mapps.bookreader.fragment.ConfirmDialogFragment.onSubmitListener;
import android.app.Dialog;
import android.app.DialogFragment;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.RatingBar.OnRatingBarChangeListener;
import android.widget.TextView;

public class RateDialogFragment extends DialogFragment {
	
	public onRateSubmitListener mListener;  
	private static float rate;

	public static RateDialogFragment newInstance(float rate){
		RateDialogFragment f = new RateDialogFragment();
		RateDialogFragment.rate = rate;
		return f;
	}
	
	public interface onRateSubmitListener{
		void setOnRateSubmitListener(int rating, String comment);
	}
	
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		Dialog dialog = new Dialog(getActivity());
		dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
		dialog.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		dialog.setContentView(R.layout.rate_submit_dialog);
		dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
		dialog.show();
		
		final TextView ratingTip = (TextView) dialog.findViewById(R.id.tv_ratingTip);
		final EditText rateComment = (EditText) dialog.findViewById(R.id.et_rateComment);
		
        final RatingBar rateBook = (RatingBar) dialog.findViewById(R.id.rb_rateBook_dialog);
        rateBook.setRating(rate);
        ratingTip.setText(getTip(rate));
        
        rateBook.setOnRatingBarChangeListener(new OnRatingBarChangeListener() {
			@Override
			public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
				ratingTip.setText(getTip(rating));
			}
		});
        
        Button btn_dDownload = (Button)dialog.findViewById(R.id.btn_rateSubmit);
        btn_dDownload.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				mListener.setOnRateSubmitListener((int) (rateBook.getRating()), rateComment.getText().toString());
				dismiss();
			}
		});
        
		return dialog;
	}
	
	private String getTip(float rating){
		if(4f < rating){
			return "Маш гоё";
		}else if(3f < rating && rating <= 4f){
			return "Сайн";
		}else if(2f < rating && rating <= 3f){
			return "Боломжийн";
		}else if(1f < rating && rating <= 2f){
			return "Яахавдээ";
		}else{
			return "Маш муу";
		}
	}
	
}
