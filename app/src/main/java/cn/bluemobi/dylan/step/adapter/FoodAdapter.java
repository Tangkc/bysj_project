package cn.bluemobi.dylan.step.adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.List;

import cn.bluemobi.dylan.step.R;
import cn.bluemobi.dylan.step.bean.FoodHistoryList;

public class FoodAdapter extends BaseAdapter {

    private Activity activity;
    List<FoodHistoryList.FoodHistory> arrayList;
    protected DisplayImageOptions options = new DisplayImageOptions.Builder()
            .cacheInMemory(true)
            .cacheOnDisk(true).considerExifParams(true)
            .bitmapConfig(Bitmap.Config.RGB_565).build();

    public FoodAdapter(Activity context, List<FoodHistoryList.FoodHistory> arrayList) {
        this.activity = context;
        this.arrayList = arrayList;
    }


    @Override
    public int getCount() {
        return arrayList.size();
    }

    @Override
    public Object getItem(int position) {
        return arrayList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View tempView = convertView;
        if (tempView == null) {
            tempView = LayoutInflater.from(activity).inflate(R.layout.item_food, null);
        }
        ImageView icon = (ImageView) tempView.findViewById(R.id.food_icon);
        TextView foodheat = (TextView) tempView.findViewById(R.id.food_heat);
        TextView foodCon = (TextView) tempView.findViewById(R.id.food_content);
        if (foodheat != null) {
            foodheat.setText(arrayList.get(position).getHeat()+"");
        }
        if (foodCon != null) {
            foodCon.setText(arrayList.get(position).getFoodIds());
        }
        if (!TextUtils.isEmpty(arrayList.get(position).getIconUrl())) {
            ImageLoader.getInstance().displayImage(arrayList.get(position).getIconUrl(), icon, options);
        }
        return tempView;
    }
}
