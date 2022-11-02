package mn.mapps.bookreader.fragment;

import mn.mapps.bookreader.R;
import android.app.Dialog;
import android.app.DialogFragment;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

public class ConfirmDialogFragment extends DialogFragment {
	
	public onSubmitListener mListener;  
	private static String title1;
	private static String label2;
	private static String title2;
	private static String label1;

	public static ConfirmDialogFragment newInstance(String title1, String title2, String label1, String label2){
		ConfirmDialogFragment f = new ConfirmDialogFragment();
        ConfirmDialogFragment.title1 = title1;
        ConfirmDialogFragment.title2 = title2;
        ConfirmDialogFragment.label1 = label1;
		ConfirmDialogFragment.label2 = label2;
		return f;
	}
	
	public interface onSubmitListener{
		void setOnSubmitListener();
	}
	
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		Dialog dialog = new Dialog(getActivity());
		dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
		dialog.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		dialog.setContentView(R.layout.confirm_dialog);
		dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
		dialog.show();
		
		TextView dTitle1 = (TextView) dialog.findViewById(R.id.tv_dTitle_1);
		TextView dTitle2 = (TextView) dialog.findViewById(R.id.tv_dTitle_2);
        TextView dLabel1 = (TextView) dialog.findViewById(R.id.tv_dLabel_1);
        TextView dLabel2 = (TextView) dialog.findViewById(R.id.tv_dLabel_2);
        
        dTitle1.setText(title1);
        dTitle2.setText(title2);
        dLabel1.setText(label1);
        dLabel2.setText(label2);
        
        Button btn_dDownload = (Button)dialog.findViewById(R.id.btn_dDownload);
        btn_dDownload.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				mListener.setOnSubmitListener();
				dismiss();
			}
		});
        
		return dialog;
	}
	
}
