package keaton.moneybank.db.dao;

import com.j256.ormlite.dao.BaseDaoImpl;
import com.j256.ormlite.support.ConnectionSource;

import java.sql.SQLException;

import keaton.moneybank.entity.PopularItem;

/**
 * Created by keaton on 09.01.2015.
 */
public class PopularItemDao extends BaseDaoImpl<PopularItem, Integer> {
    public PopularItemDao(ConnectionSource connectionSource, Class<PopularItem> dataClass) throws SQLException
    {
        super(connectionSource, dataClass);
    }
}
