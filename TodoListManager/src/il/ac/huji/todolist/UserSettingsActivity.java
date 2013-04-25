package il.ac.huji.todolist;

import android.os.Bundle;
import android.preference.PreferenceActivity;

public class UserSettingsActivity extends PreferenceActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.settings);
    }

}
