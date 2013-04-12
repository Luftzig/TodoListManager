package il.ac.huji.todolist;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;

    private static final String DATABASE_NAME = "todo_db";

    private static final String TABLE_TODO = "todo";

    // Column names
    private static final String KEY_ID = "_id";
    private static final String KEY_TITLE = "title";
    private static final String KEY_DUE = "due";

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Notes to the reader:
        // A. SQLite has no "long" type, only integer.
        // B. Autoincrement is set implicitly by "primary key"
        db.execSQL("CREATE TABLE " + TABLE_TODO + " ( "
                + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " 
                + KEY_TITLE + " TEXT, "
                + KEY_DUE   + " INTEGER" + ")");

    }

    @Override
    public void onUpgrade(SQLiteDatabase arg0, int arg1, int arg2) {
        // Do nothing

    }

    public boolean insert(ITodoItem item) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_TITLE, item.getTitle());
        values.put(KEY_DUE, item.getDueDate().getTime());
        if (db.insert(TABLE_TODO, null, values) == -1) {
            db.close();
            return false;
        } else {
            db.close();
            return true;
        }
    }

    public boolean delete(ITodoItem item) {
        SQLiteDatabase db = this.getWritableDatabase();

        String WHERE = KEY_TITLE + " = ?" + " AND "
            + KEY_DUE + " = ?";
        String[] VALUES = new String[] {item.getTitle(), 
            String.valueOf(item.getDueDate().getTime())};
        db.delete(TABLE_TODO, WHERE, VALUES);
        db.close();
        return true;
    }

    public boolean update(ITodoItem item) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_TITLE, item.getTitle());
        values.put(KEY_DUE, item.getDueDate().getTime());

        if (db.update(TABLE_TODO, values, 
                    KEY_TITLE + " = ?", new String[] {item.getTitle()}) > 0) {
            return true;
        } else {
            return false;
        }
    }

    public Cursor allCursor() {
        String query = "SELECT * FROM " + TABLE_TODO;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        return cursor;
    }

    public List<ITodoItem> getAll() {
        List<ITodoItem> all = new ArrayList<ITodoItem>();

        Cursor cursor = allCursor();
        if (cursor.moveToFirst()) {
            do {
                Date date = new Date(cursor.getLong(2));
                TodoTuple todo = new TodoTuple(cursor.getString(1), date);
                all.add(todo);
            } while (cursor.moveToNext());
        }

        return all;
    }

}
