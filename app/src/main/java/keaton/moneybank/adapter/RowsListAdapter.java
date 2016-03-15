package keaton.moneybank.adapter;


import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import keaton.moneybank.ListActivity;
import keaton.moneybank.R;
import keaton.moneybank.entity.DataItem;
import keaton.moneybank.utils.Tools;

/**
 * Created by keaton on 08.01.2015.
 */
public class RowsListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    public List items;
    private String last_date;
    private String prev_date;
    private Integer last_id = -1;
    public RowsListAdapter(List items) {
        this.items = items;
    }
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        Log.d("MONEYLOG", "onCreateViewHolder");
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.pager_row, viewGroup, false);
        return new ViewHolder(v);
    }
    public void restoreCounters() {
        last_date = null;
        prev_date = null;
        last_id = -1;
    }
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i) {

        DataItem item = (DataItem) items.get(i);
        if(item.type == DataItem.TYPE_INCOME) {
            ((ViewHolder) viewHolder).image.setImageResource(R.drawable.income_icon);
        } else {
            if(item.credit) {
                ((ViewHolder) viewHolder).image.setImageResource(R.drawable.expence_credit_icon);
            } else {
                ((ViewHolder) viewHolder).image.setImageResource(R.drawable.expense_icon);
            }

        }

            ((ViewHolder) viewHolder).title.setText(ListActivity.formatSum(item.sum));

        ((ViewHolder) viewHolder).reason.setText(item.reason);
        if(item.caption != null && !item.caption.equals("")) {
            ((ViewHolder) viewHolder).comments.setText(" - " + item.caption);
        } else {
            ((ViewHolder) viewHolder).comments.setText("");
        }

        String date = Tools.getDateFromString(item.date);
        ((ViewHolder) viewHolder).itemdate.setText(date);


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
        public TextView itemdate;

        public ViewHolder(View itemView) {
            super(itemView);
            image = (ImageView) itemView.findViewById(R.id.row_icon);
            title = (TextView) itemView.findViewById(R.id.row_sum);
            reason = (TextView) itemView.findViewById(R.id.row_reason);
            comments = (TextView) itemView.findViewById(R.id.row_caption);
            itemdate = (TextView) itemView.findViewById(R.id.row_item_date);
        }
    }
}
