package ddwu.mobile.finalproject.ma02_20180962;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void onClick(View v) {
        Intent intent = null;

        switch (v.getId()) {
            case R.id.btnMyLibrary:
                // 나만의 서재 조회 페이지로 이동
                intent = new Intent(this, ShowMyLibraryActivity.class);
                break;
            case R.id.btnSearchBook:
                // 도서 검색 페이지로 이동
                intent = new Intent(this, SearchBookActivity.class);
                break;
            case R.id.btnSearchMap:
                // 서점과 도서관 검색 페이지로 이동
                intent = new Intent(this, ShowMapActivity.class);
                break;
        }

        if (intent != null) startActivity(intent);
    }
}