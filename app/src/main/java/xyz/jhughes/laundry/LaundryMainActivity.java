package xyz.jhughes.laundry;

import android.app.ActionBar;
import android.app.Activity;
import android.os.Build;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.support.v7.widget.Toolbar;

public class LaundryMainActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        if (Build.VERSION.SDK_INT >= 21)
            setTheme(android.R.style.Theme_Material);
        else
            setTheme(android.R.style.Theme_Holo);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_laundry_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        try {
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        } catch (NullPointerException e) {
            System.out.println("Action bar was null!");
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_laundry_main, menu);
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
