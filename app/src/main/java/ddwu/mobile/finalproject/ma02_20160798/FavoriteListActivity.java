package ddwu.mobile.finalproject.ma02_20160798;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.io.File;
import java.util.ArrayList;

public class FavoriteListActivity extends AppCompatActivity {

    ListView lvFavoriteList;
    TravelDBManager travelDBManager;
    MyTravelAdapter adapter;
    ArrayList<TravelDto> lists;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorite_list);
        travelDBManager = new TravelDBManager(FavoriteListActivity.this);
        lvFavoriteList = findViewById(R.id.lvFavoriteList);

        lists = travelDBManager.getAllTravelList();
        adapter = new MyTravelAdapter(this, R.layout.listview_travel, lists);
        lvFavoriteList.setAdapter(adapter);

        lvFavoriteList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(FavoriteListActivity.this, DetailActivity.class);
                intent.putExtra("travelData", lists.get(position));
                startActivity(intent);
            }
        });

        lvFavoriteList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                final int travelPosition = position;
                AlertDialog.Builder builder = new AlertDialog.Builder(FavoriteListActivity.this);
                builder.setTitle("영화 삭제")
                        .setMessage(lists.get(travelPosition).getTitle() + "를 삭제하시겠습니까?")
                        .setPositiveButton("삭제", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                String filename = lists.get(travelPosition).getImageFileName();
                                File file = new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), filename);
                                file.delete();
                                travelDBManager.removeMovie(lists.get(travelPosition).get_id());
                                onResume();
                            }
                        })
                        .setNegativeButton("취소", null)
                        .show();
                return true;
            }
        });
    }
    @Override
    protected void onResume() {
        super.onResume();
        lists.clear();
        lists.addAll(travelDBManager.getAllTravelList());
        adapter.notifyDataSetChanged();
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.default_menu, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == R.id.goHome){
            Intent intent = new Intent(FavoriteListActivity.this, MainActivity.class);
            startActivity(intent);
        }else {
            return super.onOptionsItemSelected(item);
        }
        return true;
    }

}