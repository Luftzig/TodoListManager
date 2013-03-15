package il.ac.huji.todolist;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;

public class TodoListManagerActivity extends Activity {

    private Array<String> todoStrings = ListArray<String>();
    static {
        todoString.add("Test!!!");
    }

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_todo_list_manager);
        ArrayAdapter adapter = new ArrayAdapter<String>(this, 
                        R.id.lstTodoItems, todoStrings);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.todo_list_manager, menu);
		return true;
	}

}
