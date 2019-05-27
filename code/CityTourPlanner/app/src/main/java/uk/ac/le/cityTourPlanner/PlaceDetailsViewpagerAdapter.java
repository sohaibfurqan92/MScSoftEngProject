package uk.ac.le.cityTourPlanner;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

public class PlaceDetailsViewpagerAdapter extends PagerAdapter {
    private Context mContext;
    private String[] imageURLs;

    public PlaceDetailsViewpagerAdapter(Context context, String[] imageurls) {
        mContext = context;
        imageURLs = imageurls;
    }

    @Override
    public int getCount() {
        return imageURLs.length;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object o) {
        return view == o;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        ImageView placeImageView = new ImageView(mContext);
        Picasso.with(mContext).load(imageURLs[position]).resize(400,400).centerInside().into(placeImageView);
        container.addView(placeImageView);

        return placeImageView;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View) object);
    }
}
