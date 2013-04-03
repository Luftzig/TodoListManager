package il.ac.huji.todolist;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class TodoAdapter extends ArrayAdapter<String> {

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		String todoString = getItem(position);
		LayoutInflater inflator = LayoutInflater.from(getContext());
		View view = inflator.inflate(R.layout.todo_layout, null);
		TextView txtView = (TextView) view.findViewById(R.id.todoTxt);
		txtView.setText(todoString);
		if (position % 2 == 0) {
			txtView.setTextColor(Color.RED);
		} else {
			txtView.setTextColor(Color.BLUE);
		}
		return view;
	}

	public TodoAdapter(Context context) {
		super(context, android.R.layout.simple_list_item_1);
		// TODO Auto-generated constructor stub
	}


}
