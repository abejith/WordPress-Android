package org.wordpress.android.datasets;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import org.wordpress.android.WordPress;
import org.wordpress.android.models.PublicizeConnection;
import org.wordpress.android.models.PublicizeConnectionList;
import org.wordpress.android.models.PublicizeService;
import org.wordpress.android.models.PublicizeServiceList;
import org.wordpress.android.util.SqlUtils;

public class PublicizeTable {
    private static final String SERVICES_TABLE    = "tbl_publicize_services";
    private static final String CONNECTIONS_TABLE = "tbl_publicize_connections";

    private static void createTables(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS " + SERVICES_TABLE + " ("
                + " id                          TEXT NOT NULL COLLATE NOCASE,"
                + " label                       TEXT NOT NULL COLLATE NOCASE,"
                + " description                 TEXT NOT NULL,"
                + "	genericon	                TEXT NOT NULL,"
                + " icon_url                    TEXT NOT NULL,"
                + "	connect_url	                TEXT NOT NULL,"
                + " is_jetpack_supported        INTEGER DEFAULT 0,"
                + " is_multi_user_id_supported  INTEGER DEFAULT 0,"
                + " PRIMARY KEY (id))");

        db.execSQL("CREATE TABLE IF NOT EXISTS " + CONNECTIONS_TABLE + " ("
                + " id                          INTEGER DEFAULT 0,"
                + " site_id                     INTEGER DEFAULT 0,"
                + " user_id                     INTEGER DEFAULT 0,"
                + " keyring_connection_id       INTEGER DEFAULT 0,"
                + " keyring_connection_user_id  INTEGER DEFAULT 0,"
                + " is_shared                   INTEGER DEFAULT 0,"
                + " service                     TEXT NOT NULL COLLATE NOCASE,"
                + " label                       TEXT NOT NULL COLLATE NOCASE,"
                + " external_name               TEXT NOT NULL,"
                + " external_display            TEXT NOT NULL,"
                + " external_profile_picture    TEXT NOT NULL,"
                + " refresh_url                 TEXT NOT NULL,"
                + " status                      TEXT NOT NULL,"
                + " PRIMARY KEY (id))");
    }

    private static SQLiteDatabase getReadableDb() {
        return WordPress.wpDB.getDatabase();
    }

    private static SQLiteDatabase getWritableDb() {
        return WordPress.wpDB.getDatabase();
    }

    /*
     * for testing purposes - clears then recreates tables
     */
    public static void reset() {
        getWritableDb().execSQL("DROP TABLE IF EXISTS " + SERVICES_TABLE);
        getWritableDb().execSQL("DROP TABLE IF EXISTS " + CONNECTIONS_TABLE);
        createTables(getWritableDb());
    }

    private static boolean mCreatedTables;
    private static void ensureTablesExist() {
        if (!mCreatedTables) {
            createTables(getWritableDb());
            mCreatedTables = true;
        }
    }

    public static PublicizeServiceList getServiceList() {
        ensureTablesExist();

        PublicizeServiceList serviceList = new PublicizeServiceList();
        Cursor c = getReadableDb().rawQuery("SELECT * FROM " + SERVICES_TABLE + " ORDER BY label", null);
        try {
            while (c.moveToNext()) {
                PublicizeService service = new PublicizeService();
                service.setId(c.getString(c.getColumnIndex("id")));
                service.setLabel(c.getString(c.getColumnIndex("label")));
                service.setDescription(c.getString(c.getColumnIndex("description")));
                service.setGenericon(c.getString(c.getColumnIndex("genericon")));
                service.setIconUrl(c.getString(c.getColumnIndex("icon_url")));
                service.setConnectUrl(c.getString(c.getColumnIndex("connect_url")));
                service.setIsJetpackSupported(SqlUtils.sqlToBool(c.getColumnIndex("is_jetpack_supported")));
                service.setIsMultiExternalUserIdSupported(SqlUtils.sqlToBool(c.getColumnIndex("is_multi_user_id_supported")));
                serviceList.add(service);
            }
            return serviceList;
        } finally {
            SqlUtils.closeCursor(c);
        }
    }

