package il.ac.huji.todolist;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class TodoAdapter extends ArrayAdapter<TodoTuple> {

    final static private DateFormat dateFormatter = new SimpleDateFormat("dd/MM/yyyy");

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        TodoTuple item = getItem(position);
        LayoutInflater inflator = LayoutInflater.from(getContext());
        View view = inflator.inflate(R.layout.todo_layout, null);
        TextView titleView = (TextView) view.findViewById(R.id.txtTodoTitle);
        TextView dateView = (TextView) view.findViewById(R.id.txtTodoDueDate);
        titleView.setText(item.getTitle());
        dateView.setText(dateFormatter.format(item.getDate()));
        if (item.getDate().before(new Date())) {
            titleView.setTextColor(Color.RED);
            dateView.setTextColor(Color.RED);
        }
        return view;
    }

    public TodoAdapter(Context context) {
        super(context, android.R.layout.simple_list_item_1);
        // TODO Auto-generated constructor stub
    }


}
