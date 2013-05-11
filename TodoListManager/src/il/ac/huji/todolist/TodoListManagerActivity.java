package il.ac.huji.todolist;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
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
import android.widget.TextView;

public class TodoListManagerActivity extends Activity {

    /**
     * Representation of single tweet
     */
    private class Tweet {
        public String id;
        public String text;
        public Date due;

        /**
         * @param id
         * @param text
         */
        public Tweet(String id, String text, Date due) {
            this.id = id;
            this.text = text;
            this.due = due;
        }
    }

    private TodoAdapter adapter;
    private ListView todoList;
    private TodoDAL dal;
    private DBHelper dbHelper;

    // Constants
    final private int ADD_NEW_TODO = 100;
    private static final int RESULT_SETTINGS = 200;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_todo_list_manager);
        // adapter = new TodoAdapter(this);
        todoList = (ListView) findViewById(R.id.lstTodoItems);
        registerForContextMenu(todoList);
        // todoList.setAdapter(adapter);
        // DB stuff
        dal = new TodoDAL(this);
        dbHelper = dal.getDb(); // TodoDAL is a deprecated wrapper
        String[] columns = new String[] { "title", "due" };
        int[] ids = new int[] { R.id.txtTodoTitle, R.id.txtTodoDueDate };
        adapter = new TodoAdapter(this, dal.allCursor(), columns, ids);
        todoList.setAdapter(adapter);
        getTweets();
    }

    private void getTweets() {
        TwitterGetTask tweetGetter = new TwitterGetTask();
        String lastTweetId = dbHelper
                .getPresistentData(getString(R.string.lastTweetKey));
        // Populating tables
        SharedPreferences sharedPerfs = PreferenceManager
                .getDefaultSharedPreferences(this);
        String hashTag = sharedPerfs.getString("prefHashTag",
                getString(R.string.hashTagDefault));
        if (hashTag.startsWith("#")) {
            hashTag = hashTag.substring(1);
        }
        Log.d("DBHelper", "initializing hash tag to " + hashTag);
        String url = "http://search.twitter.com/search.json?result_type=recent&q=%23"
                + hashTag + "&rpp=100";
        if (lastTweetId == null || lastTweetId.isEmpty() || lastTweetId == "0") {
            // Get 100 latest
            tweetGetter.execute(url, "There are %d tweets with tag " + hashTag
                    + ". Import?");
        } else {
            tweetGetter
                    .execute(url + "&since_id=" + lastTweetId,
                            "There are %d new tweets with tag " + hashTag
                                    + ". Import?");
        }
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
        switch (requestCode) {
        case ADD_NEW_TODO:
            if (resultCode != Activity.RESULT_OK) {
                // Abort
                return;
            }
            TodoTuple tuple = new TodoTuple(data.getStringExtra("title"),
                    (Date) data.getSerializableExtra("dueDate"));
            // adapter.add(tuple);
            dal.insert(tuple);
            adapter.changeCursor(dal.allCursor());
            adapter.notifyDataSetChanged();
            break;
        case RESULT_SETTINGS:
            Log.d("onActivityResult", "Results returned");
            SharedPreferences sharedPerfs = PreferenceManager
                    .getDefaultSharedPreferences(this);
            String hashTag = sharedPerfs.getString("prefHashTag", "todoapp");
            Log.d("TodoListManagerActivity", "new hashTag " + hashTag);
            break;
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
            Log.d("Test", "selected " + pos);
            if (pos >= 0) {
                dal.delete((TodoTuple) todoList.getSelectedItem());
                adapter.changeCursor(dal.allCursor());
                adapter.notifyDataSetChanged();
            }
            break;
        case R.id.menuSettings:
            Intent i = new Intent(this, UserSettingsActivity.class);
            startActivityForResult(i, RESULT_SETTINGS);
            break;
        default:
            // Noop
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterContextMenuInfo info = (AdapterContextMenuInfo) item
                .getMenuInfo();
        switch (item.getItemId()) {
        case R.id.menuItemDelete:
            dal.delete((ITodoItem) adapter.getItem(info.position));
            adapter.changeCursor(dal.allCursor());
            adapter.notifyDataSetChanged();
            return true;
        case R.id.menuItemCall:
            String title = ((ITodoItem) adapter.getItem(info.position))
                    .getTitle();
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
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
        String title = ((ITodoItem) adapter.getItem(info.position)).getTitle();
        Log.d(TodoListManagerActivity.class.toString(), "position is "
                + info.position);
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

    /**
     * Handle pulling updates from twitter
     */
    private class TwitterGetTask extends AsyncTask<String, Integer, Long> {

        private String tweetsString;
        private String message;

        private List<Tweet> tweets;

        // Constants
        private final static String TWEET_TEXT = "text";
        private final static String TWEET_ID = "id_str";

        @Override
        protected Long doInBackground(String... args) {
            if (args.length != 2) {
                Log.e("TwitterGetTask", "Wrong number of args!");
                return null;
            }
            message = args[1];
            try {
                tweetsString = getTweets(args[0]);
                tweets = parseJson();
            } catch (Exception e) {
                Log.e("TwitterGetTask", "Exception while getting tweets", e);
            }
            return 0L;
        }

        protected void addTweets() {
            for (Tweet t : tweets) {
                TodoTuple todo = new TodoTuple(t.text, t.due);
                dal.insert(todo);
            }
            adapter.changeCursor(dal.allCursor());
            adapter.notifyDataSetChanged();
            String oldKey = dbHelper.updateKey(
                    getString(R.string.lastTweetKey), getLatestId());
            Log.i("TwitterGetTask", "Old latest ID " + oldKey);
        }

        @Override
        protected void onPostExecute(Long result) {
            Log.i("TwitterGetTask", "Got " + tweets.size() + " new tweets");
            TextView progress = (TextView) findViewById(R.id.progress);
            progress.setVisibility(View.GONE);
            if (tweets.size() > 0) {
                AlertDialog dialog = new AlertDialog.Builder(
                        TodoListManagerActivity.this)
                        .setTitle(getString(R.string.importTitle))
                        .setMessage(String.format(message, tweets.size()))
                        .setPositiveButton(R.string.importLbl,
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog,
                                            int which) {
                                        addTweets();
                                    }
                                })
                        .setNegativeButton(R.string.not_now,
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog,
                                            int which) {
                                        // No-op
                                    }
                                }).create();
                dialog.show();
            }
        }

        protected String getLatestId() {
            long latest;
            try {
                latest = Long.valueOf(dbHelper
                        .getPresistentData(getString(R.string.lastTweetKey)));
            } catch (NumberFormatException e) {
                latest = 0;
            }
            for (Tweet t : tweets) {
                if (Long.valueOf(t.id) > latest) {
                    latest = Long.valueOf(t.id);
                }
            }
            return String.valueOf(latest);
        }

        private String getTweets(String search) throws ClientProtocolException,
                IOException {
            Log.d("TwitterGetTask", "Twitter query is " + search);
            StringBuilder tweetsBuilder = new StringBuilder();
            HttpClient client = new DefaultHttpClient();
            HttpGet tweetsGet = new HttpGet(search);
            HttpResponse response = client.execute(tweetsGet);
            StatusLine status = response.getStatusLine();
            if (status.getStatusCode() == 200) {
                HttpEntity entity = response.getEntity();
                InputStream content = entity.getContent();
                InputStreamReader contentReader = new InputStreamReader(content);
                BufferedReader buffered = new BufferedReader(contentReader);

                String line;
                while ((line = buffered.readLine()) != null) {
                    tweetsBuilder.append(line);
                }
                Log.i("TwitterGetTask",
                        "Got tweetString: " + tweetsBuilder.toString());
                return tweetsBuilder.toString();
            } else {
                throw new IOException("request status is "
                        + status.getStatusCode());
            }
        }

        private List<Tweet> parseJson() throws JSONException {
            List<Tweet> result = new ArrayList<Tweet>();
            JSONObject json = new JSONObject(tweetsString);
            JSONArray array = json.getJSONArray("results");
            for (int i = 0; i < array.length(); i++) {
                JSONObject tweetJson = array.getJSONObject(i);
                Log.d("TwitterGetTask",
                        "tweet " + i + ": " + tweetJson.toString(4));
                String text = tweetJson.getString(TWEET_TEXT);
                Tweet tweet = new Tweet(tweetJson.getString(TWEET_ID),
                        text, getDueDate(text));
                result.add(tweet);
            }
            return result;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            // TODO Auto-generated method stub
            super.onProgressUpdate(values);
        }

        @Override
        protected void onPreExecute() {
            TextView progress = (TextView) findViewById(R.id.progress);
            progress.setVisibility(View.VISIBLE);
            super.onPreExecute();
        }

        protected Date getDueDate(String message) {
            HttpURLConnection connection = null;
            try {
                URL url = new URL(
                        "http://napi.maluuba.com/v0/normalize?&type=daterange&" 
                        + "apikey=" + getString(R.string.maluubaKey) 
                        + "&phrase=" + Uri.encode(message));
                StringBuilder result = new StringBuilder();
                connection = (HttpURLConnection) url.openConnection();
                BufferedReader in = new BufferedReader(
                        new InputStreamReader(connection.getInputStream()));
                String line;
                while ((line = in.readLine()) != null) {
                    result.append(line);
                }
                in.close();
                Log.d("TwitterGetTask", "Maluuba response " + result.toString());
                JSONObject json = new JSONObject(result.toString());
                JSONObject entities = json.getJSONObject("entities");
                JSONArray dates = entities.getJSONArray("dateRange");
                String endDate = dates.getJSONObject(0).getString("end");
                DateFormat formater = new SimpleDateFormat("yyyy-MM-dd");
                Date date = formater.parse(endDate);
                return date;
            } catch (Exception e) {
                Log.e("TwitterGetTask", "Exception while parsing date", e);
                return null;
            } finally {
                connection.disconnect();
            }
        }

    }
}
