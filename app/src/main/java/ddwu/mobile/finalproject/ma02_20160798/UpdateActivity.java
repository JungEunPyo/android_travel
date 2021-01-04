package ddwu.mobile.finalproject.ma02_20160798;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.Html;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.ExecutionException;

public class UpdateActivity extends AppCompatActivity {
    static final int REQUEST_IMAGE_CAPTURE=1;
    ImageView imgUpdate;
    EditText etUpdateTitle;
    TextView tvUpdateAddr;
    EditText etUpdateContent;
    EditText etUpdateMemo;
    TravelDBManager travelDBManager;
    GoogleMap mGoogleMap;
    MapFragment mapFragment;
    TravelDto travelData;
    ImageFileManager imageFileManager;
    String cameraImagePath = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update);
        mapFragment = (MapFragment)getFragmentManager().findFragmentById(R.id.updateMap);
        imageFileManager = new ImageFileManager(UpdateActivity.this);

        imgUpdate = findViewById(R.id.imgUpdate);

        imgUpdate.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if(!checkCameraHardware(UpdateActivity.this)) {
                    Toast.makeText(UpdateActivity.this, "카메라를 사용할 수 없습니다", Toast.LENGTH_SHORT).show();
                    return true;
                }
                AlertDialog.Builder builder = new AlertDialog.Builder(UpdateActivity.this);
                builder.setTitle("사진 변경")
                        .setMessage("사진을 변경하시겠습니까?")
                        .setPositiveButton("변경", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dispatchTakePictureIntent();
                                    }
                                }
                        )
                        .setNegativeButton("취소", null)
                        .show();
                return true;
            }
        });

        etUpdateTitle = findViewById(R.id.etUpdateTitle);
        tvUpdateAddr = findViewById(R.id.tvUpdateAddr);
        etUpdateContent = findViewById(R.id.etUpdateContent);
        etUpdateMemo = findViewById(R.id.etUpdateMemo);

        Intent intent = getIntent();
        travelData = (TravelDto) intent.getSerializableExtra("travelData");

        etUpdateTitle.setText(travelData.getTitle());
        tvUpdateAddr.setText(travelData.getAddr());
        etUpdateContent.setText(Html.fromHtml(travelData.getDetailContent()));
        imgUpdate.setImageResource(R.mipmap.ic_default);
        Bitmap bitmap = null;
        if(travelData.getImageFileName()!=null){
            bitmap = imageFileManager.getBitmapFromExternal(travelData.getImageFileName());
            if(bitmap!=null)
                imgUpdate.setImageBitmap(bitmap);
        }
        else if(travelData.getImageLink()!=null) {
            bitmap = imageFileManager.getBitmapFromTemporary(travelData.getImageLink());
            if (bitmap != null) {
                imgUpdate.setImageBitmap(bitmap);
            }
        }
        etUpdateMemo.setText(travelData.getMemo());
        mapFragment.getMapAsync(mapReadyCallback);
    }
    OnMapReadyCallback mapReadyCallback= new OnMapReadyCallback() {
        @Override
        public void onMapReady(GoogleMap googleMap) {
            mGoogleMap = googleMap;
            LatLng loca = new LatLng(travelData.getMapY(), travelData.getMapX());
            MarkerOptions options = new MarkerOptions().position(loca).title(travelData.getTitle());
            Marker marker = mGoogleMap.addMarker(options);
            mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(loca, 17));
            marker.showInfoWindow();
        }
    };
    public void onClick(View v) throws ExecutionException, InterruptedException {
        switch(v.getId()){
            case R.id.btnUpdateCancel:
                finish();
                break;
            case R.id.btnUpdateSave:
                travelDBManager = new TravelDBManager(UpdateActivity.this);
                travelData.setTitle(etUpdateTitle.getText().toString());
                travelData.setDetailContent(etUpdateContent.getText().toString());
                travelData.setMemo(etUpdateMemo.getText().toString());
                if(travelData.get_id()==-1) {
                    travelDBManager.addTravelList(travelData);
                }
                else {
                    travelDBManager.modifyTravelList(travelData);
                }
                Intent intent = new Intent(UpdateActivity.this, FavoriteListActivity.class);
                startActivity(intent);
                break;
        }
    }
    private boolean checkCameraHardware(Context context){
        if(context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA))
            return true;
        else
            return false;
    }
    private void dispatchTakePictureIntent(){
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if(takePictureIntent.resolveActivity(getPackageManager())!=null){
            File photoFile = null;
            try{
                photoFile = createImageFile();
            }catch(IOException ex){
                ex.printStackTrace();
            }

            if(photoFile!=null){
                Uri photoURI = FileProvider.getUriForFile(this,
                        "ddwu.mobile.finalproject.ma02_20160798.fileprovider", photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            setPic();
            String imageName = imageFileManager.getFileNameFromUrl(cameraImagePath);
            travelData.setImageFileName(imageName);
        }
    }
    private void setPic(){
        int targetW = imgUpdate.getWidth();
        int targetH = imgUpdate.getHeight();

        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(cameraImagePath, bmOptions);
        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;

        int scaleFactor = Math.min(photoW/targetW, photoH/targetH);

        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;
        bmOptions.inPurgeable = true;

        Bitmap bitmap = BitmapFactory.decodeFile(cameraImagePath, bmOptions);
        imgUpdate.setImageBitmap(bitmap);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.default_menu, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == R.id.goHome){
            Intent intent = new Intent(UpdateActivity.this, MainActivity.class);
            startActivity(intent);
        }else {
            return super.onOptionsItemSelected(item);
        }
        return true;
    }

    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName, ".jpg", storageDir
        );
        cameraImagePath = image.getAbsolutePath();
        return image;
    }
}