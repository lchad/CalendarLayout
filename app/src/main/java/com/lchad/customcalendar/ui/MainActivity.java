package com.lchad.customcalendar.ui;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.lchad.customcalendar.R;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getSimpleName();

    private CalendarFragment fragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        fragment = new CalendarFragment();
        getFragmentManager().beginTransaction().add(R.id.container, fragment, TAG).commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_switch) {
            fragment.switchMonthAndWeek();
        }
        if (item.getItemId() == R.id.action_today) {
            Toast.makeText(MainActivity.this, "toady", Toast.LENGTH_SHORT).show();
        }
        return super.onOptionsItemSelected(item);
    }
}