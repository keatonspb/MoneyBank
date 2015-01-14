package keaton.moneybank.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import keaton.moneybank.entity.DataItem;

/**
 * Created by keato_000 on 13.01.2015.
 */
public class RowPagerAdapter extends PagerAdapter {
    private List<View> list;
    public RowPagerAdapter(List list) {
        Log.d("RowPagerAdapter" , "RowPagerAdapter listsize: "+list.size());
        this.list = list;
    }

    @Override
    public Object instantiateItem(ViewGroup collection, int position) {
        View v =  list.get(position);

        collection.addView(v,0);
        return v;
    }

    @Override
    public void destroyItem(ViewGroup collection, int position, Object view) {
        ((ViewPager) collection).removeView((View) view);
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return (view==object);

    }

    @Override
    public int getCount() {
        return list.size();
    }


}
