package il.ac.huji.todolist;

import java.util.Date;

import android.net.Uri;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.ListView;

import com.parse.Parse;

public class TodoListManagerActivity extends Activity {

    private TodoAdapter adapter;
    private ListView todoList;
    private TodoDAL dal;

    final private int ADD_NEW_TODO = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_todo_list_manager);
        // adapter = new TodoAdapter(this);
        todoList = (ListView) findViewById(R.id.lstTodoItems);
        registerForContextMenu(todoList);
        // todoList.setAdapter(adapter);
        // DB stuff
        Parse.initialize(this, getString(R.string.parseApplication), 
                getString(R.string.clientKey));
        dal = new TodoDAL(this);
        String[] columns = new String[] {"title", "due"};
        int[] ids = new int[] {R.id.txtTodoTitle, R.id.txtTodoDueDate};
        adapter = new TodoAdapter(this, dal.allCursor(), columns, ids);
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
            // adapter.add(tuple);
            dal.insert(tuple);
            adapter.changeCursor(dal.allCursor());
            adapter.notifyDataSetChanged();
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
                dal.delete((TodoTuple) todoList.getSelectedItem());
                adapter.changeCursor(dal.allCursor());
                adapter.notifyDataSetChanged();
            }
            break;
        default:
            // Noop
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
        switch (item.getItemId()) {
            case R.id.menuItemDelete:
                dal.delete((ITodoItem) adapter.getItem(info.position));
                adapter.changeCursor(dal.allCursor());
                adapter.notifyDataSetChanged();
                return true;
            case R.id.menuItemCall:
                String title = ((ITodoItem) adapter.getItem(info.position)).getTitle();
                String number = title.substring("Call ".length());
                Log.d("ContextMenu", "num to call " + number);
                Intent callIntent = new Intent(Intent.ACTION_DIAL);
                callIntent.setData(Uri.parse("tel:" + number));
                startActivity(callIntent);
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
            ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.context_menu, menu);
        AdapterView.AdapterContextMenuInfo info =
                (AdapterView.AdapterContextMenuInfo) menuInfo;
        String title = ((ITodoItem) adapter.getItem(info.position)).getTitle();
        Log.d(TodoListManagerActivity.class.toString(), "position is " + info.position);
        menu.setHeaderTitle(title);
        if (title.startsWith("Call ")) {
            MenuItem menuItem = menu.findItem(R.id.menuItemCall);
            menuItem.setVisible(true);
            menuItem.setTitle(title);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.todo_list_manager, menu);
        return true;
    }

}
