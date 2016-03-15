package keaton.moneybank.adapter;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.ArrayAdapter;
import android.widget.CursorAdapter;
import android.widget.SpinnerAdapter;

import java.util.ArrayList;
import java.util.List;

import keaton.moneybank.entity.ReasonItem;

/**
 * Created by keaton on 08.01.2015.
 */
public class ReasonAdapter extends ArrayAdapter {
    private Context context;
    LayoutInflater inflater;

    public ReasonAdapter(Context context, List list) {
        super(context, android.R.layout.simple_spinner_item, list);
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.context = context;
    }



}
