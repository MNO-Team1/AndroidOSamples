package com.jsb.explorearround.ui;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.jsb.explorearround.R;
import com.jsb.explorearround.parser.Location;
import com.jsb.explorearround.parser.Result;
import com.jsb.explorearround.parser.Reviews;
import com.jsb.explorearround.utils.PreferencesHelper;

import java.text.DecimalFormat;
import java.util.Arrays;

/**
 * Created by JSB on 10/24/15.
 */


public class DetailsActivity extends AppCompatActivity implements OnMapReadyCallback, View.OnClickListener {

    private static final String TAG = "DetailsActivity";
    private static Result mResults = null;
    private TextView mWebAddress;
    private TextView mPhoneNumber;
    private TextView mVicinity;
    private TextView mRating;
    private TextView mName;
    private TextView mReviews;
    private TextView mDistance;
    private ImageView mImage1;
    private ImageView mImage2;
    private ImageView mImage3;
    private ImageView mImage4;
    private static String mPhotoRef;
    private static String mPhotoWidth;
    private CollapsingToolbarLayout collapsingToolbarLayout;
    private ImageView image;
    private static String URL="https://maps.googleapis.com/maps/api/place/photo?";
    private Toolbar toolbar;
    private RatingBar mRatingBar;
    private LinearLayout mLinear1;
    private LinearLayout mLinear2;
    private LinearLayout mLinear3;
    private LinearLayout mLinear4;

    private LinearLayout mWebLayout;
    private LinearLayout mPhoneLayout;
    private LinearLayout mRatingBarLayout;

    private LinearLayout mOpenHoursLayout; //open_hours_layout;
    private TextView mOpenHourText; //open_status_text;
    private Button mUpDownButton; //updown_btn;
    private LinearLayout mWeekDayItemLayout; //weekday_item_layout;
    private TextView mWeekdayStatus; //weekday_text;
    private TextView mWeekdayHours; //weekday_hours;


    public static void actionLaunchResultsActivity(Activity fromActivity, Result res, String photoWidth, String photoRef) {
        Intent i = new Intent(fromActivity, DetailsActivity.class);
        fromActivity.startActivity(i);
        mResults = res;
        mPhotoRef = photoRef;
        mPhotoWidth = photoWidth;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate");
        setContentView(R.layout.activity_details);
        mWebAddress = (TextView) findViewById(R.id.weblink);
        mPhoneNumber = (TextView) findViewById(R.id.phone);
        mVicinity = (TextView) findViewById(R.id.vicinity);
        mRating = (TextView) findViewById(R.id.rating);
        mName = (TextView) findViewById(R.id.name);
        mReviews = (TextView) findViewById(R.id.reviews);
        mDistance = (TextView) findViewById(R.id.distance);
        mImage1 = (ImageView) findViewById(R.id.imageView1);
        mImage2 = (ImageView) findViewById(R.id.imageView2);
        mImage3 = (ImageView) findViewById(R.id.imageView3);
        mImage4 = (ImageView) findViewById(R.id.imageView4);
        mRatingBar = (RatingBar) findViewById(R.id.ratingBar);
        mLinear1 = (LinearLayout) findViewById(R.id.Linear1);
        mLinear2 = (LinearLayout) findViewById(R.id.Linear2);
        mLinear3 = (LinearLayout) findViewById(R.id.Linear3);
        mLinear4 = (LinearLayout) findViewById(R.id.Linear4);

        mOpenHoursLayout = (LinearLayout) findViewById(R.id.open_hours_layout);
        mOpenHourText = (TextView) findViewById(R.id.open_status_text);
        mUpDownButton = (Button) findViewById(R.id.updown_btn);
        mUpDownButton.setOnClickListener(this);
        mWeekDayItemLayout = (LinearLayout) findViewById(R.id.weekday_item_layout);

        mWeekdayStatus =(TextView) findViewById(R.id.weekday_text);
        mWeekdayHours =(TextView) findViewById(R.id.weekday_hours);

        mWebLayout = (LinearLayout)findViewById(R.id.weblayout);
        mPhoneLayout = (LinearLayout)findViewById(R.id.phonelayout);
        mRatingBarLayout = (LinearLayout)findViewById(R.id.ratingBarLayout);

        mImage1.setOnClickListener(this);
        mImage2.setOnClickListener(this);
        mImage3.setOnClickListener(this);
        mLinear1.setOnClickListener(this);
        mLinear2.setOnClickListener(this);
        mLinear3.setOnClickListener(this);
        mLinear4.setOnClickListener(this);

        if (mResults.getWebsite() != null) {
            mWebAddress.setText(mResults.getWebsite());
        } else {
            mWebLayout.setVisibility(View.GONE);
        }

        if (mResults.getInternational_phone_number() != null) {
            mPhoneNumber.setText(mResults.getInternational_phone_number());
        } else {
            mPhoneLayout.setVisibility(View.GONE);
        }

        mVicinity.setText(mResults.getVicinity());
        mRating.setText(mResults.getRating());
        mName.setText(mResults.getName());

        String rating = mResults.getRating();
        if (rating != null) {
            mRatingBar.setRating(Float.valueOf(mResults.getRating()));
            Reviews[] reviews = mResults.getReviews();
            if (reviews != null) {
                String count = String.valueOf(reviews.length) + " reviews";
                mReviews.setText(count);

                mDistance.setText(" - " + calculateDst(mResults.getGeometry().getLocation()));
            } else {
                mReviews.setEnabled(false);
            }

        } else {
            mRatingBar.setEnabled(false);
            mRatingBarLayout.setVisibility(View.GONE);
        }

        if (mResults.getOpening_hours() == null) {
            mOpenHoursLayout.setVisibility(View.GONE);
        }

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        //setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        collapsingToolbarLayout.setTitle(mResults.getName());
        collapsingToolbarLayout.setExpandedTitleColor(getResources().getColor(android.R.color.transparent));
        collapsingToolbarLayout.setContentScrimColor(this.getResources().getColor(R.color.color_primary));
        collapsingToolbarLayout.setStatusBarScrimColor(this.getResources().getColor(R.color.color_primary));
        //setPalette();

        toolbar = (Toolbar) findViewById(R.id.tool_bar); // Attaching the layout to the toolbar object
        toolbar.setTitle(mResults.getName());
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        //setSupportActionBar(toolbar);

//        String buildURL = URL +"maxwidth=" + mPhotoWidth + "&photoreference=" +
//                mPhotoRef + "&key=AIzaSyC9OpeSXAjldUge51N0PPjg5DajOMGNTog" ;
//        Picasso.with(this)
//                .load(buildURL)
//                .into(mImage);
    }

