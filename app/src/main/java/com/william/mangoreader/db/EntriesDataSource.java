package com.william.mangoreader.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import com.william.mangoreader.model.MangaCardItem;

import java.util.ArrayList;
import java.util.List;

public class EntriesDataSource {

    // Database fields
    private SQLiteDatabase database;
    private MySQLiteHelper dbHelper;
    private String[] allColumns = {MySQLiteHelper.COLUMN_ID,
            MySQLiteHelper.COLUMN_TITLE};

    public EntriesDataSource(Context context) {
        dbHelper = new MySQLiteHelper(context);
    }

    public void open() throws SQLException {
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    public MangaCardItem createEntry(MangaCardItem entry) {
        ContentValues values = new ContentValues();
        values.put(MySQLiteHelper.COLUMN_TITLE, entry.title);

        long insertId = database.insert(MySQLiteHelper.TABLE_ENTRIES, null,
                values);
        System.out.println("insertID: " + insertId);
        Cursor cursor = database.query(MySQLiteHelper.TABLE_ENTRIES,
                allColumns, MySQLiteHelper.COLUMN_ID + " = " + insertId, null,
                null, null, null);
        cursor.moveToFirst();
        MangaCardItem newItem = cursorToEntry(cursor);
        cursor.close();
        return newItem;
    }

    public void deleteEntry(MangaCardItem item) {
        long id = item.getDB_ID();
        System.out.println("Comment deleted with id: " + id);
        database.delete(MySQLiteHelper.TABLE_ENTRIES, MySQLiteHelper.COLUMN_ID
                + " = " + id, null);
    }

    public List<MangaCardItem> getAllEntries() {
        List<MangaCardItem> items = new ArrayList<MangaCardItem>();

        Cursor cursor = database.query(MySQLiteHelper.TABLE_ENTRIES,
                allColumns, null, null, null, null, null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            MangaCardItem item = cursorToEntry(cursor);
            items.add(item);
            cursor.moveToNext();
        }
        // make sure to close the cursor
        cursor.close();
        return items;
    }

    private MangaCardItem cursorToEntry(Cursor cursor) {
        MangaCardItem item = new MangaCardItem();
        item.setDB_ID(cursor.getLong(0));
        item.title = cursor.getString(1);
        return item;
    }
}