package il.ac.huji.todolist;

import java.util.Date;
import java.util.List;

import com.parse.FindCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;

public class TodoDAL {

    private DBHelper db;

    public TodoDAL(Context context) {
        db = new DBHelper(context);
        Parse.initialize(context, context.getString(R.string.parseApplication),
                context.getString(R.string.clientKey));
    }

    public boolean insert(ITodoItem todoItem) {
        ParseObject todoObj = new ParseObject("todo");
        todoObj.put("title", todoItem.getTitle());
        todoObj.put("due", todoItem.getDueDate().getTime());
        todoObj.saveInBackground();
        return db.insert(todoItem);
    }

    public void parseUpdate(ITodoItem item) {
        ParseQuery query = new ParseQuery("todo");
        query.whereEqualTo("title", item.getTitle());
        final Date date = item.getDueDate();
        query.findInBackground(new FindCallback() {

            @Override
            public void done(List<ParseObject> list, ParseException e) {
                if (e != null) {
                    Log.e("TodoDAL", "Error while updating", e);
                    return;
                }
                for (ParseObject obj : list) {
                    obj.put("due", date.getTime());
                }
            }
        });
    }

    public boolean update(ITodoItem todoItem) {
        parseUpdate(todoItem);
        return db.update(todoItem);
    }

    public void parseDelete(ITodoItem item) {
        ParseQuery query = new ParseQuery("todo");
        final Date date = item.getDueDate();
        query.whereEqualTo("title", item.getTitle());
        query.whereEqualTo("due", date.getTime());
        query.findInBackground(new FindCallback() {

            @Override
            public void done(List<ParseObject> list, ParseException e) {
                if (e != null) {
                    Log.e("TodoDAL", "Error while deleting", e);
                    return;
                }
                for (ParseObject obj : list) {
                    obj.deleteInBackground();
                }
            }
        });
    }

    public boolean delete(ITodoItem todoItem) {
        parseDelete(todoItem);
        return db.delete(todoItem);
    }

    public List<ITodoItem> all() {
        return db.getAll();
    }

    public Cursor allCursor() {
        return db.allCursor();
    }
}
