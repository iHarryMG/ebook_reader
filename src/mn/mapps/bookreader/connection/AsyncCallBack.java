package mn.mapps.bookreader.connection;

import java.util.ArrayList;

import mn.mapps.bookreader.vo.BookVO;


public interface AsyncCallBack<T> {
	
	public void onSuccess(String requestResult);
	
	public void onFailure(Throwable th);

}

