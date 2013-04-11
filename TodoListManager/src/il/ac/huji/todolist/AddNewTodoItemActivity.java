package il.ac.huji.todolist;

import java.util.Calendar;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.widget.DatePicker;
import android.widget.EditText;

public class AddNewTodoItemActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_add_new_todo_item);
    }

    public void cancelCallback(View view) {
        setResult(Activity.RESULT_CANCELED);
        finish();
    }

    public void okCallback(View view) {
        Intent returned = new Intent();
        EditText titleEdit = (EditText) findViewById(R.id.edtNewItem);
        DatePicker datePicker = (DatePicker) findViewById(R.id.datePicker);
        Calendar cal = Calendar.getInstance();
        cal.set(datePicker.getYear(), 
                datePicker.getMonth(), 
                datePicker.getDayOfMonth());
        Log.d("AddNewTodoItemActivity", "title " + titleEdit.getText());
        returned.putExtra("title", titleEdit.getText().toString());
        returned.putExtra("dueDate", cal.getTime());
        setResult(Activity.RESULT_OK, returned);
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.add_new_todo_item, menu);
        return true;
    }

}