    private String calculateDst(Location location) {
        String ret = null;

        PreferencesHelper preference = PreferencesHelper.getPreferences(this);
        android.location.Location srcLoc = new android.location.Location("source");
        srcLoc.setLatitude(Double.valueOf( preference.getLatitude()));
        srcLoc.setLongitude(Double.valueOf( preference.getLongtitude()));


        android.location.Location dstLoc = new android.location.Location("destination");
        dstLoc.setLatitude(Double.valueOf(location.getLatitude()));
        dstLoc.setLongitude(Double.valueOf(location.getLongtitude()));

        Float dst = (srcLoc.distanceTo(dstLoc)/1000);
        Double miles = dst * 0.621;
        //ret = NumberFormat.getInstance().format(miles);


        DecimalFormat df = new DecimalFormat();
        df.setMaximumFractionDigits(1);
        ret = df.format(miles);
        ret = ret + " miles";
        return ret;
    }

    private void shrinkOpenHoursLayout() {
        if (mWeekDayItemLayout.getVisibility() == View.VISIBLE) {
            shrinkOpenHoursLayout(true);
        } else {
            shrinkOpenHoursLayout(false);
        }
    }

    private void shrinkOpenHoursLayout(boolean shrink) {
        if (shrink) {
            if (mUpDownButton == null) {
                mUpDownButton = (Button) mOpenHoursLayout.findViewById(R.id.updown_btn);
            }
            mUpDownButton.setBackground(getResources().getDrawable(R.drawable.arrow_up));
            mWeekDayItemLayout.setVisibility(View.GONE);
        } else { /* Spread mode */
            if (mUpDownButton == null) {
                mUpDownButton = (Button) mOpenHoursLayout.findViewById(R.id.updown_btn);
            }
            mUpDownButton.setBackground(getResources().getDrawable(R.drawable.arrow_down));
            mWeekDayItemLayout.setVisibility(View.VISIBLE);
            if (mResults != null && mResults.getOpening_hours() != null) {
                String[] weekStatus = mResults.getOpening_hours().getWeekday_text();
                StringBuffer days = new StringBuffer();
                StringBuffer time = new StringBuffer();
                int len = weekStatus.length;
                for (int i=0; i < len; i++) {
                    String[] split = weekStatus[i].split(": ");
                    String[] dualTime = split[1].split(",");
                    if (dualTime.length > 1) {
                        time.append(dualTime[0] + "\n" + dualTime[1] + "\n");
                        days.append(split[0] + "\n" + "\n");
                    } else {
                        time.append(split[1] + "\n");
                        days.append(split[0] + "\n");
                    }
                }
                mWeekdayHours.setText(time.toString());
                mWeekdayStatus.setText(days.toString());
            }
        }
    }

