package io.github.kylelam.funfactsg;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.TextView;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;


public class MainActivity extends AppCompatActivity {
    WebView myWebView;
    String question;
    String answer;
    Button answerButton;
    Button nextButton;
    TextView questionTextView;
    TextView answerTextView;
    static final String STATE_CURRENT_QUESTION = "currentQuestion";
    static final String STATE_CURRENT_ANSWER = "currentAnswer";
    static final String STATE_ANSWER_BUTTON = "answerButton";
    static final String PREFS_HISTORY = "history";
    Set history;

    public void onHistoryButtonClicked(View view){
        // We need an Editor object to make preference changes.
        // All objects are from android.context.Context
        SharedPreferences settings = getSharedPreferences(PREFS_HISTORY, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putStringSet(PREFS_HISTORY, history);

        // Commit the edits!
        editor.commit();

        Intent intent = new Intent(this, HistoryActivity.class);
        startActivity(intent);

    }

    public void onAnswerButtonClicked(View view) {
        answerButton.setVisibility(View.GONE);
        answerTextView.setVisibility(View.VISIBLE);
    }

    public void onNextButtonClicked(View view) {
        nextButton.setEnabled(false);
        nextQuestion();
        Log.e("result", "onNextButtonClicked");
    }

    private class LoadListener{

        @JavascriptInterface
        public void processHTML(boolean isQuestion, String html)
        {
            if (isQuestion){
                //Log.e("result", "question: "+html);
                question=html;
            } else {
                answer=html;


            }
            //Log.e("result", tag+": "+html);
        }
    }

    private void getAnswerFromWebview(){
        myWebView.loadUrl("javascript:window.HTMLOUT.processHTML(true, document.evaluate(\"//p[text() = 'Ask another question']/../../div[1]\", document, null, XPathResult.FIRST_ORDERED_NODE_TYPE, null).singleNodeValue.innerHTML);");
        myWebView.loadUrl("javascript:window.HTMLOUT.processHTML(false, document.evaluate(\"//p[text() = 'Ask another question']/../../div[2]/div[1]\", document, null, XPathResult.FIRST_ORDERED_NODE_TYPE, null).singleNodeValue.innerHTML);");
    }

    private void nextQuestion(){
        //get question from the webview, set it on screen and reload the page.
        getAnswerFromWebview();

        setAnswer();
        myWebView.loadUrl("https://www.google.com/search?q=fun+facts");
    }

    private void setAnswer(){
        if (question.equals("")){
            question = "No internet connection?";
            answer = "Seems like it. Or... something went wrong; if so a team of highly trained monkeys has been dispatched to deal with this situation.";
        } else {
            history.add(question+"`"+answer);
        }

        questionTextView.setText(question);
        answerTextView.setText(answer);

        //Log.e("result", "Q: "+question);
        //Log.e("result", "A: "+answer);
        answerTextView.setVisibility(View.INVISIBLE);
        answerButton.setVisibility(View.VISIBLE);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        Log.e("result", "onCreate");

        // Probably initialize members with default values for a new instance
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        myWebView = (WebView) findViewById(R.id.webview);
        WebSettings webSettings = myWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        myWebView.addJavascriptInterface(new LoadListener(), "HTMLOUT");

        myWebView.setWebViewClient(new WebViewClient() {
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                // do your handling codes here, which url is the requested url
                // probably you need to open that url rather than redirect:
                view.loadUrl(url);
                return false; // then it is not handled by default action
            }
        });


        myWebView.loadUrl("https://www.google.com/search?q=fun+facts");

        myWebView.setWebViewClient(new WebViewClient() {

            public void onPageFinished(WebView view, String url) {
                // page finish loading
                getAnswerFromWebview();
                nextButton.setEnabled(true);
            }
        });

        answerButton = (Button)findViewById(R.id.answerButton);
        nextButton = (Button)findViewById(R.id.nextButton);
        questionTextView = (TextView)findViewById(R.id.question);
        answerTextView = (TextView)findViewById(R.id.answer);
        if (savedInstanceState != null) {

            // Restore value of members from saved state
            questionTextView.setText(savedInstanceState.getCharSequence(STATE_CURRENT_QUESTION));
            answerTextView.setText(savedInstanceState.getCharSequence(STATE_CURRENT_ANSWER));
            if (savedInstanceState.getBoolean(STATE_ANSWER_BUTTON)){
                answerButton.setVisibility(View.GONE);
                answerTextView.setVisibility(View.VISIBLE);
            } else {

                answerButton.setVisibility(View.VISIBLE);
                answerTextView.setVisibility(View.INVISIBLE);
            }
            //mCurrentLevel = savedInstanceState.getInt(STATE_LEVEL);
        }



        // Restore preferences
        SharedPreferences historySharedPreferences = getSharedPreferences(PREFS_HISTORY, 0);
        history = historySharedPreferences.getStringSet(PREFS_HISTORY, null);
        if (history==null){
            Log.e("result", "null");
            history = new HashSet<String>();
        }

        Intent intent = getIntent();
        if (intent.hasExtra("question")){
            String selectedQuestion = intent.getStringExtra("question");
            String selectedAnswer = intent.getStringExtra("answer");

            questionTextView.setText(selectedQuestion);
            answerTextView.setText(selectedAnswer);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        // Save the user's current state
        savedInstanceState.putCharSequence(STATE_CURRENT_QUESTION, questionTextView.getText());
        savedInstanceState.putCharSequence(STATE_CURRENT_ANSWER, answerTextView.getText());

        savedInstanceState.putBoolean(STATE_ANSWER_BUTTON, (answerButton.getVisibility()==View.GONE));

        // Always call the superclass so it can save the view hierarchy state
        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    protected void onStop(){
        super.onStop();
        /*
        Iterator it=history.iterator();
        while(it.hasNext()){
            String oldStr=it.next().toString();
            Log.v("result",oldStr);
        }*/

        // We need an Editor object to make preference changes.
        // All objects are from android.context.Context
        SharedPreferences settings = getSharedPreferences(PREFS_HISTORY, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putStringSet(PREFS_HISTORY, history);

        // Commit the edits!
        editor.commit();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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
            //nextQuestion();
            //myWebView.loadUrl("javascript:window.HTMLOUT.processHTML(true, document.evaluate(\"//p[text() = 'Ask another question']/../../div[1]\", document, null, XPathResult.FIRST_ORDERED_NODE_TYPE, null).singleNodeValue.innerHTML);");

            //myWebView.loadUrl("javascript:window.HTMLOUT.processHTML(false, document.evaluate(\"//p[text() = 'Ask another question']/../../div[2]/div[1]\", document, null, XPathResult.FIRST_ORDERED_NODE_TYPE, null).singleNodeValue.innerHTML);");
            //myWebView.loadUrl("javascript:window.HTMLOUT.processHTML(document.getElementsByTagName('html')[0].innerHTML);");
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
