package keaton.moneybank.entity;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.io.Serializable;

/**
 * Created by keaton on 08.01.2015.
 */
@DatabaseTable(tableName = "reason")
@SuppressWarnings("serial")
public class ReasonItem implements Serializable {
    public static final String ID_FIELD = "id";
    public static final String NAME_FIELD = "name";
    public static final String TYPE_FIELD = "type";

    @DatabaseField(columnName = ID_FIELD, id = true, useGetSet = true)
    private int id;
    @DatabaseField(columnName = NAME_FIELD, useGetSet = true)
    private String name;
    @DatabaseField(columnName = TYPE_FIELD, useGetSet = true)
    private String type;


    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getType() {
        return type;
    }
    public void setType(String type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return getName();
    }
}
