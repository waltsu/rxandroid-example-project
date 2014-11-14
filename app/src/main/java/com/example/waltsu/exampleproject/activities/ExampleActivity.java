package com.example.waltsu.exampleproject.activities;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.example.waltsu.exampleproject.R;
import com.example.waltsu.exampleproject.communication.Client;

import butterknife.ButterKnife;
import butterknife.InjectView;
import rx.android.schedulers.AndroidSchedulers;
import rx.util.functions.Action1;
import rx.util.functions.Func1;


public class ExampleActivity extends Activity {

    public static final String LOG_TAG = ExampleActivity.class.getCanonicalName();
    @InjectView(R.id.contentView) TextView contentView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_example);
        ButterKnife.inject(this);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.example, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.v(LOG_TAG, "Starting request");
        Client.getInstance().getAsString(this, "http://google.com")
            .observeOn(AndroidSchedulers.mainThread()) // Modify UI elements only in UI thread
            .map(new Func1<String, String>() { // FRP is amazing
                @Override
                public String call(String response) {
                    if (response.length() > 100) return response.substring(0, 99);
                    return response;
                }
            })
            .subscribe(new Action1<String>() {
                @Override
                public void call(String responseAsString) {
                    Log.v(LOG_TAG, "Got response: " + responseAsString);
                    contentView.setText(responseAsString);
                }
            }, new Action1<Throwable>() {
                @Override
                public void call(Throwable throwable) {
                    Log.v(LOG_TAG, "Oops.. Failed. Reason: ", throwable);
                }
            });
    }
}
