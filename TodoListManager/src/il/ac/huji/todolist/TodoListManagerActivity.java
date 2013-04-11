package il.ac.huji.todolist;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;

public class TodoListManagerActivity extends Activity {

	private ArrayAdapter<TodoTuple> adapter;
	private ListView todoList;
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_todo_list_manager);
        adapter = new TodoAdapter(this);
        todoList = (ListView) findViewById(R.id.lstTodoItems);
        todoList.setAdapter(adapter);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.menuItemAdd:
			EditText newTodo = (EditText) findViewById(R.id.edtNewItem);
			if (newTodo.toString() != "") {
				adapter.add(new String(newTodo.getText().toString()));
			}
			newTodo.setText("");
			break;
		case R.id.menuItemDelete:
			int pos = todoList.getSelectedItemPosition();
			Log.d(INPUT_SERVICE, "selected " + pos);
			if (pos >= 0) {
				adapter.remove((String) todoList.getSelectedItem());
			}
			break;
		default:
			// Noop
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.todo_list_manager, menu);
		return true;
	}

}