    private void setPalette() {
        Bitmap bitmap = ((BitmapDrawable) image.getDrawable()).getBitmap();
        Palette.from(bitmap).generate(new Palette.PaletteAsyncListener() {
            @Override
            public void onGenerated(Palette palette) {
                int primaryDark = getResources().getColor(R.color.primary_dark);
                int primary = getResources().getColor(R.color.primary);
                collapsingToolbarLayout.setContentScrimColor(palette.getMutedColor(primary));
                collapsingToolbarLayout.setStatusBarScrimColor(palette.getDarkVibrantColor(primaryDark));
            }
        });

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
    }

    @Override
    public void onMapReady(GoogleMap map) {
        // Add a marker in Sydney, Australia, and move the camera.
        LatLng location = new LatLng(Double.valueOf(mResults.getGeometry().getLocation().getLatitude()),
                Double.valueOf(mResults.getGeometry().getLocation().getLongtitude()));
        map.addMarker(new MarkerOptions().position(location).title(mResults.getName()));
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 10));
        map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == android.R.id.home) {
            Log.d(TAG, "action bar clicked");
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        final int id = v.getId();
        switch (id) {
            case R.id.updown_btn:
                shrinkOpenHoursLayout();
                break;
            case R.id.Linear1://Call
                try {
                    String phone = mResults.getInternational_phone_number();
                    if (phone != null) {
                        String uri = "tel:" + mResults.getInternational_phone_number();
                        Intent dialIntent = new Intent(Intent.ACTION_CALL, Uri.parse(uri));
                        startActivity(dialIntent);
                    } else {
                        Toast.makeText(this, "No valid phoen number found", Toast.LENGTH_LONG).show();
                    }
                } catch (ActivityNotFoundException e) {
                    Toast.makeText(this, "No application can handle this request.",
                            Toast.LENGTH_LONG).show();
                    e.printStackTrace();
                }
                break;
            case R.id.Linear2://Direction
                try {
                    Intent intent = new Intent(android.content.Intent.ACTION_VIEW, Uri.parse("google.navigation:q="+
                            String.valueOf(mResults.getGeometry().getLocation().getLatitude()) + "," +
                            String.valueOf(mResults.getGeometry().getLocation().getLongtitude())));
                    intent.setClassName("com.google.android.apps.maps", "com.google.android.maps.MapsActivity");
                    startActivity(intent);
                } catch (ActivityNotFoundException e) {
                    Toast.makeText(this, "No application can handle this request.",
                            Toast.LENGTH_LONG).show();
                    e.printStackTrace();
                }
                break;
            case R.id.Linear3://Share
                try {
                    Intent share = new Intent(Intent.ACTION_SEND);
                    share.setType("text/plain");
                    String web = "";
                    if (mResults.getWebsite() != null) {
                        web = mResults.getWebsite();
                    }
                    String shareText = mResults.getName() + "\n" + mResults.getVicinity() + "\n" +
                            mResults.getInternational_phone_number() + "\n" +
                             web + "\n" + mResults.getGeometry().getLocation().getLatitude() +
                            "," + mResults.getGeometry().getLocation().getLongtitude();
                    share.putExtra(Intent.EXTRA_TEXT, shareText);
                    startActivity(Intent.createChooser(share, "Share Information"));
                } catch (ActivityNotFoundException e) {
                    Toast.makeText(this, "No application can handle this request.",
                            Toast.LENGTH_LONG).show();
                    e.printStackTrace();
                }
                break;
            case R.id.Linear4://Website
                try {
                    String web = mResults.getWebsite();
                    if (web != null) {
                        Intent myIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(web));
                        startActivity(myIntent);
                    } else {
                        Toast.makeText(this, "No website address found", Toast.LENGTH_LONG).show();
                    }
                } catch (ActivityNotFoundException e) {
                    Toast.makeText(this, "No application can handle this request."
                            + " Please install a web browser", Toast.LENGTH_LONG).show();
                    e.printStackTrace();
                }
                break;
        }
    }
}
