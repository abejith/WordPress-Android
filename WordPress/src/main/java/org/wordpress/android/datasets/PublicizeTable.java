package org.wordpress.android.datasets;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import com.automattic.android.tracks.datasets.SqlUtils;

import org.wordpress.android.WordPress;
import org.wordpress.android.models.PublicizeService;
import org.wordpress.android.models.PublicizeServiceList;

public class PublicizeTable {
    private static final String SERVICES_TABLE = "tbl_publicize_services";

    private static void createTables(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS " + SERVICES_TABLE + " ("
                + " name            TEXT NOT NULL COLLATE NOCASE,"
                + " label           TEXT NOT NULL,"
                + " description     TEXT NOT NULL,"
                + "	noticon		    TEXT NOT NULL,"
                + " icon_url        TEXT NOT NULL,"
                + "	connect_url	    TEXT NOT NULL,"
                + " PRIMARY KEY (name))");
    }

    private static SQLiteDatabase getReadableDb() {
        return WordPress.wpDB.getDatabase();
    }

    private static SQLiteDatabase getWritableDb() {
        return WordPress.wpDB.getDatabase();
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
        Cursor c = getReadableDb().rawQuery("SELECT * FROM " + SERVICES_TABLE + " ORDER BY name", null);
        try {
            while (c.moveToNext()) {
                PublicizeService service = new PublicizeService();
                service.setName(c.getString(c.getColumnIndex("name")));
                service.setLabel(c.getString(c.getColumnIndex("label")));
                service.setDescription(c.getString(c.getColumnIndex("description")));
                service.setNoticon(c.getString(c.getColumnIndex("noticon")));
                service.setIconUrl(c.getString(c.getColumnIndex("icon_url")));
                service.setConnectUrl(c.getString(c.getColumnIndex("connect_url")));
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
                    + " (name, label, description, noticon, icon_url, connect_url)"
                    + " VALUES (?1, ?2, ?3, ?4, ?5, ?6)");
            for (PublicizeService service : serviceList) {
                stmt.bindString(1, service.getName());
                stmt.bindString(2, service.getLabel());
                stmt.bindString(3, service.getDescription());
                stmt.bindString(4, service.getNoticon());
                stmt.bindString(5, service.getIconUrl());
                stmt.bindString(6, service.getConnectUrl());
                stmt.executeInsert();
            }

            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
            SqlUtils.closeStatement(stmt);
        }
    }

}
