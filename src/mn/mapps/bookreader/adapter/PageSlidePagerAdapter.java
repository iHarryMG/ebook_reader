package mn.mapps.bookreader.adapter;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

import nl.siegmann.epublib.domain.Book;
import nl.siegmann.epublib.domain.Resource;
import nl.siegmann.epublib.domain.Spine;
import nl.siegmann.epublib.domain.SpineReference;
import mn.mapps.bookreader.fragment.PageSlidePageFragment;
import mn.mapps.bookreader.util.Util;
import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Environment;
import android.support.v13.app.FragmentStatePagerAdapter;

public class PageSlidePagerAdapter extends FragmentStatePagerAdapter {

	private static final String TAG = PageSlidePagerAdapter.class.getSimpleName();
	private Util util = new Util();
	private int NUM_PAGES;
	private Spine spine;
	private String bookFolderName;
	private Book book;

	public PageSlidePagerAdapter(FragmentManager fm, Book bookEpub, String bookFolderName) {
		super(fm);
		this.book = bookEpub;
		this.spine = bookEpub.getSpine();
		this.bookFolderName = bookFolderName;
	    List<SpineReference> spineList = spine.getSpineReferences();
	    this.NUM_PAGES = spineList.size();
	    
	}

	@Override
	public Fragment getItem(int position) {
		
		String pageContent = "";
		StringBuilder string =  new StringBuilder();
		
		try{
			Resource res = spine.getResource(position);
			InputStream is = res.getInputStream();
			BufferedReader reader = new BufferedReader(new InputStreamReader(is));
			
			String line = "";
			while((line = reader.readLine()) != null){
				pageContent = string.append(line + "\n").toString();
				if(line.contains("<head>")){
					pageContent = string.append("<meta name=\"viewport\" content=\"width=device-width, initial-scale=1\">\n").toString();
				}
			}
			
			pageContent = pageContent.replace("../", "");
			pageContent = pageContent.replace("src=\"IMAGES/", "src=\"thmb/");
			pageContent = pageContent.replace("src=\"IMAGE/", "src=\"thmb/");
			pageContent = pageContent.replace("src=\"Images/", "src=\"thmb/");
			pageContent = pageContent.replace("src=\"Image/", "src=\"thmb/");
			pageContent = pageContent.replace("src=\"images/", "src=\"thmb/");
			pageContent = pageContent.replace("src=\"image/", "src=\"thmb/");
			pageContent = pageContent.replace("src=\"imgs/", "src=\"thmb/");
			pageContent = pageContent.replace("src=\"img/", "src=\"thmb/");
			
			String cssFolder = util.getRootDirName() +"/"+ bookFolderName +"/" + util.getEpub_StyleDirName();
			String epubCssName = "";
			
			File dir = new File(Environment.getExternalStorageDirectory() + "/" + cssFolder);
			File[] files = dir.listFiles();
			for (File file : files) {
			    if (!file.isDirectory()) {
			    	if(file.getName().contains(".css")){
			    		System.out.println("Directory Name==>:" + file.getName());
			    		epubCssName = file.getName();
			    	}
			    }
			}
			
			pageContent = pageContent.replaceAll("href=\""+epubCssName+"\"", "href=\"stl/"+epubCssName+"\"");
			
//			Document doc = Jsoup.parse(pageContent);
//			Elements bodyElement = doc.getElementsByTag("body");
//			Element element = bodyElement.get(0);
//			List<Node> childNodes = element.childNodes();
//			Log.d(TAG, "childNodeSize: " + childNodes.size());
			
			
		} catch (IOException e) {  
			e.printStackTrace();
		}
		
		return PageSlidePageFragment.create(position, pageContent, bookFolderName);
	}

	@Override
	public int getCount() {
		return NUM_PAGES;
	}

}
