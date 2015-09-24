package io.github.kylelam.funfactsg;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

import java.util.HashSet;
import java.util.Set;

public class HistoryActivity extends AppCompatActivity {
    static final String PREFS_HISTORY = "history";
    Set history;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);


        // Restore preferences
        SharedPreferences historySharedPreferences = getSharedPreferences(PREFS_HISTORY, 0);
        history = historySharedPreferences.getStringSet(PREFS_HISTORY, null);
        if (history==null){
            Log.e("result", "null");
            history = new HashSet<String>();
        }

        String[] historyQuestions = (String [])history.toArray( new String[history.size()]);

        for (int i=0; i < history.size(); i++){
            historyQuestions[i]= historyQuestions[i].substring(0, historyQuestions[i].indexOf('`') );
        }

        ArrayAdapter<String> historyListAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, historyQuestions);
        ListView historyListView = (ListView)findViewById(R.id.listView);
        historyListView.setAdapter(historyListAdapter);

        // ListView Item Click Listener
        historyListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {

                // ListView Clicked item index
                int itemPosition = position;

                // ListView Clicked item value
                //String itemValue = (String) listView.getItemAtPosition(position);

                // Show Alert
                /*Toast.makeText(getApplicationContext(),
                        "Position :"+itemPosition+"  ListItem : " +itemValue , Toast.LENGTH_LONG)
                        .show();
*/
                String[] historyQuestions = (String [])history.toArray( new String[history.size()]);

                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                String question = historyQuestions[itemPosition].substring(0, historyQuestions[itemPosition].indexOf('`'));
                intent.putExtra("question", question);
                String answer = historyQuestions[itemPosition].substring(historyQuestions[itemPosition].indexOf('`') +1);
                intent.putExtra("answer", answer);

                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }

        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_history, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
