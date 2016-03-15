package keaton.moneybank.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

import java.sql.SQLException;

import keaton.moneybank.db.dao.PopularItemDao;
import keaton.moneybank.db.dao.ReasonDao;
import keaton.moneybank.entity.PopularItem;
import keaton.moneybank.entity.ReasonItem;

/**
 * Created by keaton on 08.01.2015.
 */
public class DatabaseHelper extends OrmLiteSqliteOpenHelper {
    private static final String DATABASE_NAME = "moneybank.db";
    private static final int DATABASE_VERSION = 1;

    private ReasonDao reasonDao;
    private PopularItemDao popularItemDao;

    public DatabaseHelper(Context context)
    {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    @Override
    public void onCreate(SQLiteDatabase database, ConnectionSource connectionSource) {
        Log.i("HITECH", "create db tables");
        try
        {
            TableUtils.createTable(connectionSource, ReasonItem.class);
            TableUtils.createTable(connectionSource, PopularItem.class);
        } catch (SQLException e)
        {
            Log.e("HITECH", "Unable to create datbases", e);
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase database, ConnectionSource connectionSource, int oldVersion, int newVersion) {

    }

    public ReasonDao getReasonDao() throws SQLException
    {
        if (reasonDao == null)
        {
            reasonDao = new ReasonDao(getConnectionSource(), ReasonItem.class);
        }
        return reasonDao;
    }

    public PopularItemDao getPopularItemDao() throws SQLException
    {
        if (popularItemDao == null)
        {
            popularItemDao = new PopularItemDao(getConnectionSource(), PopularItem.class);
        }
        return popularItemDao;
    }
}
