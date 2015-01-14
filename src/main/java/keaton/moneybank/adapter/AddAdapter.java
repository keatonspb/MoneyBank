package keaton.moneybank.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import keaton.moneybank.frament.AddExpense;
import keaton.moneybank.frament.AddIncome;

/**
 * Created by keaton on 08.01.2015.
 */
public class AddAdapter extends FragmentPagerAdapter {
    public AddAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return new AddExpense();
            default:
                return new AddIncome();
        }

    }

    @Override
    public int getCount() {
        return 2;
    }
}
