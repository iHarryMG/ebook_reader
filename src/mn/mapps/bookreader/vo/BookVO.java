package mn.mapps.bookreader.vo;

import org.json.simple.JSONArray;

import android.graphics.Bitmap;

public class BookVO {

	private String book_id = "";
	private String book_name = "";
	private String author_name = "";
	private Bitmap cover_pic;
	private double price = 0;
	private float rating = 0;
	private int rating_count = 0;
	private boolean is_new = false;
	private boolean is_marked = false;
	private String description = "";
	private String category_id = "";
	private String category_name = "";
	private String subText = "";
	private String publisher = "";
	private String published_year = "";
	private String added_to_mapps_date = "";
	private String genre = "";
	private String language = "";
	private String path = "";
	private String fileSize = "";
	private String lastModifiedDate = "";
	private JSONArray rating_all = null;
	
	public BookVO() {
	}
	
	public BookVO(String book_id, String book_name, String author_name, Bitmap cover_pic, double price, float rating, int rating_count, String description) {
		this.setBook_id(book_id);
		this.setBook_name(book_name);
		this.setAuthor_name(author_name);
		this.setCover_pic(cover_pic);
		this.setPrice(price);
		this.setRating(rating);
		this.setRating_count(rating_count);
		this.setDescription(description);
	}

	public String getBook_id() {
		return book_id;
	}

	public void setBook_id(String book_id) {
		this.book_id = book_id;
	}

	public String getBook_name() {
		return book_name;
	}

	public void setBook_name(String book_name) {
		this.book_name = book_name;
	}

	public String getAuthor_name() {
		return author_name;
	}

	public void setAuthor_name(String author_name) {
		this.author_name = author_name;
	}

	public Bitmap getCover_pic() {
		return cover_pic;
	}

	public void setCover_pic(Bitmap cover_pic) {
		this.cover_pic = cover_pic;
	}

	public double getPrice() {
		return price;
	}

	public void setPrice(double price) {
		this.price = price;
	}

	public float getRating() {
		return rating;
	}

	public void setRating(float rating) {
		this.rating = rating;
	}

	public int getRating_count() {
		return rating_count;
	}

	public void setRating_count(int rating_count) {
		this.rating_count = rating_count;
	}

	public boolean isNew() {
		return is_new;
	}

	public void setNew(boolean isNew) {
		this.is_new = isNew;
	}

	public boolean isBookmarked() {
		return is_marked;
	}

	public void setBookmarked(boolean isBookmarked) {
		this.is_marked = isBookmarked;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getCategory_name() {
		return category_name;
	}

	public void setCategory_name(String category_name) {
		this.category_name = category_name;
	}

	public String getSubText() {
		return subText;
	}

	public void setSubText(String subText) {
		this.subText = subText;
	}

	public String getPublished_year() {
		return published_year;
	}

	public void setPublished_year(String published_year) {
		this.published_year = published_year;
	}

	public String getPublisher() {
		return publisher;
	}

	public void setPublisher(String publisher) {
		this.publisher = publisher;
	}

	public String getAdded_to_mapps_date() {
		return added_to_mapps_date;
	}

	public void setAdded_to_mapps_date(String added_to_mapps_date) {
		this.added_to_mapps_date = added_to_mapps_date;
	}

	public String getGenre() {
		return genre;
	}

	public void setGenre(String genre) {
		this.genre = genre;
	}

	public String getLanguage() {
		return language;
	}

	public void setLanguage(String language) {
		this.language = language;
	}

	public JSONArray getRating_all() {
		return rating_all;
	}

	public void setRating_all(JSONArray rating_all) {
		this.rating_all = rating_all;
	}

	public String getCategory_id() {
		return category_id;
	}

	public void setCategory_id(String category_id) {
		this.category_id = category_id;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public String getFileSize() {
		return fileSize;
	}

	public void setFileSize(String fileSize) {
		this.fileSize = fileSize;
	}

	public String getLastModifiedDate() {
		return lastModifiedDate;
	}

	public void setLastModifiedDate(String lastModifiedDate) {
		this.lastModifiedDate = lastModifiedDate;
	}
	
	
}
