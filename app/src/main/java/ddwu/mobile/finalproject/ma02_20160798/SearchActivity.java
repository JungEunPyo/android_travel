package ddwu.mobile.finalproject.ma02_20160798;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

public class SearchActivity extends AppCompatActivity {
    private final static int MY_PERMISSIONS_REQ_LOC = 100;

    Button btnSearchByName;
    Button btnSearchByLoca;
    EditText etSearch;
    ListView lvList;

    APINetworkManager networkManager;
    ImageFileManager imageFileManager;
    APIXMLParser parser;

    MyTravelAdapter adapter;
    ArrayList<TravelDto> lists;

    LocationManager locManager;
    double latitude;
    double longitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);


        etSearch = findViewById(R.id.etSearch);
        lvList = findViewById(R.id.lvList);
        networkManager = new APINetworkManager(SearchActivity.this);
        imageFileManager = new ImageFileManager(SearchActivity.this);
        locManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        parser = new APIXMLParser();
        lists = new ArrayList<>();

        btnSearchByName = findViewById(R.id.btnSearchByName);
        btnSearchByName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String keyword = etSearch.getText().toString();
                String url = getUrlByName();
                try {
                    new NetworkAsyncTask().execute(url, URLEncoder.encode(keyword,"UTF-8"));
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }
        });

        btnSearchByLoca = findViewById(R.id.btnSearchByLoca);
        btnSearchByLoca.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String url = getUrlByLoca(longitude,latitude);
                new NetworkAsyncTask().execute(url, "");
            }
        });


        adapter = new MyTravelAdapter(this, R.layout.listview_travel, lists);
        lvList.setAdapter(adapter);
        lvList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                TravelDto dto = adapter.getItem(i);
                Intent intent = new Intent(SearchActivity.this, DetailActivity.class);
                try {
                    dto = new DetailNetworkAsyncTask().execute(dto).get();

                } catch (ExecutionException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                intent.putExtra("travelData", dto);
                startActivity(intent);
            }
        });
    }

    LocationListener locListener = new LocationListener() {
        @Override
        public void onLocationChanged(@NonNull Location location) {
            //currentLoc =location;
            latitude = location.getLatitude();
            longitude = location.getLongitude();
            Toast.makeText(SearchActivity.this, "위도: " + latitude + "\n경도: " + longitude, Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onProviderDisabled(String provider) {

        }
    };

    protected String getUrlByName(){
        String url = getResources().getString(R.string.search_keyword_url);
        url += "?ServiceKey=" + getResources().getString(R.string.service_key);
        url += "&MobileApp=TRAVEL";
        url += "&MobileOS=AND";
        url +="&contentTypeId=12";
        url += "&arrange=O"; //(A=제목순, B=조회순, C=수정일순, D=생성일순) 대표이미지가 반드시 있는 정렬(O=제목순, P=조회순, Q=수정일순, R=생성일순)
        url += "&numOfRows=1000";
        url += "&keyword=";
        return url;
    }
    protected String getUrlByLoca(Double mapX, Double mapY){
        String url = getResources().getString(R.string.search_loca_url);
        url += "?ServiceKey=" + getResources().getString(R.string.service_key);
        url += "&MobileApp=TRAVEL";
        url += "&MobileOS=AND";
        url += "&arrange=E"; //(A=제목순, B=조회순, C=수정일순, D=생성일순, E=거리순)
        url += "&mapX=" + mapX;
        url += "&mapY=" + mapY;
        url += "&numOfRows=1000";
        url += "&radius=20000";//m단위
        return url;
    }


    class NetworkAsyncTask extends AsyncTask<String, Void, ArrayList<TravelDto>> {
        ProgressDialog progressDlg;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDlg = ProgressDialog.show(SearchActivity.this, "Wait", "Downloading...");
        }

        @Override
        protected ArrayList<TravelDto> doInBackground(String... strings) {
            String url = strings[0] + strings[1];
            String result = networkManager.downloadContents(url);
            ArrayList<TravelDto> list = parser.parse(result);
            return list;
        }

        @Override
        protected void onPostExecute(ArrayList<TravelDto> list) {
            progressDlg.dismiss();
            adapter.setList(list);
        }
    }
    class DetailNetworkAsyncTask extends AsyncTask<TravelDto, Void, TravelDto>{

        @Override
        protected TravelDto doInBackground(TravelDto... travelDtos) {
            String url  = getResources().getString(R.string.detail_url);
            url += "?ServiceKey=" + getResources().getString(R.string.service_key);
            url +="&contentId=" + travelDtos[0].getContentId();
            url += "&MobileApp=TRAVEL";
            url += "&MobileOS=AND";
            url += "&overviewYN=Y";

            String xml = networkManager.downloadContents(url);
            parser.parse(travelDtos[0], xml);
            return travelDtos[0];
        }

    }
    private boolean checkPermission(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION}, MY_PERMISSIONS_REQ_LOC);
                return false;
            } else {
                return true;
            }
        }
        return false;
    }

    /*권한승인 요청에 대한 사용자의 응답 결과에 따른 수행*/
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch(requestCode) {
            case MY_PERMISSIONS_REQ_LOC:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED
                        && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                    /*권한을 승인받았을 때 수행하여야 하는 동작 지정*/
                } else {
                    /*사용자에게 권한 제약에 따른 안내*/
                    Toast.makeText(this, "Permissions are not granted.", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }
    @Override
    protected void onPause() {
        super.onPause();
        locManager.removeUpdates(locListener);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.search_menu, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == R.id.goHome){
            Intent intent = new Intent(SearchActivity.this, MainActivity.class);
            startActivity(intent);
        }else if(item.getItemId() == R.id.changeLoca){
            if (checkPermission()) {
                locManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 10, locListener);
            }
        }else {
            return super.onOptionsItemSelected(item);
        }
        return true;
    }
}