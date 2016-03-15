package keaton.moneybank.db.dao;

import com.j256.ormlite.dao.BaseDaoImpl;
import com.j256.ormlite.support.ConnectionSource;

import java.sql.SQLException;

import keaton.moneybank.entity.ReasonItem;

/**
 * Created by keaton on 09.01.2015.
 */
public class ReasonDao extends BaseDaoImpl<ReasonItem, Integer> {
    public ReasonDao(ConnectionSource connectionSource, Class<ReasonItem> dataClass) throws SQLException
    {
        super(connectionSource, dataClass);
    }
}
