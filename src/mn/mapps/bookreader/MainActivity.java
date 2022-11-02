package mn.mapps.bookreader;

import java.text.Format;
import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.OptionalPendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;

import mn.mapps.bookreader.R;
import mn.mapps.bookreader.SignInActivity.onLoginListener;
import mn.mapps.bookreader.adapter.ArrayAdapterForBookmarkedList;
import mn.mapps.bookreader.adapter.NavDrawerListAdapter;
import mn.mapps.bookreader.connection.AsyncCallBack;
import mn.mapps.bookreader.connection.ServerApiListContainer;
import mn.mapps.bookreader.controller.ActionController;
import mn.mapps.bookreader.fragment.ConfirmDialogFragment;
import mn.mapps.bookreader.fragment.ConfirmDialogFragment.onSubmitListener;
import mn.mapps.bookreader.util.Util;
import mn.mapps.bookreader.vo.NavDrawerItem;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.graphics.Paint;
import android.graphics.Paint.FontMetrics;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.NotificationCompat;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SearchView;
import android.widget.Toast;
import android.widget.SearchView.OnQueryTextListener;

public class MainActivity extends FragmentActivity implements onSubmitListener,
	OnQueryTextListener, GoogleApiClient.OnConnectionFailedListener, ConnectionCallbacks, onLoginListener {
	
	private static final String TAG = MainActivity.class.getSimpleName();
	
	public static Context context;
	private DrawerLayout mDrawerLayout;
	private ListView mDrawerList;
	private ActionBarDrawerToggle mDrawerToggle;
	private CharSequence mDrawerTitle;
	private CharSequence mTitle;
	private String[] navMenuTitles;
	private TypedArray navMenuIcons;
	private ArrayList<NavDrawerItem> navDrawerItems;
	private static ActionController actionController;
	private ArrayAdapterForBookmarkedList adapterItem;
	private static Util util;
	private static NotificationManager notificationManager;
	private static NotificationCompat.Builder builder;
	
	private static final int RC_SIGN_IN = 9001;
	private static final int CHECK_USER_INFO = 9002;
	private static final int REVOKE_ACCESS_GOOGLE_SIGNIN = 9999;
	
    private static GoogleApiClient mGoogleApiClient;
    private ProgressDialog mProgressDialog;

	
    public static final Handler handler = new Handler() {

		public void handleMessage(Message msg) {

			if (msg.what == CHECK_USER_INFO){
				getUserInfo();
			}else if (msg.what == REVOKE_ACCESS_GOOGLE_SIGNIN){
				revokeAccess();
			}
		}
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		// Enable ActionBar app icon to behave as action to toggle nav drawer
		getActionBar().setDisplayShowTitleEnabled(false);
		getActionBar().setDisplayHomeAsUpEnabled(true);
		getActionBar().setHomeButtonEnabled(true);
		
		context = this;
		actionController = new ActionController(this);
		util = new Util();
		mTitle = mDrawerTitle = getTitle();
		
//		util.showProgressDialog(this);
		
		// load slide menu items
		navMenuTitles = getResources().getStringArray(R.array.nav_drawer_titles);
		navMenuIcons = getResources().obtainTypedArray(R.array.nav_drawer_icons);
		mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		mDrawerList = (ListView) findViewById(R.id.left_drawer);
		
		navDrawerItems = new ArrayList<NavDrawerItem>();
		// adding nav drawer items to array
        // Миний номын сан
        navDrawerItems.add(new NavDrawerItem(navMenuTitles[0], navMenuIcons.getResourceId(0, -1), true, "9"));
        // Худалдаж авсан ном
        navDrawerItems.add(new NavDrawerItem(navMenuTitles[1], navMenuIcons.getResourceId(1, -1)));
        // Утасны файл
        navDrawerItems.add(new NavDrawerItem(navMenuTitles[2], navMenuIcons.getResourceId(2, -1)));
        // SD картны файл
        navDrawerItems.add(new NavDrawerItem(navMenuTitles[3], navMenuIcons.getResourceId(3, -1)));

        // Recycle the typed array
        navMenuIcons.recycle();
		
		// set a custom shadow that overlays the main content when the drawer ons
//		mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow,  GravityCompat.START);
		
		// Add items to the ListView
		NavDrawerListAdapter adapter = new NavDrawerListAdapter(getApplicationContext(), navDrawerItems);
		mDrawerList.setAdapter(adapter);
		// Set the OnItemClickListener so something happens when a 
		// user clicks on an item.
		mDrawerList.setOnItemClickListener(new DrawerItemClickListener());
		
		mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, 
				R.drawable.ic_drawer, 
				R.string.drawer_open, 
				R.string.drawer_close
				) {
			
			public void onDrawerClosed(View view) {
//				getActionBar().setTitle(mTitle);
				invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu
			}
			
			public void onDrawerOpened(View drawerView) {
//				getActionBar().setTitle(mDrawerTitle);
				invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu
			}
		};
		
		mDrawerLayout.setDrawerListener(mDrawerToggle);
		
		RelativeLayout btnLogout = (RelativeLayout) findViewById(R.id.rl_btnLogout);
		btnLogout.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				signOut();
			}
		});
		
        if(ServerApiListContainer.isDummyDataTest){
        	util.cacheData(getApplicationContext(), "user_email", "skytelmapps@gmail.com");
            util.cacheData(getApplicationContext(), "token", "123456789");
            util.cacheData(getApplicationContext(), "is_signed_in", "true");
            navigateTo(0);
        }else{
        	checkVersionUpdate();
        }
        
        String is_signed_in = util.getCacheData(context, "is_signed_in", "false");
        
        if("false".equals(is_signed_in)){
        	Log.d(TAG, "[@ onCreate] user logged out!");
        	navigateTo(999);
        }else{
//        	[START configure_signin]
	        // Configure sign-in to request the user's ID, email address, and basic
	        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
	        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
	                .requestEmail()
	                .build();
	        // [END configure_signin]

	        // [START build_client]
	        // Build a GoogleApiClient with access to the Google Sign-In API and the
	        // options specified by gso.
	        mGoogleApiClient = new GoogleApiClient.Builder(this)
	                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
	                .addConnectionCallbacks(this)
	                .addOnConnectionFailedListener(this)
	                .build();
	        // [END build_client]
        	        
	 		if(savedInstanceState == null) {
	 			Log.d(TAG, "[@ onCreate] Got cache logged in!");
	 			mGoogleApiClient.connect();
	 		}else{
	 			Log.d(TAG, "[@ onCreate] savedInstanceState.size: " + savedInstanceState.size());
	 		}
        }
        
	}

	private void checkVersionUpdate() {
		
		boolean[] networkState = util.getNetworkState(context);
    	if(networkState[0] == false && networkState[1] == false){
    		Toast.makeText(getApplicationContext(), "Интернэтэд холбогдоогүй байна!", Toast.LENGTH_SHORT).show();
    	}else{
    		if(networkState[0] == false && networkState[1] == true){
    			
    			// TODO: Confirm dialog гаргах
    			Toast.makeText(getApplicationContext(), "3G интернэт холбогдсон байна!", Toast.LENGTH_SHORT).show();
    		}else{
				actionController.getVersion(new AsyncCallBack<String>(){

					@Override
					public void onSuccess(final String result) {
						
						if(result != null && !result.isEmpty()){
							
							Log.d(TAG, "[@ getVersion] request result: " + result);
							try {
								PackageInfo pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
    		        			int appVersion = pInfo.versionCode;
    		        			int res = Integer.parseInt(result);
    		        			
								if(res != 0 && appVersion < res){
									
//									TODO: Шинэ хувилбар татах confirm dialog гаргаж APK-г татах
									showConfirmDialog("dialog", "Шинэ хувилбар", "гарсан байна.\nШинэчлэгдсэн хувилбарыг\nтатан авч ашиглана уу!", "Баталгаажуулах: ", "");
    		        			}
		        			
    						} catch (NameNotFoundException e) {
    							e.printStackTrace();
    						}
						}else{
							Log.d(TAG, "[@ getVersion] request result: failed");
						}
						
						util.hideProgressDialog();
					}
					
					private void showConfirmDialog(String dialogTag, String title1, String title2, String label1, String label2) {
						FragmentTransaction ft = getFragmentManager().beginTransaction();
						Fragment prev = getFragmentManager().findFragmentByTag(dialogTag);
						if (prev != null) {
						    ft.remove(prev);
						}
						ft.addToBackStack(null);
							
						ConfirmDialogFragment newFragment = ConfirmDialogFragment.newInstance(title1, title2, label1, label2);
						newFragment.mListener = MainActivity.this;
						newFragment.show(ft, dialogTag);
					}

					@Override
					public void onFailure(Throwable th) {
						Log.e(TAG, "Error on isDownloadFree..");
						util.hideProgressDialog();
						th.printStackTrace();
					}
				});
    				
    		}
    	}
	}
	
	@Override
	public void setOnSubmitListener() {
		notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
		builder = new NotificationCompat.Builder(context);
		builder.setContentTitle("Шинэ хувилбар").setContentText("Файлыг татаж эхэллээ..").setSmallIcon(R.drawable.downloading);
		notificationManager.notify(0, builder.build());
		
		String userEmail = util.getCacheData(context, "user_email", "");
		String token = util.getCacheData(context, "token", "");
		
		Toast.makeText(getApplicationContext(), "Апп-н шинэ хувилбарыг татаж байна.. тест", Toast.LENGTH_SHORT).show();
		
//		actionController.downloadSample(userEmail, book.getBook_id(), token, 
//				util.getDgn(getApplicationContext()), book.getBook_name());
	}
	
	/*
	 * If you do not have any menus, you still need this function
	 * in order to open or close the NavigationDrawer when the user 
	 * clicking the ActionBar app icon.
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if(mDrawerToggle.onOptionsItemSelected(item)) {
			return true;
		}
		// Handle action bar actions click
        switch (item.getItemId()) {
	        case R.id.action_settings:
	            return true;
	        default:
	            return super.onOptionsItemSelected(item);
        }
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.main, menu);
		SearchView searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
		searchView.setOnQueryTextListener(this);
		return super.onCreateOptionsMenu(menu);
	}
	
	@Override
	public boolean onQueryTextSubmit(String query) {
		Log.d(TAG, "[@ onQueryTextSubmit] Searching for book : [ "+query+" ]");
		
		util.showProgressDialog(context);

		String userEmail = util.getCacheData(getApplication(), "user_email", "");
		actionController.search(userEmail, 0, 10, query, new AsyncCallBack<String>(){

			@Override
			public void onSuccess(final String result) {
				
				if(result != null){
					Log.d(TAG, "[@ search onSuccess] Search successully completed!");
					util.hideProgressDialog();
					
					Intent intent = new Intent(getApplicationContext(), MoreBookListActivity.class);
					intent.putExtra("More_Book_List_Result", result);
					intent.putExtra("Search_Result_Items", "10");
					startActivity(intent);
				}
			}

			@Override
			public void onFailure(Throwable th) {
				Log.e(TAG, "Error on isDownloadFree..");
				util.hideProgressDialog();
				th.printStackTrace();
			}
		});
		
		return false;
	}

	@Override
	public boolean onQueryTextChange(String newText) {
//		Log.i(TAG, "[0] onQueryTextChange: [ "+newText+" ]");
		return false;
	}
	
	/*
	 * When using the ActionBarDrawerToggle, you must call it during onPostCreate()
	 * and onConfigurationChanged()
	 */
	
	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		mDrawerToggle.syncState();
	}
	
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		mDrawerToggle.onConfigurationChanged(newConfig);
	}
	
	private class DrawerItemClickListener implements OnItemClickListener {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			Log.i(TAG, "ponies");
			mDrawerLayout.closeDrawers();
			navigateTo(position);
		}
	}
	
