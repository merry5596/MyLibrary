package ddwu.mobile.finalproject.ma02_20180962;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.FetchPlaceRequest;
import com.google.android.libraries.places.api.net.FetchPlaceResponse;
import com.google.maps.android.SphericalUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import noman.googleplaces.NRPlaces;
import noman.googleplaces.PlaceType;
import noman.googleplaces.PlacesException;
import noman.googleplaces.PlacesListener;


public class ShowMapActivity extends AppCompatActivity implements OnMapReadyCallback {

    final static String TAG = "ShowMapActivity";
    final static int PERMISSION_REQ_CODE = 100;

    LinearLayout llPosition;
    LinearLayout llLocal;
    EditText etLocal;
    RadioButton rbBookStore;
    RadioButton rbLibrary;
    ListView lvPlace;

    GoogleMap mGoogleMap;
    MarkerOptions markerOptions;
    LocationManager locationManager;
    LatLng currentLoc;
    LatLngResultReceiver latLngResultReceiver;
    PlaceAdapter adapter;
    LatLng centerLatlng;

    ArrayList<PlaceDto> placeList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_search);

        llPosition = findViewById(R.id.llPosition);
        llLocal = findViewById(R.id.llLocal);
        etLocal = findViewById(R.id.etLocal);
        rbBookStore = findViewById(R.id.rbBookStore);
        rbLibrary = findViewById(R.id.rbLibrary);
        lvPlace = findViewById(R.id.lvPlace);

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        latLngResultReceiver = new LatLngResultReceiver(new Handler());
        placeList = new ArrayList<>();
        adapter = new PlaceAdapter(this, R.layout.listview_place, placeList);
        lvPlace.setAdapter(adapter);

        Places.initialize(getApplicationContext(), getResources().getString(R.string.api_key));

        mapLoad();

        lvPlace.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                PlaceDto place = placeList.get(position);
                Marker marker = place.getMarker();
                LatLng latLng = marker.getPosition();

                mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 17));
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        locationManager.removeUpdates(locationListener);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mGoogleMap = googleMap;
        markerOptions = new MarkerOptions();
        placeList = new ArrayList<>();

        if (checkPermission()) {
            Location lastLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            Double latitude = lastLocation.getLatitude();
            Double longitude = lastLocation.getLongitude();
            Log.d(TAG, "lat: " + latitude + ", long: " + longitude);
            mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(latitude, longitude), 16));
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                    1000, 0, locationListener);
        }

    }


    LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            // 현재 위치 저장
            currentLoc = new LatLng(location.getLatitude(), location.getLongitude());
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {        }

        @Override
        public void onProviderEnabled(String provider) {        }

        @Override
        public void onProviderDisabled(String provider) {        }
    };



    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnCurrent:
                if (llLocal.getVisibility() == View.VISIBLE) {
                    llLocal.setVisibility(View.GONE);
                }
                while (currentLoc == null);
                if (currentLoc != null) {
                    mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLoc, 16));
                }
                break;
            case R.id.btnLocal:
                if (llLocal.getVisibility() == View.GONE) {
                    llLocal.setVisibility(View.VISIBLE);
                } else {
                    llLocal.setVisibility(View.GONE);
                }
                break;
            case R.id.btnMove:
                // 해당 지역으로 이동
                if (etLocal.getText().toString().equals("")) {
                    Toast.makeText(this, "지역명을 입력하세요.", Toast.LENGTH_SHORT).show();
                } else {
                    startLatLngService();
                }
                break;
            case R.id.btnSearchPlace:
                // 지도 중심의 좌표 저장
                CameraPosition cp = mGoogleMap.getCameraPosition();
                centerLatlng = cp.target;

                if (rbBookStore.isChecked()) {
                    searchStart(centerLatlng, PlaceType.BOOK_STORE);
                } else if (rbLibrary.isChecked()) {
                    searchStart(centerLatlng, PlaceType.LIBRARY);
                }
                break;
        }
    }

    /* 주소 → 위도/경도 변환 IntentService 실행 */
    private void startLatLngService() {
        String address = etLocal.getText().toString();
        Log.d(TAG, address);
        Intent intent = new Intent(this, FetchLatLngIntentService.class);
        intent.putExtra(Constants.RECEIVER, latLngResultReceiver);
        intent.putExtra(Constants.ADDRESS_DATA_EXTRA, address);
        startService(intent);
    }

    /* 주소 → 위도/경도 변환 ResultReceiver */
    class LatLngResultReceiver extends ResultReceiver {
        public LatLngResultReceiver(Handler handler) {
            super(handler);
        }

        @Override
        protected void onReceiveResult(int resultCode, Bundle resultData) {

            LatLng latlng = null;
            ArrayList<LatLng> latLngList = null;

            if (resultCode == Constants.SUCCESS_RESULT) {
                if (resultData == null) return;
                latLngList = (ArrayList<LatLng>) resultData.getSerializable(Constants.RESULT_DATA_KEY);
                if (latLngList == null) {
                    Log.e(TAG, "latLngList is null");
                } else {
                    latlng = latLngList.get(0);
                }
                mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latlng, 16));
            } else {
                Toast.makeText(ShowMapActivity.this, getString(R.string.no_address_found), Toast.LENGTH_SHORT).show();
            }
        }
    }


    /*입력된 유형의 주변 정보를 검색*/
    private void searchStart(LatLng centerLatlng, String type) {
        Log.d(TAG, "search started!");

        // 장소 정보 요청 (laglng에 해당하는 좌표의 반경 300미터 근처의 [type] 유형의 장소 정보 요청)
        new NRPlaces.Builder().listener(placesListener)
                .key(getResources().getString(R.string.api_key))
                .latlng(centerLatlng.latitude, centerLatlng.longitude)
                .radius(300)
                .type(type)
                .build()
                .execute();

        Log.d(TAG, "execute!");
    }

    // 장소 정보 요청 수신 리스너
    PlacesListener placesListener = new PlacesListener() {

        @Override
        public void onPlacesFailure(PlacesException e) {
            Log.d(TAG, "Places Failed");

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    // 기존 마커 삭제
                    for (PlaceDto marker : placeList) {
                        marker.getMarker().remove();
                    }
                    placeList.clear();
                    adapter.setList(placeList);
                    Toast.makeText(ShowMapActivity.this, "결과가 없습니다.", Toast.LENGTH_SHORT).show();
                }
            });
        }

        @Override
        public void onPlacesStart() {}

        // 장소 정보 요청 성공적으로 수신 시
        @Override
        public void onPlacesSuccess(final List<noman.googleplaces.Place> places) {
            Log.d(TAG, "Adding Markers");

            // UI 요소에 접근
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    // 기존 마커 삭제
                    for (PlaceDto marker : placeList) {
                        marker.getMarker().remove();
                    }

                    // 리스트 초기화
                    placeList.clear();
                    // id 1부터 늘려감
                    long id = 1;
                    // 마커 추가 작업
                    for (noman.googleplaces.Place place : places) {

                        markerOptions.title(place.getName());
                        markerOptions.position(new LatLng(place.getLatitude(), place.getLongitude()));
                        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(
                                BitmapDescriptorFactory.HUE_RED
                        ));

                        Marker newMarker = mGoogleMap.addMarker(markerOptions);
                        // 현재 위치와의 거리 계산
                        double distance = SphericalUtil.computeDistanceBetween(newMarker.getPosition(), centerLatlng);
                        String name = place.getName();
                        String address = place.getVicinity();

                        placeList.add(new PlaceDto(id, name, address, distance, newMarker));

                        id++;
                    }


                    Collections.sort(placeList);
                    adapter.setList(placeList);
                }
            });

        }

        @Override
        public void onPlacesFinished() {}
    };


    private void mapLoad() {
        MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }


    /* 필요 permission 요청 */
    private boolean checkPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[] {Manifest.permission.ACCESS_FINE_LOCATION},
                        PERMISSION_REQ_CODE);
                return false;
            }
        }
        return true;
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode == PERMISSION_REQ_CODE) {
            if(grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // 퍼미션을 획득하였을 경우 맵 로딩 실행
                mapLoad();
            } else {
                // 퍼미션 미획득 시 액티비티 종료
                Toast.makeText(this, "앱 실행을 위해 권한 허용이 필요함", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_library, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        Intent intent = null;

        switch (item.getItemId()) {
            case R.id.itemLibrary:
                intent = new Intent(this, ShowMyLibraryActivity.class);
                break;
        }

        startActivity(intent);

        return super.onOptionsItemSelected(item);
    }
}
