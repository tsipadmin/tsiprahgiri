package com.tstracker.tstracker;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

public class about extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        TextView txtAbout = (TextView) findViewById(R.id.txtAbout);
        DatabaseHelper dh = new DatabaseHelper(this);
        SQLiteDatabase db;
        try {
            db = dh.getReadableDatabase();
            String[] columns = {DatabaseContracts.Settings.COLUMN_NAME_ID,
                    DatabaseContracts.Settings.COLUMN_NAME_tell,
                    DatabaseContracts.Settings.COLUMN_NAME_logo,
                    DatabaseContracts.Settings.COLUMN_NAME_site};
            Cursor c = db.query(DatabaseContracts.Settings.TABLE_NAME, columns, "", null, "", "", "");
            c.moveToFirst();
            long itemId = 0;
            try {
                itemId = c.getLong(c.getColumnIndexOrThrow(DatabaseContracts.Settings.COLUMN_NAME_ID));
                txtAbout.setText(c.getString(c.getColumnIndexOrThrow(DatabaseContracts.Settings.COLUMN_NAME_site)) +
                        c.getString(c.getColumnIndexOrThrow(DatabaseContracts.Settings.COLUMN_NAME_tell)) +
                        c.getString(c.getColumnIndexOrThrow(DatabaseContracts.Settings.COLUMN_NAME_logo)));

            } catch (Exception er) {
            }
        } catch (Exception er) {
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_about, menu);
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
