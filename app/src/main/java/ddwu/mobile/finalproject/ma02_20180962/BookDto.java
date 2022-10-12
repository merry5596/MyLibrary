package ddwu.mobile.finalproject.ma02_20180962;

import android.text.Html;
import android.text.Spanned;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class BookDto implements Serializable {

    private long _id;
    private String title;
    private String author;
    private String publisher;
    private String pubdate;
    private int price;
    private String isbn;
    private String description;
    private String imageLink;
    private String imageFileName;       // 외부저장소에 저장했을 때의 파일명
    private int state;
    private int page;
    private List<String> passages;
    private float rate;
    private String review;

    public BookDto() {
        state = 0;
        page = 0;
        passages = new ArrayList<>();
        passages.add("");
        passages.add("");
        passages.add("");
        rate = 0;
        review = "";
    }

    public BookDto(long _id, String title, String author, String publisher, String pubdate, int price, String isbn, String description, String imageLink, String imageFileName, int state, int page, List<String> passage, float rate, String review) {
        this._id = _id;
        this.title = title;
        this.author = author;
        this.publisher = publisher;
        this.pubdate = pubdate;
        this.price = price;
        this.isbn = isbn;
        this.description = description;
        this.imageLink = imageLink;
        this.imageFileName = imageFileName;
        this.state = state;
        this.page = page;
        this.passages = passage;
        this.rate = rate;
        this.review = review;
    }

    public long get_id() {
        return _id;
    }

    public void set_id(long _id) {
        this._id = _id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    public String getPubdate() {
        return pubdate;
    }

    public void setPubdate(String pubdate) {
        this.pubdate = pubdate;
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImageLink() {
        return imageLink;
    }

    public void setImageLink(String imageLink) {
        this.imageLink = imageLink;
    }

    public String getImageFileName() {
        return imageFileName;
    }

    public void setImageFileName(String imageFileName) {
        this.imageFileName = imageFileName;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public List<String> getPassages() {
        return passages;
    }

    public void setPassages(List<String> passages) {
        this.passages = passages;
    }

    public float getRate() {
        return rate;
    }

    public void setRate(float rate) {
        this.rate = rate;
    }

    public String getReview() {
        return review;
    }

    public void setReview(String review) {
        this.review = review;
    }


    @Override
    public String toString() {
        return  _id + ": " + title + " (" + author + ')';
    }

}
