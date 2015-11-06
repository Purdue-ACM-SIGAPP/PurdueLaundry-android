package xyz.jhughes.laundry;

import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import xyz.jhughes.laundry.LaundryParser.Machine;

public class InformationActivity extends AppCompatActivity {

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

        ImageView imageView = (ImageView) findViewById(R.id.machineImage);
        setImage(imageView, machine);
    }

    public void setImage(ImageView imageView, Machine m) {
        if (m.getType().equals("Dryer")) {
            switch (m.getStatus()) {
                case "Available":
                    imageView.setImageResource(R.drawable.dryer_available);
                    break;
                case "In use":
                    imageView.setImageResource(R.drawable.dryer_running);
                    break;
                case "Almost done":
                    imageView.setImageResource(R.drawable.dryer_almost_done);
                    break;
                case "End of cycle":
                    imageView.setImageResource(R.drawable.dryer_end_cycle);
                    break;
                default:
                    imageView.setImageResource(R.drawable.dryer_running);
            }
        } else if (m.getType().equals("Washer")) {
            switch (m.getStatus()) {
                case "Available":
                    imageView.setImageResource(R.drawable.washer_available);
                    break;
                case "In use":
                    imageView.setImageResource(R.drawable.washer_running);
                    break;
                case "Almost done":
                    imageView.setImageResource(R.drawable.washer_almost_done);
                    break;
                case "End of cycle":
                    imageView.setImageResource(R.drawable.washer_end_cycle);
                    break;
                default:
                    imageView.setImageResource(R.drawable.washer_running);
            }
        }
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
