package ddwu.mobile.finalproject.ma02_20160798;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.FetchPlaceRequest;
import com.google.android.libraries.places.api.net.FetchPlaceResponse;
import com.google.android.libraries.places.api.net.PlacesClient;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;

import noman.googleplaces.NRPlaces;
import noman.googleplaces.PlaceType;
import noman.googleplaces.PlacesException;
import noman.googleplaces.PlacesListener;

public class DetailActivity extends AppCompatActivity {
    private final static int MY_PERMISSIONS_REQ_LOC = 100;
    ImageView imgDetail;
    TextView tvDetailTitle;
    TextView tvDetailAddr;
    TextView tvDetailContent;
    TextView tvDetailMemo;

    TravelDto travelData;
    ImageFileManager imageFileManager;
    GoogleMap mGoogleMap;
    MapFragment mapFragment;
    TravelDBManager travelDBManager;
    MarkerOptions markerOptions;
    List<Marker> markerList;
    PlacesClient placesClient;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        imageFileManager = new ImageFileManager(DetailActivity.this);
        mapFragment = (MapFragment)getFragmentManager().findFragmentById(R.id.detailMap);
        travelDBManager = new TravelDBManager(DetailActivity.this);
        Intent intent = getIntent();
        travelData = (TravelDto) intent.getSerializableExtra("travelData");

        mapFragment.getMapAsync(mapReadyCallback);
        imgDetail = findViewById(R.id.imgDetail);
        tvDetailTitle = findViewById(R.id.tvDetailTitle);
        tvDetailAddr = findViewById(R.id.tvDetailAddr);
        tvDetailContent = findViewById(R.id.tvDetailContent);
        tvDetailMemo = findViewById(R.id.tvDetailMemo);


        tvDetailTitle.setText(travelData.getTitle());
        tvDetailAddr.setText(travelData.getAddr());
        tvDetailContent.setText(Html.fromHtml(travelData.getDetailContent()));
        imgDetail.setImageResource(R.mipmap.ic_default);
        Bitmap bitmap = null;
        if(travelData.getImageFileName()!=null){
            bitmap = imageFileManager.getBitmapFromExternal(travelData.getImageFileName());
            if(bitmap!=null)
                imgDetail.setImageBitmap(bitmap);
        }
        else if(travelData.getImageLink()!=null) {
            bitmap = imageFileManager.getBitmapFromTemporary(travelData.getImageLink());
            if (bitmap != null) {
                imgDetail.setImageBitmap(bitmap);
            }
        }
        tvDetailMemo.setText(travelData.getMemo());

