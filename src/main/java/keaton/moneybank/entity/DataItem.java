package keaton.moneybank.entity;

import java.io.Serializable;

/**
 * Created by keato_000 on 08.01.2015.
 */
public class DataItem implements Serializable {
    public static int TYPE_EXPENSE = 0;
    public static int TYPE_INCOME = 1;
    public String sum;
    public int id;
    public int type;
    public String reason;
    public String caption;
    public String date;
    public Boolean credit = false;

    public String getReasonAsString() {
        if(type == DataItem.TYPE_EXPENSE) {
            return "expense";
        } else if(type == TYPE_INCOME) {
            return "income";
        } else {
            return "";
        }
    }

}