//	Navigation Menu-nii jagsaaltin tohirgoo
	public static void navigateTo(int position) {
		Intent intent = null;
		
		switch(position) {
			case 0: // Library
				((Activity) context).getActionBar().show();
				((FragmentActivity) context).getSupportFragmentManager()
					.beginTransaction()
					.replace(R.id.content_frame, TabbedActivity.newInstance(), TabbedActivity.TAG).commit();
				break;
			case 1: // Purchased books
				intent = new Intent(context, PurchasedBookActivity.class);
			    context.startActivity(intent);
				break;
			case 2: // Books on internal storage  
				intent = new Intent(context, InternalStorageBookActivity.class);
			    context.startActivity(intent);
				break;
			case 3: // Books on external storage
				intent = new Intent(context, ExternalStorageBookActivity.class);
			    context.startActivity(intent);
				break;
			case 999: // Login activity
				((Activity) context).getActionBar().hide();
				((FragmentActivity) context).getSupportFragmentManager()
					.beginTransaction()
					.replace(R.id.content_frame, SignInActivity.newInstance(context), SignInActivity.TAG).commit();
				break;
		}
	}

    @Override
    public void onStart() {
        super.onStart();

        OptionalPendingResult<GoogleSignInResult> opr = Auth.GoogleSignInApi.silentSignIn(mGoogleApiClient);
        if (opr.isDone()) {
            // If the user's cached credentials are valid, the OptionalPendingResult will be "done"
            // and the GoogleSignInResult will be available instantly.
            Log.d(TAG, "Got cached sign-in");
            GoogleSignInResult result = opr.get();
            handleSignInResult(result);
        } else {
            // If the user has not previously signed in on this device or the sign-in has expired,
            // this asynchronous branch will attempt to sign in the user silently.  Cross-device
            // single sign-on will occur in this branch.
//            showProgressDialog();
            opr.setResultCallback(new ResultCallback<GoogleSignInResult>() {
                @Override
                public void onResult(GoogleSignInResult googleSignInResult) {
//                    hideProgressDialog();
                    handleSignInResult(googleSignInResult);
                }
            });
        }
    }
    
    @Override
	public void onStop() {
		super.onStop();
		if (mGoogleApiClient.isConnected()) {
	      mGoogleApiClient.disconnect();
	    }
	}

    // [START onActivityResult]
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);
        }
    }

    private void handleSignInResult(GoogleSignInResult result) {
        Log.d(TAG, "handleSignInResult:" + result.isSuccess());
        if (result.isSuccess()) {

        	GoogleSignInAccount acct = result.getSignInAccount();
            Log.d(TAG, "Logged in " + acct.getEmail());
            util.cacheData(getApplicationContext(), "user_email", acct.getEmail());
            util.cacheData(getApplicationContext(), "token", acct.getIdToken());
            
            if(ServerApiListContainer.isDummyDataTest){
            	navigateTo(0);
            }else{
            	handler.sendEmptyMessage(CHECK_USER_INFO);
            }
            
            if (!mGoogleApiClient.isConnecting()) {
  		      mGoogleApiClient.connect();
  		    }
        } else {
        	navigateTo(999);
        }
    }

    private void signOut() {
        Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(Status status) {
                        // [START_EXCLUDE]
            			util.removeCacheData(getApplicationContext(), "is_signed_in");
            			util.removeCacheData(getApplicationContext(), "user_email");
            			util.removeCacheData(getApplicationContext(), "user_name");
            			util.removeCacheData(getApplicationContext(), "token");
            			Log.d(TAG, "User logged out!");
            			mGoogleApiClient.disconnect();
            			
            			mDrawerLayout.closeDrawers();
            			navigateTo(999);
                        // [END_EXCLUDE]
                    }
                });
    }
    // [END signOut]

    // [START revokeAccess]
    private static void revokeAccess() {
        Auth.GoogleSignInApi.revokeAccess(mGoogleApiClient).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(Status status) {
                        // [START_EXCLUDE]
                    	util.removeCacheData(context, "is_signed_in");
            			util.removeCacheData(context, "user_email");
            			util.removeCacheData(context, "user_name");
            			util.removeCacheData(context, "token");
            			Log.d(TAG, "User access revoked!");
            			mGoogleApiClient.disconnect();
            			navigateTo(999);
                        // [END_EXCLUDE]
                    }
                });
    }
    // [END revokeAccess]

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.d(TAG, "onConnectionFailed:" + connectionResult);
        setOnLoginListener();
    }
    
    @Override
	public void onConnected(Bundle arg0) {
    	Log.d(TAG, "mGoogleApiClient is connected");
    	mGoogleApiClient.connect();
	}

	@Override
	public void onConnectionSuspended(int arg0) {
		mGoogleApiClient.connect();
	}

	// [START signIn]
	@Override
	public void setOnLoginListener() {
		Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
	}
	// [END signIn]
	

	private static void getUserInfo() {
		String userEmail = util.getCacheData(context, "user_email", "");
		actionController.getUserInfo(userEmail, new AsyncCallBack<String>(){

			@Override
			public void onSuccess(final String result) {
				Log.d(TAG, "getUserInfo Succeeded: \n\n" + result);
				
				JSONObject values;
				try {
					values = new JSONObject(result);
					int error_code = values.getInt("error_code");
					
					if(1001 == error_code){
						handler.sendEmptyMessage(REVOKE_ACCESS_GOOGLE_SIGNIN);
						util.removeCacheData(context, "user_name");
						util.removeCacheData(context, "token");
						Toast.makeText(context, "Хэрэглэгчийн мэдээллийг шалгахад алдаа гарлаа!", Toast.LENGTH_SHORT).show();
						navigateTo(999);
						return;
					}else if(1003 == error_code){
						handler.sendEmptyMessage(REVOKE_ACCESS_GOOGLE_SIGNIN);
						util.removeCacheData(context, "user_name");
						util.removeCacheData(context, "token");
						Toast.makeText(context, "Та MAPPS-д бүртгэлгүй байгаа тул уг апп-г ашиглах боломжгүй байна!", Toast.LENGTH_SHORT).show();
						navigateTo(999);
						return;
					}
					
				} catch (JSONException e) {
				}
					
				try {
					values = new JSONObject(result);
					String user_name = values.getString("user_name");
					util.cacheData(context, "user_name", user_name);
					util.cacheData(context, "is_signed_in", "true");
					Log.d(TAG, "[@ getUserInfo] registered user on mapps.");
					navigateTo(0);
				} catch (JSONException e) {
					e.printStackTrace();
				}
				
			}

			@Override
			public void onFailure(Throwable th) {
				Log.e(TAG, "Error on Rating book");
				th.printStackTrace();
			}
		});
	}

}