        if(checkPermission()){
            Places.initialize(getApplicationContext(), getString(R.string.google_api_key));
            placesClient = Places.createClient(this);
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.detail_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == R.id.share_data){
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            if(travelData.get_id() == -1)
                shareIntent.putExtra(Intent.EXTRA_TEXT, travelData.toString());
            else
                shareIntent.putExtra(Intent.EXTRA_TEXT, travelData.toStringFavorite());
            shareIntent.setType("text/plain");
            startActivity(shareIntent);
        }else {
            return super.onOptionsItemSelected(item);
        }
        return true;
    }

    public void onClick(View v) throws ExecutionException, InterruptedException {
        Intent intent = null;
        switch(v.getId()){
            case R.id.btnDetailUpdate:
                if(travelData.getImageFileName()==null)
                    travelData.setImageFileName(imageFileManager.moveFileToExt(travelData.getImageLink()));
                intent = new Intent(DetailActivity.this, UpdateActivity.class);
                intent.putExtra("travelData", travelData);
                startActivity(intent);
                break;
            case R.id.btnDetailSave:
                if(travelData.getImageFileName()==null)
                    travelData.setImageFileName(imageFileManager.moveFileToExt(travelData.getImageLink()));
                if(travelData.get_id() == -1)
                    travelDBManager.addTravelList(travelData);
                else
                    travelDBManager.modifyTravelList(travelData);
                Toast.makeText(this, "저장되었습니다", Toast.LENGTH_SHORT).show();
//                intent = new Intent(DetailActivity.this, FavoriteListActivity.class);
//                startActivity(intent);
                break;
            case R.id.findCafe:
                if(markerList.size()>0){
                    for(Marker removeMarker: markerList){
                        removeMarker.remove();
                    }
                    markerList.clear();
                }
                new NRPlaces.Builder().listener(placesListener)
                        .key(getString(R.string.google_api_key))
                        .latlng(travelData.getMapY(), travelData.getMapX())
                        .radius(200)
                        .type(PlaceType.CAFE)
                        .build()
                        .execute();

                break;
            case R.id.findRestaurant:
                if(markerList.size()>0){
                    for(Marker removeMarker: markerList){
                        removeMarker.remove();
                    }
                    markerList.clear();
                }
                new NRPlaces.Builder().listener(placesListener)
                        .key(getResources().getString(R.string.google_api_key))
                        .latlng(travelData.getMapY(), travelData.getMapX())
                        .radius(200)
                        .type(PlaceType.RESTAURANT)
                        .build()
                        .execute();

                break;
        }
    }

    PlacesListener placesListener = new PlacesListener() {
        @Override
        public void onPlacesFailure(PlacesException e) { }

        @Override
        public void onPlacesStart() { }

        @Override
        public void onPlacesSuccess(final List<noman.googleplaces.Place> places) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    for(noman.googleplaces.Place place: places){
                       markerOptions.title(place.getName());
                       markerOptions.position(new LatLng(place.getLatitude(), place.getLongitude()));
                       markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE));
                       Marker newMarker = mGoogleMap.addMarker(markerOptions);
                       newMarker.setTag(place.getPlaceId());
                       markerList.add(newMarker);
                       Log.d("PLACE", place.getName());
                    }

                }
            });
        }

        @Override
        public void onPlacesFinished() { }
    };
    OnMapReadyCallback mapReadyCallback= new OnMapReadyCallback() {
        @Override
        public void onMapReady(GoogleMap googleMap) {
            mGoogleMap = googleMap;
            markerOptions = new MarkerOptions();
            markerList = new ArrayList<Marker>();
            LatLng loca = new LatLng(travelData.getMapY(), travelData.getMapX());
            MarkerOptions options = new MarkerOptions().position(loca).title(travelData.getTitle());
            Marker marker = mGoogleMap.addMarker(options);
            mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(loca, 17));
            marker.showInfoWindow();

            mGoogleMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
                @Override
                public void onInfoWindowClick(Marker marker) {
                    if(!markerList.contains(marker))
                        return;
                    String placeId = marker.getTag().toString();

                    List<Place.Field> placeFields = Arrays.asList(Place.Field.NAME, Place.Field.PHONE_NUMBER, Place.Field.ADDRESS, Place.Field.PHOTO_METADATAS);
                    FetchPlaceRequest request = FetchPlaceRequest.builder(placeId, placeFields).build();

                    placesClient.fetchPlace(request).addOnSuccessListener(new OnSuccessListener<FetchPlaceResponse>() {
                        @Override
                        public void onSuccess(FetchPlaceResponse fetchPlaceResponse) {
                            final Place place = fetchPlaceResponse.getPlace();
                            String summary = "";
                            if(place.getAddress()!=null)
                                summary += "주소: " + place.getAddress();
                            if(place.getPhoneNumber()!=null) {
                                String number = place.getPhoneNumber().replace("+82 ", "0").trim();
                                summary += "\n전화번호: " + number;
                            }
                            AlertDialog.Builder builder = new AlertDialog.Builder(DetailActivity.this);
                            builder.setTitle(place.getName())
                                    .setMessage(summary)
                                    .setPositiveButton("확인", null)
                            .show();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            if(e instanceof ApiException){
                                ApiException apiException = (ApiException)e;
                                int statusCode = apiException.getStatusCode();
                                Toast.makeText(DetailActivity.this, "Place not found: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            });
        }


    };

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

}