package ddwu.mobile.finalproject.ma02_20180962;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ShowBookInfoActivity extends AppCompatActivity {

    final static String TAG = "ShowBookInfoActivity";


    TextView tvTitleInfo;
    TextView tvAuthorInfo;
    TextView tvPublisherInfo;
    TextView tvPubdateInfo;
    TextView tvIsbnInfo;
    TextView tvPriceInfo;
    TextView tvDescription;
    ImageView ivImageInfo;

    RadioGroup rgState;
    LinearLayout llPage;
    EditText etPage;
    TextView tvPassage;
    LinearLayout llPsg1;
    EditText etPassage1;
    LinearLayout llPsg2;
    EditText etPassage2;
    LinearLayout llPsg3;
    EditText etPassage3;
    Button btnPsgAdd;
    TextView tvReview;
    RatingBar rbRate;
    EditText etReview;


    ImageFileManager imageFileManager;
    BookDto book;
    BookDBHelper helper;

    boolean isNew;

    long _id;
    String title;
    String author;
    String publisher;
    String pubdate;
    int price;
    String isbn;
    String description;
    String imageLink;
    String imageFileName;
    int state;
    int page;
    List<String> passages;
    float rate;
    String review;
    int currentRbId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_info);

        imageFileManager = new ImageFileManager(this);
        helper = new BookDBHelper(this);

        tvTitleInfo = findViewById(R.id.tvTitleInfo);
        tvAuthorInfo = findViewById(R.id.tvAuthorInfo);
        tvPublisherInfo = findViewById(R.id.tvPublisherInfo);
        tvPubdateInfo = findViewById(R.id.tvPubdateInfo);
        tvPriceInfo = findViewById(R.id.tvPriceInfo);
        tvIsbnInfo = findViewById(R.id.tvIsbnInfo);
        tvDescription = findViewById(R.id.tvDescription);
        ivImageInfo = findViewById(R.id.ivImageInfo);
        rgState = findViewById(R.id.rgState);
        llPage = findViewById(R.id.llPage);
        etPage = findViewById(R.id.etPage);
        tvPassage = findViewById(R.id.tvPassage);
        llPsg1 = findViewById(R.id.llPsg1);
        etPassage1 = findViewById(R.id.etPassage1);
        llPsg2 = findViewById(R.id.llPsg2);
        etPassage2 = findViewById(R.id.etPassage2);
        llPsg3 = findViewById(R.id.llPsg3);
        etPassage3 = findViewById(R.id.etPassage3);
        btnPsgAdd = findViewById(R.id.btnPsgAdd);
        tvReview = findViewById(R.id.tvReview);
        rbRate = findViewById(R.id.rbRate);
        etReview = findViewById(R.id.etReview);

        book = (BookDto) getIntent().getSerializableExtra("book");
        isNew = getIntent().getBooleanExtra("isNew", true);

        _id = book.get_id();
        title = book.getTitle();
        author = book.getAuthor();
        publisher = book.getPublisher();
        pubdate = book.getPubdate();
        price = book.getPrice();
        isbn = book.getIsbn();
        description = book.getDescription();
        imageLink = book.getImageLink();
        imageFileName = book.getImageFileName();

        state = book.getState();
        page = book.getPage();
        passages = book.getPassages();
        rate = book.getRate();
        review = book.getReview();

        tvTitleInfo.setText(title);
        tvAuthorInfo.setText(author);
        tvPublisherInfo.setText(publisher);
        tvPubdateInfo.setText(pubdate);
        tvPriceInfo.setText(Integer.toString(price));
        tvIsbnInfo.setText(isbn);
        tvDescription.setText(description);

        // 현재 독서 상태에 따라 라디오 버튼 체크
        if (state == Constants.WISHBOOK) rgState.check(R.id.rbWishBook);
        if (state == Constants.READINGBOOK) {
            Log.d(TAG, "state: readingbook");
            rgState.check(R.id.rbReadingBook);
        }
        if (state == Constants.READBOOK) rgState.check(R.id.rbReadBook);

        if (page != 0) etPage.setText(Integer.toString(page));
        etPassage1.setText(passages.get(0));
        etPassage2.setText(passages.get(1));
        etPassage3.setText(passages.get(2));
        rbRate.setRating(rate);
        etReview.setText(review);

        if (isNew) {
            Bitmap bitmap = imageFileManager.getBitmapFromTemporary(imageLink); // 파일 이름
            if (bitmap == null) {
                Log.d(TAG, "Failed getting Bitmap: " + imageLink);
                ivImageInfo.setImageResource(R.mipmap.ic_launcher);
            }
            ivImageInfo.setImageBitmap(bitmap);
        } else {
            Bitmap bitmap = imageFileManager.getBitmapFromExternal(imageFileName);
            if (bitmap == null) {
                Log.d(TAG, "Failed getting Bitmap: " + imageFileName);
                ivImageInfo.setImageResource(R.mipmap.ic_launcher);
            } else {
                ivImageInfo.setImageBitmap(bitmap);
            }

        }

        // 독서 상태에 따라 다른 인터페이스를 제공함
        if (state == 1) {
            currentRbId = R.id.rbWishBook;
        } else if (state == 2) {
            currentRbId = R.id.rbReadingBook;
        } else if (state == 3) {
            currentRbId = R.id.rbReadBook;
        }
        setDisplayType(currentRbId);

        rgState.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                Log.d(TAG, "hello");
                setDisplayType(i);
            }
        });


    }

    // 체크된 라디오버튼 id에 따른 화면 제공
    public void setDisplayType(int id) {
        switch (id) {
            case R.id.rbWishBook:
                // 읽고 있는 페이지, 인상 깊은 구절, 평점, 독후감 안 보이게
                llPage.setVisibility(View.GONE);
                tvPassage.setVisibility(View.GONE);
                llPsg1.setVisibility(View.GONE);
                llPsg2.setVisibility(View.GONE);
                llPsg3.setVisibility(View.GONE);
                btnPsgAdd.setVisibility(View.GONE);
                tvReview.setVisibility(View.GONE);
                rbRate.setVisibility(View.GONE);
                etReview.setVisibility(View.GONE);
                break;
            case R.id.rbReadingBook:
                // 읽고 있는 페이지, 인상 깊은 구절 보이게
                llPage.setVisibility(View.VISIBLE);
                tvPassage.setVisibility(View.VISIBLE);
                llPsg1.setVisibility(View.VISIBLE);
                btnPsgAdd.setVisibility(View.VISIBLE);
                if (!etPassage2.getText().toString().equals("")) {
                    llPsg2.setVisibility(View.VISIBLE);
                }
                if (!etPassage3.getText().toString().equals("")) {
                    llPsg3.setVisibility(View.VISIBLE);
                    btnPsgAdd.setVisibility(View.GONE);
                }
                // 평점, 독후감 안 보이게
                tvReview.setVisibility(View.GONE);
                rbRate.setVisibility(View.GONE);
                etReview.setVisibility(View.GONE);
                break;
            case R.id.rbReadBook:
                // 읽고 있는 페이지 안 보이게
                llPage.setVisibility(View.GONE);
                // 인상 깊은 구절, 평점, 독후감 보이게
                tvPassage.setVisibility(View.VISIBLE);
                llPsg1.setVisibility(View.VISIBLE);
                btnPsgAdd.setVisibility(View.VISIBLE);
                if (!etPassage2.getText().toString().equals("")) {
                    llPsg2.setVisibility(View.VISIBLE);
                }
                if (!etPassage3.getText().toString().equals("")) {
                    llPsg3.setVisibility(View.VISIBLE);
                    btnPsgAdd.setVisibility(View.GONE);
                }
                tvReview.setVisibility(View.VISIBLE);
                rbRate.setVisibility(View.VISIBLE);
                etReview.setVisibility(View.VISIBLE);
                break;
        }
    }

    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.btnPsgAdd:
                if (llPsg2.getVisibility() == View.GONE) {
                    llPsg2.setVisibility(View.VISIBLE);
                } else if (llPsg3.getVisibility() == View.GONE) {
                    llPsg3.setVisibility(View.VISIBLE);
                    btnPsgAdd.setVisibility(View.GONE);
                }
                break;
            case R.id.btnSave:
                // passage가 존재하면 값을 passages(List)에 넣고, 존재하지 않는 만큼 ""로 채움
                List<String> newPassages = new ArrayList<>();
                String passage1 = etPassage1.getText().toString();
                String passage2 = etPassage2.getText().toString();
                String passage3 = etPassage3.getText().toString();

                if (!passage1.equals("")) {
                    newPassages.add(passage1);
                }
                if (!passage2.equals("")) {
                    newPassages.add(passage2);
                }
                if (!passage3.equals("")) {
                    newPassages.add(passage3);
                }
                while (newPassages.size() < 3) {
                    newPassages.add("");
                }

                // DB 저장
                SQLiteDatabase db = helper.getWritableDatabase();
                ContentValues row = new ContentValues();
                if (isNew) {
                    row.put(BookDBHelper.COL_TITLE, title);
                    row.put(BookDBHelper.COL_AUTHOR, author);
                    row.put(BookDBHelper.COL_PUBLISHER, publisher);
                    row.put(BookDBHelper.COL_PUBDATE, pubdate);
                    row.put(BookDBHelper.COL_PRICE, price);
                    row.put(BookDBHelper.COL_ISBN, isbn);
                    row.put(BookDBHelper.COL_DESCRIPTION, description);
                    imageFileName = imageFileManager.moveFileToExt(imageLink);
                    row.put(BookDBHelper.COL_IMAGEFILENAME, imageFileName);
                }

                int checkedId = rgState.getCheckedRadioButtonId();
                if (checkedId == R.id.rbWishBook) {
                    // state = WISHBOOK
                    row.put(BookDBHelper.COL_STATE, Constants.WISHBOOK);
                } else if (checkedId == R.id.rbReadingBook) {
                    // state = READINGBOOK
                    row.put(BookDBHelper.COL_STATE, Constants.READINGBOOK);
                    // 페이지, 구절 추가
                    row.put(BookDBHelper.COL_PAGE, Integer.parseInt(etPage.getText().toString()));
                    row.put(BookDBHelper.COL_PASSAGE1, newPassages.get(0));
                    row.put(BookDBHelper.COL_PASSAGE2, newPassages.get(1));
                    row.put(BookDBHelper.COL_PASSAGE3, newPassages.get(2));
                } else if (checkedId == R.id.rbReadBook) {
                    // state = READBOOK
                    row.put(BookDBHelper.COL_STATE, Constants.READBOOK);
                    // 구절, 독후감 추가
                    row.put(BookDBHelper.COL_PASSAGE1, newPassages.get(0));
                    row.put(BookDBHelper.COL_PASSAGE2, newPassages.get(1));
                    row.put(BookDBHelper.COL_PASSAGE3, newPassages.get(2));
                    row.put(BookDBHelper.COL_RATE, rbRate.getRating());
                    row.put(BookDBHelper.COL_REVIEW, etReview.getText().toString());
                }

                if (isNew) {
                    long result = db.insert(BookDBHelper.TABLE_NAME, null, row);

                    if (result > 0) {
                        Toast.makeText(this,"저장되었습니다.", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(this, "저장 실패", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    String whereClause = BookDBHelper.COL_ID + "=?";
                    String[] whereArgs = new String[] {String.valueOf(_id)};
                    int result = db.update(BookDBHelper.TABLE_NAME, row, whereClause, whereArgs);

                    if (result > 0) {
                        Toast.makeText(this,"수정되었습니다.", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(this, "수정 실패", Toast.LENGTH_SHORT).show();
                    }
                }

                helper.close();

                finish();
                break;
        }

    }

}