package dao;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.table.TableUtils;
import models.EventModel;

import java.io.File;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.util.UUID;

public class CalendarDao {

    public static Dao<EventModel, UUID> getDao() throws SQLException, Exception {

        File databaseFile = new File(Paths.get(".").toAbsolutePath().normalize().toString()+"/event.db");

        try {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
            throw new SQLException(e);
        }

        JdbcConnectionSource connectionSource = new JdbcConnectionSource("jdbc:sqlite:" + databaseFile.getAbsolutePath());
        Dao<EventModel,UUID> eventDao = DaoManager.createDao(connectionSource, EventModel.class);
        TableUtils.createTableIfNotExists(connectionSource,EventModel.class);
        return eventDao;
    }
}
