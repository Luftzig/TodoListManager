package il.ac.huji.todolist;

import java.util.Date;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.widget.DatePicker;
import android.widget.EditText;

public class AddNewTodoItemActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_todo_item);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
    }

    public void cancelCallback(View view) {
        setResult(Activity.RESULT_CANCELED);
        finish();
    }

    public void okCallback(View view) {
        Intent returned = new Intent();
        EditText titleEdit = (EditText) findViewById(R.id.edtNewItem);
        DatePicker datePicker = (DatePicker) findViewById(R.id.datePicker);
        returned.putExtra("title", titleEdit.getText());
        returned.putExtra("year", datePicker.getYear());
        returned.putExtra("month", datePicker.getMonth());
        returned.putExtra("day", datePicker.getDayOfMonth());
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
