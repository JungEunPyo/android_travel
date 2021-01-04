package ddwu.mobile.finalproject.ma02_20160798;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class MyTravelAdapter extends BaseAdapter {
    private LayoutInflater inflater;
    private Context context;
    private int layout;
    private ArrayList<TravelDto> list;
    private APINetworkManager networkManager;
    private ImageFileManager imageFileManager;

    public MyTravelAdapter(Context context, int resource, ArrayList<TravelDto> list){
        this.context = context;
        this.layout = resource;
        this.list = list;
        networkManager = new APINetworkManager(context);
        imageFileManager = new ImageFileManager(context);
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }
    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public TravelDto getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        ViewHolder viewHolder = null;

        if(view == null){
            view = inflater.inflate(layout, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.tvTitle = view.findViewById(R.id.tvTitle);
            viewHolder.tvAddress = view.findViewById(R.id.tvAddress);
            viewHolder.imgTravel = view.findViewById(R.id.imgTravel);
            view.setTag(viewHolder);
        }else{
            viewHolder = (ViewHolder) view.getTag();
        }

        TravelDto dto = list.get(position);
        viewHolder.tvTitle.setText(dto.getTitle());
        viewHolder.tvAddress.setText(dto.getAddr());

        if(dto.getImageLink() == null && dto.getImageFileName() == null){
            viewHolder.imgTravel.setImageResource(R.mipmap.ic_default);
            return view;
        }
        // 파일에 있는지 확인
        // dto 의 이미지 주소 정보로 이미지 파일 읽기
        Bitmap savedBitmap = null;
        if(dto.getImageFileName()!=null){
            savedBitmap = imageFileManager.getBitmapFromExternal(dto.getImageFileName());
            if(savedBitmap!=null)
                viewHolder.imgTravel.setImageBitmap(savedBitmap);
            else
                viewHolder.imgTravel.setImageResource(R.mipmap.ic_default);
        }else{
            savedBitmap = imageFileManager.getBitmapFromTemporary(dto.getImageLink());
            if(savedBitmap!=null)
                viewHolder.imgTravel.setImageBitmap(savedBitmap);
            else{
                viewHolder.imgTravel.setImageResource(R.mipmap.ic_default);
                new GetImageAsyncTask(viewHolder).execute(dto.getImageLink());
            }
        }

        return view;
    }
    public void setList(ArrayList<TravelDto> list){
        this.list = list;
        notifyDataSetChanged();
    }
    static class ViewHolder{
        public TextView tvTitle = null;
        public TextView tvAddress = null;
        public ImageView imgTravel = null;
    }

    class GetImageAsyncTask extends AsyncTask<String, Void, Bitmap> {
        ViewHolder viewHolder;
        String imageAddress;

        public GetImageAsyncTask(ViewHolder holder){viewHolder = holder;}

        @Override
        protected Bitmap doInBackground(String... strings) {
            imageAddress = strings[0];
            Bitmap result = null;
            result = networkManager.downloadImage(imageAddress);
            return result;
        }
        @Override
        protected void onPostExecute(Bitmap bitmap) {
            if(bitmap!=null) {
                viewHolder.imgTravel.setImageBitmap(bitmap);
                imageFileManager.saveBitmapToTemporary(bitmap, imageAddress);
            }
        }
    }
}
