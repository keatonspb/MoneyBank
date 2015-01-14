package keaton.moneybank.adapter;


import android.content.Context;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import keaton.moneybank.ListActivity;
import keaton.moneybank.R;
import keaton.moneybank.entity.DataItem;
import keaton.moneybank.utils.Tools;

/**
 * Created by keaton on 08.01.2015.
 */
public class RowsListAdapterWithSwipe extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    public List items;
    private Context ctx;
    private View deleteLayout;
    private ListActivity.DeleteCallBack callBack;

    public RowsListAdapterWithSwipe(List items, ListActivity.DeleteCallBack callBack) {
        this.items = items;
        this.callBack = callBack;
    }
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        Log.d("MONEYLOG", "onCreateViewHolder");
        ctx = viewGroup.getContext();
        View v = LayoutInflater.from(ctx).inflate(R.layout.pager_row, viewGroup, false);

        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, final int i) {
        List<View> pages = new ArrayList<View>();
        DataItem item = (DataItem) items.get(i);

        View mainView = LayoutInflater.from(ctx).inflate(R.layout.row, ((ViewHolder) viewHolder).pager, false);
        deleteLayout = LayoutInflater.from(ctx).inflate(R.layout.delete_layot, ((ViewHolder) viewHolder).pager, false);
        pages.add(populate(item, mainView));
        pages.add(deleteLayout);

        RowPagerAdapter adapter = new RowPagerAdapter(pages);
        ((ViewHolder) viewHolder).pager.setAdapter(adapter);
        ((ViewHolder) viewHolder).pager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                callBack.deleteItemCallback(i);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });



    }

    public View populate(DataItem item, View v) {
        ImageView image = (ImageView) v.findViewById(R.id.row_icon);
        if(item.type == DataItem.TYPE_INCOME) {
            image.setImageResource(R.drawable.income_icon);
        } else {
            if(item.credit) {
                image.setImageResource(R.drawable.expence_credit_icon);
            } else {
                image.setImageResource(R.drawable.expense_icon);
            }

        }
        TextView title = (TextView) v.findViewById(R.id.row_sum);
        title.setText(ListActivity.formatSum(item.sum));

        TextView reason = (TextView) v.findViewById(R.id.row_reason);
        reason.setText(item.reason);

        TextView caption = (TextView) v.findViewById(R.id.row_caption);

        if(item.caption != null && !item.caption.equals("")) {
            caption.setText(" - " + item.caption);
        } else {
            caption.setText("");
        }

        TextView itemdate = (TextView) v.findViewById(R.id.row_item_date);

        String date = Tools.getDateFromString(item.date);
        itemdate.setText(date);

        return v;
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView image;
        public TextView title;
        public TextView reason;
        public TextView comments;
        public TextView date;
        public TextView itemdate;
        public ViewPager pager;

        public ViewHolder(View itemView) {
            super(itemView);
            pager = (ViewPager) itemView.findViewById(R.id.row_pager);
        }
    }


}
