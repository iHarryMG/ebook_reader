package mn.mapps.bookreader;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


public class SignInActivity extends Fragment implements View.OnClickListener {

    public static final String TAG = SignInActivity.class.getSimpleName();
	private onLoginListener mListener;
    
    public SignInActivity(Context context) {
		mListener = (onLoginListener) context;
    }
    
    public static SignInActivity newInstance(Context context) {
		return new SignInActivity(context);
	}
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.activity_login, container, false);
        v.findViewById(R.id.btnGLogin).setOnClickListener(this);
        return v;
    }
    
    public interface onLoginListener{
		void setOnLoginListener();
	}
    
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnGLogin:
            	mListener.setOnLoginListener();
                break;
        }
    }

}
