package keaton.moneybank.entity;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.io.Serializable;

/**
 * Created by keaton on 08.01.2015.
 */
@DatabaseTable(tableName = "popular_expense")
@SuppressWarnings("serial")
public class PopularItem implements Serializable {
    public static final String ID_FIELD = "id";
    public static final String ID_REASON_FIELD = "id_reason";
    public static final String NAME_REASON_FIELD = "reason_name";
    public static final String SUM_FIELD = "sum";

    @DatabaseField(columnName = ID_FIELD, generatedId = true, useGetSet = true)
    private int id;
    @DatabaseField(columnName = ID_REASON_FIELD, useGetSet = true)
    private Integer idReason;
    @DatabaseField(columnName = NAME_REASON_FIELD, useGetSet = true)
    private String reasonName;
    @DatabaseField(columnName = SUM_FIELD, useGetSet = true)
    private Integer sum;


    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public String getReasonName() {
        return reasonName;
    }
    public void setReasonName(String name) {
        this.reasonName = name;
    }
    public Integer getIdReason() {
        return idReason;
    }
    public void setIdReason(Integer id_reason) {
        this.idReason = id_reason;
    }
    public Integer getSum() {
        return sum;
    }
    public void setSum(Integer sum) {
        this.sum = sum;
    }

    @Override
    public String toString() {
        return getReasonName()+" - "+getSum().toString();
    }
}
