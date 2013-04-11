package il.ac.huji.todolist;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;

public class TodoListManagerActivity extends Activity {

    private ArrayAdapter<TodoTuple> adapter;
    private ListView todoList;

    final private int ADD_NEW_TODO = 100;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_todo_list_manager);
        adapter = new TodoAdapter(this);
        todoList = (ListView) findViewById(R.id.lstTodoItems);
        todoList.setAdapter(adapter);
    }

    /**
     * Create new activity to get item info and then add that to adapter
     */
    private void addItem() {
        //  
        Intent intent = new Intent(this, AddNewTodoItemActivity.class);
        startActivityForResult(intent, ADD_NEW_TODO);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != Activity.RESULT_OK) {
            // Abort
            return;
        }
        switch (requestCode) {
            case ADD_NEW_TODO:
                TodoTuple tuple = new TodoTuple(data.getStringExtra("title"),
                                    (Date) data.getSerializableExtra("dueDate"));
                adapter.add(tuple);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case R.id.menuItemAdd:
            addItem();
            break;
        case R.id.menuItemDelete:
            int pos = todoList.getSelectedItemPosition();
            Log.d(INPUT_SERVICE, "selected " + pos);
            if (pos >= 0) {
                adapter.remove((TodoTuple) todoList.getSelectedItem());
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
