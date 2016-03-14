package keaton.moneybank.adapter;

import android.app.Fragment;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import java.util.List;

import keaton.moneybank.R;
import keaton.moneybank.entity.PopularItem;
import keaton.moneybank.frament.AddExpense;

/**
 * Created by keaton on 08.01.2015.
 */
public class PopularAdapter extends ArrayAdapter {
    private Context context;
    LayoutInflater inflater;

    public PopularAdapter(Context context, List list) {
        super(context, R.layout.popular_item, list);
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.context = context;

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        PopularItem popularItem = (PopularItem) getItem(position);
        if(convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.popular_item, parent, false);
        }
        TextView name = (TextView) convertView.findViewById(R.id.popular_expense_item);
        TextView value = (TextView) convertView.findViewById(R.id.popular_expense_item_value);
        name.setText(popularItem.getReasonName().toLowerCase());
        value.setText(popularItem.getSum().toString());
        return convertView;
    }
}
