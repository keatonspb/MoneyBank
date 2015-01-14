package keaton.moneybank.frament;


import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import keaton.moneybank.R;
import keaton.moneybank.entity.DataItem;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link RowFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RowFragment extends Fragment {

    private static final String ARG_ITEM = "item";

    private DataItem item;



    // TODO: Rename and change types and number of parameters
    public static RowFragment newInstance(DataItem item) {
        RowFragment fragment = new RowFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_ITEM, item);
        fragment.setArguments(args);
        return fragment;
    }

    public RowFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            item = (DataItem) getArguments().getSerializable(ARG_ITEM);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.row, container, false);
        return view;
    }


}
