package ddwu.mobile.finalproject.ma02_20160798;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    ImageFileManager imageFileManager;

    Button button;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        imageFileManager = new ImageFileManager(this);
    }

    public void onClick(View v){
        Intent intent = null;
        switch(v.getId()){
            case R.id.button1:
                intent = new Intent(MainActivity.this, SearchActivity.class);
                startActivity(intent);
                break;

            case R.id.button2:
                intent = new Intent(MainActivity.this, FavoriteListActivity.class);
                startActivity(intent);
                break;

        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        imageFileManager.clearTemporaryFiles();
    }
}