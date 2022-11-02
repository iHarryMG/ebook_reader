package mn.mapps.bookreader.vo;

public class NavDrawerItem {
	
	private String title;
	private int icon;
	private boolean isCounterVisible = false;
	private String count = "0";

	public NavDrawerItem() {
	}

	public NavDrawerItem(String title, int icon) {
		this.setTitle(title);
		this.setIcon(icon);
	}
	
	public NavDrawerItem(String title, int icon, boolean isCounterVisible, String count) {
		this.setTitle(title);
		this.setIcon(icon);
		this.setCounterVisible(isCounterVisible);
		this.setCount(count);
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public int getIcon() {
		return icon;
	}

	public void setIcon(int icon) {
		this.icon = icon;
	}

	public boolean getCounterVisibility(){
        return this.isCounterVisible;
    }

	public void setCounterVisible(boolean isCounterVisible) {
		this.isCounterVisible = isCounterVisible;
	}

	public String getCount() {
		return count;
	}

	public void setCount(String count) {
		this.count = count;
	}
	
	
}