    public static void setServiceList(final PublicizeServiceList serviceList) {
        ensureTablesExist();

        SQLiteStatement stmt = null;
        SQLiteDatabase db = getWritableDb();
        db.beginTransaction();
        try {
            db.delete(SERVICES_TABLE, null, null);

            stmt = db.compileStatement(
                    "INSERT INTO " + SERVICES_TABLE
                    + " (id,"                           // 1
                    + " label,"                         // 2
                    + " description,"                   // 3
                    + " genericon,"                     // 4
                    + " icon_url,"                      // 5
                    + " connect_url,"                   // 6
                    + " is_jetpack_supported,"          // 7
                    + " is_multi_user_id_supported)"    // 8
                    + " VALUES (?1, ?2, ?3, ?4, ?5, ?6, ?7, ?8)");
            for (PublicizeService service : serviceList) {
                stmt.bindString(1, service.getId());
                stmt.bindString(2, service.getLabel());
                stmt.bindString(3, service.getDescription());
                stmt.bindString(4, service.getGenericon());
                stmt.bindString(5, service.getIconUrl());
                stmt.bindString(6, service.getConnectUrl());
                stmt.bindLong  (7, SqlUtils.boolToSql(service.isJetpackSupported()));
                stmt.bindLong  (8, SqlUtils.boolToSql(service.isMultiExternalUserIdSupported()));
                stmt.executeInsert();
            }

            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
            SqlUtils.closeStatement(stmt);
        }
    }

    public static long getNumServices() {
        return SqlUtils.getRowCount(getReadableDb(), SERVICES_TABLE);
    }

    public static PublicizeConnectionList getConnectionsForSite(int siteId) {
        ensureTablesExist();

        PublicizeConnectionList connectionList= new PublicizeConnectionList();
        String args[] = {Integer.toString(siteId)};
        Cursor c = getReadableDb().rawQuery("SELECT * FROM " + CONNECTIONS_TABLE + " WHERE site_id=?", args);
        try {
            while (c.moveToNext()) {
                PublicizeConnection connection = new PublicizeConnection();

                connection.siteId = siteId;
                connection.connectionId = c.getInt(c.getColumnIndex("id"));
                connection.userId = c.getInt(c.getColumnIndex("user_id"));
                connection.keyringConnectionId = c.getInt(c.getColumnIndex("keyring_connection_id"));
                connection.keyringConnectionUserId = c.getInt(c.getColumnIndex("keyring_connection_user_id"));

                connection.isShared = SqlUtils.sqlToBool(c.getInt(c.getColumnIndex("is_shared")));

                connection.setService(c.getString(c.getColumnIndex("service")));
                connection.setLabel(c.getString(c.getColumnIndex("label")));
                connection.setExternalName(c.getString(c.getColumnIndex("external_name")));
                connection.setExternalDisplay(c.getString(c.getColumnIndex("external_display")));
                connection.setExternalProfilePictureUrl(c.getString(c.getColumnIndex("external_profile_picture")));
                connection.setRefreshUrl(c.getString(c.getColumnIndex("refresh_url")));
                connection.setStatus(c.getString(c.getColumnIndex("status")));

                connectionList.add(connection);
            }
            return connectionList;
        } finally {
            SqlUtils.closeCursor(c);
        }
    }

    public static void setConnectionsForSite(int siteId, PublicizeConnectionList connectionList) {
        ensureTablesExist();

        SQLiteStatement stmt = null;
        SQLiteDatabase db = getWritableDb();
        db.beginTransaction();
        try {
            db.delete(CONNECTIONS_TABLE, "site_id=?", new String[] {Integer.toString(siteId)});

            stmt = db.compileStatement(
                    "INSERT INTO " + CONNECTIONS_TABLE
                            + " (id,"                           // 1
                            + " site_id,"                       // 2
                            + " user_id,"                       // 3
                            + " keyring_connection_id,"         // 4
                            + " keyring_connection_user_id,"    // 5
                            + " is_shared,"                     // 6
                            + " service,"                       // 7
                            + " label,"                         // 8
                            + " external_name,"                 // 9
                            + " external_display,"              // 10
                            + " external_profile_picture,"      // 11
                            + " refresh_url,"                   // 12
                            + " status)"                        // 13
                            + " VALUES (?1, ?2, ?3, ?4, ?5, ?6, ?7, ?8, ?9, ?10, ?11, ?12, ?13)");
            for (PublicizeConnection connection : connectionList) {
                stmt.bindLong(1, connection.connectionId);
                stmt.bindLong(2, connection.siteId);
                stmt.bindLong(3, connection.userId);
                stmt.bindLong(4, connection.keyringConnectionId);
                stmt.bindLong(5, connection.keyringConnectionUserId);

                stmt.bindLong(6, SqlUtils.boolToSql(connection.isShared));

                stmt.bindString(7, connection.getService());
                stmt.bindString(8, connection.getLabel());
                stmt.bindString(9, connection.getExternalName());
                stmt.bindString(10, connection.getExternalDisplay());
                stmt.bindString(11, connection.getExternalProfilePictureUrl());
                stmt.bindString(12, connection.getRefreshUrl());
                stmt.bindString(13, connection.getStatus());

                stmt.executeInsert();
            }

            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
            SqlUtils.closeStatement(stmt);
        }
    }

}
