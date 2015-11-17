package com.jsb.explorearround.ui;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.jsb.explorearround.Controller;
import com.jsb.explorearround.R;
import com.jsb.explorearround.location.LocationTracker;
import com.jsb.explorearround.parser.Model;
import com.jsb.explorearround.utils.PreferencesHelper;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private static final String RADIUS = "50000";
    private ControllerResults mControllerCallback;
    public static String[] prgmDisplayNameList = {
            "ATM",
            "Bar",
            "Beauty Salon",
            "Cafe",
            "Car Wash",
            "Gas station",
            "Restaurant",
            "RV Parks",
            "Car parking",
            "Movie theatre",
            "Departmental store",
            "Parks",
            "Hindu temple",
            "Mosque",
            "Church"
    };
    public static String[] prgmNameList = {
            "atm",
            "bar",
            "beauty_salon",
            "cafe",
            "car_wash",
            "gas_station",
            "restaurant",
            "rv_park",
            "parking",
            "movie_theatre",
            "grocery_or_supermarket",
            "parks",
            "hindu_temple",
            "mosque",
            "church"
    };
    public static String[] prgmKeyword = {
            "ATM",
            "wine",
            "beauty|spa",
            "coffee",
            "car|clean",
            "gas|motor",
            "restaurant",
            "rv",
            "parking",
            "cinemas",
            "",
            "parks",
            "hindu_temple",
            "mosque",
            "church"
    };

    public static int[] prgmImages = {
            R.drawable.atm,
            R.drawable.bar,
            R.drawable.beauty,
            R.drawable.cafe,
            R.drawable.car_wash,
            R.drawable.gas,
            R.drawable.resturant,
            R.drawable.rv,
            R.drawable.parking,
            R.drawable.movies,
            R.drawable.groceries,
            R.drawable.parks,
            R.drawable.temple,
            R.drawable.mosque,
            R.drawable.church
    };
    private Toolbar toolbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d(TAG, "onCreate");

        toolbar = (Toolbar) findViewById(R.id.tool_bar); // Attaching the layout to the toolbar object
        setSupportActionBar(toolbar);

        //Initialise the Controller from here
        mControllerCallback = new ControllerResults();
        Controller.getsInstance(this).addResultCallback(mControllerCallback);

        GridView gridview = (GridView) findViewById(R.id.gridView);
        gridview.setAdapter(new CustomAdapter(this, prgmNameList, prgmDisplayNameList, prgmImages, prgmKeyword));

        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {
            }
        });
        if (LocationTracker.isLocationEnabled(MainActivity.this)) {
            PreferencesHelper preference = PreferencesHelper.getPreferences(this);
            if (preference.getLatitude() == null && preference.getLongtitude() == null) {
                new LocationTracker(this).getLocation();
            }
        }

    }

    /**
     * Controller result Callback.
     */
    private class ControllerResults extends Controller.Result {

        @Override
        public void getInformationCallback(final Model status, final String reason) {
            Log.d(TAG, "getInformationCallback");
            runOnUiThread(new Runnable() {
                public void run() {
                    if (status == null) {
                        Toast.makeText(MainActivity.this, reason, Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (status.getResults().length > 0) {
                        ResultsActivity.actionLaunchResultsActivity(MainActivity.this, status);
                    } else {
                        Toast.makeText(MainActivity.this, "No search results found", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "onPause");
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy");
        Controller.getInstance().removeResultCallback(mControllerCallback);
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
            SettingsActivity.actionLaunchSettings(this);
            return true;
        }

        if (id == R.id.action_about) {
            AboutActivity.actionAboutActivity(this);
            return true;
        }

        if (id == R.id.action_rate_us) {
            final Uri marketUri = Uri.parse("market://details?id=" + "com.samsung.mno.zenreport");
            startActivity(new Intent(Intent.ACTION_VIEW, marketUri));
            return true;
        }

        if (id == R.id.action_mail_us) {
            try {
                Intent share = new Intent(Intent.ACTION_SEND);
                share.setType("text/html");
                String shareText = "Test";
                share.putExtra(Intent.EXTRA_TEXT, shareText);
                share.putExtra(Intent.EXTRA_EMAIL, "jayantheesh.sb@gmail.com");
                share.putExtra(Intent.EXTRA_SUBJECT, "Explore Arround");
                startActivity(Intent.createChooser(share, "Send Email"));
            } catch (ActivityNotFoundException e) {
                Toast.makeText(this, "No application can handle this request.",
                        Toast.LENGTH_LONG).show();
                e.printStackTrace();
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public class CustomAdapter extends BaseAdapter {

        String[] names;
        String[] displayNames;
        Context context;
        int[] imageId;
        String[] keywords;
        private LayoutInflater inflater = null;

        public CustomAdapter(MainActivity mainActivity, String[] prgmNameList,
                             String[] prgmDisplayNameList, int[] prgmImages,
                             String[] prgmKeywordList) {
            names = prgmNameList;
            displayNames = prgmDisplayNameList;
            context = mainActivity;
            imageId = prgmImages;
            keywords = prgmKeywordList;
            inflater = (LayoutInflater) context.
                    getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        }

        @Override
        public int getCount() {
            return names.length;
        }

        @Override
        public Object getItem(int position) {
            return position;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        public class Holder {
            TextView tv;
            ImageView img;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            Holder holder = new Holder();
            View rowView;

            rowView = inflater.inflate(R.layout.gridview_holder, null);
            holder.tv = (TextView) rowView.findViewById(R.id.textView1);
            holder.img = (ImageView) rowView.findViewById(R.id.imageView1);

            holder.tv.setText(displayNames[position]);
            holder.img.setImageResource(imageId[position]);
            rowView.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    // check if GPS enabled
                    if (LocationTracker.isLocationEnabled(MainActivity.this)) {
                        //Invoke the Controller API to get the response back from Google server
                        Controller.getInstance().getInformation(MainActivity.this, names[position],
                                RADIUS, keywords[position]);
                    }
                }
            });
            return rowView;
        }

    }
}