package il.ac.huji.todolist;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

public class TodoAdapter extends SimpleCursorAdapter {

    final static private DateFormat dateFormatter = new SimpleDateFormat(
            "dd/MM/yyyy");

    private final Context context;

    public TodoAdapter(Context context, Cursor cursor, String[] from, int[] to) {
        super(context, R.layout.todo_layout, cursor, from, to, 0);
        this.context = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflator = LayoutInflater.from(context);
        ITodoItem item = getItem(position);
        View view = inflator.inflate(R.layout.todo_layout, null);
        TextView titleView = (TextView) view.findViewById(R.id.txtTodoTitle);
        TextView dateView = (TextView) view.findViewById(R.id.txtTodoDueDate);
        titleView.setText(item.getTitle());
        Date date = item.getDueDate();
        // Log.d("TodoAdapter", "date is " + date + " time " + date.getTime());
        if (date == null || date.getTime() == 0) {
            dateView.setText("No due date");
        } else {
            dateView.setText(dateFormatter.format(date));
            if (date.before(new Date())) {
                titleView.setTextColor(Color.RED);
                dateView.setTextColor(Color.RED);
            }
        }
        return view;
    }

    public ITodoItem getItem(int position) {
        getCursor().moveToPosition(position);
        Long epoch = getCursor().getLong(2);
        Date date = null;
        if (epoch >= 0) {
            date = new Date(getCursor().getLong(2));
        }
        return new TodoTuple(getCursor().getString(1), date);
    }

    @Override
    public void bindView(View arg0, Context arg1, Cursor arg2) {
        // TODO Auto-generated method stub

    }

    @Override
    public View newView(Context arg0, Cursor arg1, ViewGroup arg2) {
        // TODO Auto-generated method stub
        return null;
    }

}
