package il.ac.huji.todolist;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.preference.PreferenceManager;
import android.util.Log;

public class DBHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;

    private static final String DATABASE_NAME = "todo_db";

    // Todo's table
    private static final String TABLE_TODO = "todo";
    // Column names
    private static final String KEY_ID = "_id";
    private static final String KEY_TITLE = "title";
    private static final String KEY_DUE = "due";

    // Presistent Data
    // This table will be used to save user preferences and other assortments
    // of data that has to be kept between sessions.
    private static final String TABLE_PRESISTENT = "presistent";
    private static final String PRES_ID = "_id";
    private static final String PRES_KEY = "key";
    private static final String PRES_VALUE = "value";

    // Thumbmails Table
    private static final String TABLE_THUMBS = "thumbmail";
    private static final String THUMBS_ID = "_id";
    private static final String THUMBS_PATH = "path";

    private Context context;
    
    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
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
        db.execSQL("CREATE TABLE " + TABLE_PRESISTENT + " ( "
                + PRES_ID + " INTEGER PRIMARY KEY, " 
                + PRES_KEY + " TEXT UNIQUE, "
                + PRES_VALUE + " TEXT)");
        db.execSQL("CREATE TABLE " + TABLE_THUMBS + " ( "
                + THUMBS_ID + " INTEGER PRIMARY KEY, "
                + THUMBS_PATH + " TEXT)");
        // Populating tables
        SharedPreferences sharedPerfs = PreferenceManager.getDefaultSharedPreferences(context);
        String hashTag = sharedPerfs.getString("prefHashTag", context.getString(R.string.hashTagDefault));
        if (hashTag.startsWith("#")) {
            hashTag = hashTag.substring(1);
        }
        Log.d("DBHelper", "initializing hash tag to " + hashTag);
        this.insertKey(context.getString(R.string.hashTagKey), 
                hashTag, db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase arg0, int arg1, int arg2) {
        // Do nothing

    }

    public boolean insert(ITodoItem item) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_TITLE, item.getTitle());
        if (item.getDueDate() != null) {
            values.put(KEY_DUE, item.getDueDate().getTime());
        } else {
            values.put(KEY_DUE, 0);
        }
        if (db.insert(TABLE_TODO, null, values) == -1) {
            db.close();
            return false;
        } else {
            db.close();
            return true;
        }
    }

    public boolean insertKey(String key, String value) {
        SQLiteDatabase db = this.getWritableDatabase();
        boolean result = this.insertKey(key, value, db);
        db.close();
        return result;
    }

    public boolean insertKey(String key, String value, SQLiteDatabase db) {
        Log.d("DBHelper", "insertKey called for " + key + ", " + value);
        ContentValues values = new ContentValues();
        values.put(PRES_KEY, key);
        values.put(PRES_VALUE, value);
        if (db.insert(TABLE_PRESISTENT, null, values) == -1) {
            return false;
        } else {
            return true;
        }
    }

    public boolean insertThumb(String path) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(THUMBS_PATH, path);
        if (db.insert(TABLE_THUMBS, null, values) == -1) {
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

    /**
     * Updates the requested string and returns the old value.
     * If string does not exists, it is added.
     */
    public String updateKey(String key, String newValue) {
        Log.d("DBHelper", "updateKey called for " + key + ", " + newValue);
        SQLiteDatabase db = this.getWritableDatabase();
        String oldValue = getPresistentData(key);
        if (oldValue == null) {
            if (insertKey(key, newValue)) {
                return newValue;
            } else {
                return null;
            }
        }
        ContentValues values = new ContentValues();
        values.put(PRES_KEY, key);
        values.put(PRES_VALUE, newValue);

        if (db.update(TABLE_PRESISTENT, values, 
                    PRES_KEY + " = ?", new String[] {key}) > 0) {
            return oldValue;
        } else {
            Log.e("DBHelper", "updateKey failed");
            return null;
        }
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

    public String getPresistentData(String key) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_PRESISTENT, new String[] {PRES_VALUE}, 
                PRES_KEY + " = ?", new String[] {key}, null, null, null);
        if (cursor.moveToFirst()) {
            return cursor.getString(0);
        } else {
            return null;
        }
    }

    public Map<String, String> getAllPresistent() {
        Map<String, String> map = new HashMap<String, String>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_PRESISTENT, new String[] {PRES_KEY, PRES_VALUE}, 
                null, null, null, null, null);

        if (cursor.moveToFirst()) {
            do {
                map.put(cursor.getString(0), cursor.getString(1));
            } while (cursor.moveToNext());
        }

        return map;
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
