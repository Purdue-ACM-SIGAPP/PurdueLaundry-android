package xyz.jhughes.laundry;

import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import xyz.jhughes.laundry.LaundryParser.Machine;

public class InfromationActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_infromation);

        Machine machine = (Machine) getIntent().getSerializableExtra("machine");

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(machine.getName());

        setSupportActionBar(toolbar);

        final ActionBar actionBar = getSupportActionBar();
        //We can't really do anything if the ActionBar is null.
        //This should never happen though, so we shouldn't worry too much about it.
        //Maybe someone can find a case where this does happen and we can fix it.
        if (actionBar == null) {
            return;
        }

        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_infromation, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        finish();

        return super.onOptionsItemSelected(item);
    }
}
