package com.jsb.explorearround;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.Toast;

import com.jsb.explorearround.network.Connectivity;
import com.jsb.explorearround.parser.ApiService;
import com.jsb.explorearround.parser.Model;
import com.jsb.explorearround.parser.PlaceDetailsModel;
import com.jsb.explorearround.parser.Results;
import com.jsb.explorearround.utils.PreferencesHelper;

import java.util.HashSet;

import retrofit.RestAdapter;

/**
 * Created by JSB on 10/7/15.
 */
public class Controller {

    private static final String TAG = "Controller";
    private static final String BASE_URL = "https://maps.googleapis.com";
    private static Controller sInstance;
    private final Context mContext;
    private final HashSet<Result> mListeners = new HashSet<Result>();


    protected Controller(Context context) {
        mContext = context;

    }

    /**
     * Gets or creates the singleton instance of Controller
     *
     * @param _context
     * @return
     */
    public synchronized static Controller getsInstance(Context _context) {
        if (sInstance == null) {
            sInstance = new Controller(_context);
        }
        return sInstance;
    }

    public synchronized static Controller getInstance() {
        return sInstance;
    }

    /**
     * Any UI code that wishes for callback results (on async ops) should
     * register their callback here (typically from onResume()). Unregistered
     * callbacks will never be called, to prevent problems when the command
     * completes and the activity has already paused or finished.
     *
     * @param listener The callback that may be used in action methods
     */
    public void addResultCallback(Result listener) {
        synchronized (mListeners) {
            listener.setRegistered(true);
            mListeners.add(listener);
        }
    }

    /**
     * Any UI code that no longer wishes for callback results (on async ops)
     * should unregister their callback here (typically from onPause()).
     * Unregistered callbacks will never be called, to prevent problems when the
     * command completes and the activity has already paused or finished.
     *
     * @param listener The callback that may no longer be used
     */
    public void removeResultCallback(Result listener) {
        synchronized (mListeners) {
            if (listener != null) {
                listener.setRegistered(false);
                mListeners.remove(listener);
            }
        }
    }

    /**
     * Simple callback for synchronous commands. For many commands, this can be
     * largely ignored and the result is observed via provider cursors. The
     * callback will *not* necessarily be made from the UI thread, so you may
     * need further handlers to safely make UI updates.
     */
    public static abstract class Result {
        private volatile boolean mRegistered;

        protected void setRegistered(boolean registered) {
            mRegistered = registered;
        }

        protected final boolean isRegistered() {
            return mRegistered;
        }
        //Callback for teh API needs to be implemented here

        /**
         * Callback for getInformationCallback API
         *
         * @param bundle
         * @param reason
         */
        public void getInformationCallback(Model bundle, String reason) {
        }

        /**
         * Callback for getPlaceDetailsCallback API
         *
         * @param bundle
         * @param reason
         */
        public void getPlaceDetailsCallback(PlaceDetailsModel bundle, String reason) {
        }

        /**
         * Callback for getPhotoDetailsCallback API
         *
         * @param bundle
         */
        public void getPhotoDetailsCallback(PlaceDetailsModel bundle) {
        }
    }

    /**
     * getInformation - API to get information about the requested item
     *
     * @param searchItem
     * @param radius
     * @param keyword
     */
    public void getInformation(Context context, final String searchItem,
                               final String radius, final String keyword) {
        Log.d(TAG, "getInformation");
        if (!Connectivity.isDataConnectionAvailable(mContext)) {
            Toast.makeText(mContext, "No data connection available", Toast.LENGTH_SHORT).show();
            //TODO - Return error callback
            postErrorCallBack("No Data Connectivity");
            return;
        }
        PreferencesHelper preference = PreferencesHelper.getPreferences(mContext);
        String longtitude = preference.getLongtitude();
        String latitude = preference.getLatitude();
        if (longtitude == null || latitude == null) {
            postErrorCallBack("Please wait to detect your location");
            return;
        }
        new PostTask(context, searchItem, longtitude, latitude, radius, keyword).execute();
    }

    private void postErrorCallBack(String reason) {
        synchronized (mListeners) {
            for (Result l : mListeners) {
                l.getInformationCallback(null, reason);
            }
        }
    }

    private class PostTask extends AsyncTask<Void, Void, Model> {
        RestAdapter restAdapter;

        final Context mContext;
        ProgressDialog mProgressDialog;
        String mSearchItem;
        String mKeyword;
        String mLongtitude;
        String mLatitude;
        String mRaduius;

        private PostTask(Context context, String searchItem, String longtitude, String latitude, String radius, String keyword) {
            mContext = context;
            mProgressDialog = new ProgressDialog(context, R.style.AlertDialogCustom);
            mSearchItem = searchItem;
            mLongtitude = longtitude;
            mLatitude = latitude;
            mRaduius = radius;
            mKeyword = keyword;
        }

        @Override
        protected void onPreExecute() {

            mProgressDialog.setTitle("Please wait");
            mProgressDialog.setCancelable(false);
            mProgressDialog.setIndeterminate(true);
            mProgressDialog.setMessage("Downloading...");
            mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            mProgressDialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
                public boolean onKey(DialogInterface info, int keycode, KeyEvent keyevent) {
                    return false;
                }
            });
            mProgressDialog.show();

            restAdapter = new RestAdapter.Builder()
                    .setEndpoint(BASE_URL)
                    .setLogLevel(RestAdapter.LogLevel.FULL)
                    .setLog(new RestAdapter.Log() {
                        @Override
                        public void log(String msg) {
                            Log.i(TAG, msg);
                        }
                    }).build();
        }

        @Override
        protected Model doInBackground(Void... params) {
            ApiService methods = restAdapter.create(ApiService.class);
            Model posts = null;
            try {
                String location = mLatitude + "," + mLongtitude;
                posts = methods.getSearchResults(location, /*mRaduius*/ mSearchItem, mKeyword,
                        "AIzaSyC9OpeSXAjldUge51N0PPjg5DajOMGNTog", /*"true",*/ "distance");
            } catch (Exception e) {
                e.printStackTrace();
            }
            return posts;
        }

        @Override
        protected void onPostExecute(Model posts) {
            mProgressDialog.dismiss();
            Log.e(TAG, "Status " + (posts.getStatus() != null));
            //Log.e(TAG, "html_attributes " + posts.getHtml_attributions());
            Results[] results = posts.getResults();
            Log.e(TAG, "Results " + results);
            if (results.length > 0) {
                Log.e(TAG, "Name " + results[0].getName());
                Log.e(TAG, "Lat " + results[0].getGeometry().getLocation().getLatitude());
                Log.e(TAG, "Long " + results[0].getGeometry().getLocation().getLongtitude());
                Log.e(TAG, "icon " + results[0].getIcon());
                Log.e(TAG, "id " + results[0].getId());
                //Log.e(TAG, "open_now " + results[0].getOpening_hours().getOpen_now());
                Log.e(TAG, "Vicinity " + results[0].getVicinity());
            }

            //Bundle respBundle = new Bundle();
            //respBundle.putStringArrayList("information", posts);
            synchronized (mListeners) {
                for (Result l : mListeners) {
                    l.getInformationCallback(posts, null);
                }
            }
        }
    }


    /**
     * getPhotoDetails - API to get information about the requested item
     *
     * @param photo_id
     */
    public void getPhotoDetails(Context context, final String photo_id, final String maxwidth) {
        Log.d(TAG, "getPhotoDetails");
        if (!Connectivity.isDataConnectionAvailable(mContext)) {
            Toast.makeText(mContext, "No data connection available", Toast.LENGTH_SHORT).show();
            //TODO - Return error callback
            return;
        }
        new getPhotoDetailsTask(context, photo_id, maxwidth).execute();
    }

    private class getPhotoDetailsTask extends AsyncTask<Void, Void, PlaceDetailsModel> {
        RestAdapter restAdapter;

        final Context mContext;
        ProgressDialog mProgressDialog;
        String mMaxWidth;
        String mPhotoId;

        private getPhotoDetailsTask(Context context, String photo_id, String maxwidth) {
            mContext = context;
            mProgressDialog = new ProgressDialog(context, R.style.AlertDialogCustom);
            mPhotoId = photo_id;
            mMaxWidth = maxwidth;
        }

        @Override
        protected void onPreExecute() {

            mProgressDialog.setTitle("Please wait");
            mProgressDialog.setCancelable(false);
            mProgressDialog.setIndeterminate(true);
            mProgressDialog.setMessage("Downloading...");
            //mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            mProgressDialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
                public boolean onKey(DialogInterface info, int keycode, KeyEvent keyevent) {
                    return false;
                }
            });
            mProgressDialog.show();

            restAdapter = new RestAdapter.Builder()
                    .setEndpoint(BASE_URL)
                    .setLogLevel(RestAdapter.LogLevel.FULL)
                    .setLog(new RestAdapter.Log() {
                        @Override
                        public void log(String msg) {
                            Log.i(TAG, msg);
                        }
                    }).build();
        }

        @Override
        protected PlaceDetailsModel doInBackground(Void... params) {
            ApiService methods = restAdapter.create(ApiService.class);
            PlaceDetailsModel posts = null;
            try {
                posts = methods.getPhotoDetails(mMaxWidth, mPhotoId, "AIzaSyC9OpeSXAjldUge51N0PPjg5DajOMGNTog");
            } catch (Exception e) {
                e.printStackTrace();
            }
            return posts;
        }

        @Override
        protected void onPostExecute(PlaceDetailsModel posts) {
            mProgressDialog.dismiss();
            Log.e(TAG, "Status " + (posts.getStatus() != null));
            com.jsb.explorearround.parser.Result result = posts.getResult();
            //Bundle respBundle = new Bundle();
            //respBundle.putStringArrayList("information", posts);
            synchronized (mListeners) {
                for (Result l : mListeners) {
                    l.getPhotoDetailsCallback(posts);
                }
            }
        }
    }

    /**
     * getPlaceDetails - API to get information about the requested item
     *
     * @param place_id
     */
    public void getPlaceDetails(Context context, final String place_id) {
        Log.d(TAG, "getPlaceDetails");
        if (!Connectivity.isDataConnectionAvailable(mContext)) {
            Toast.makeText(mContext, "No data connection available", Toast.LENGTH_SHORT).show();
            postPlaceDetailsErrorCallBack("No Data Connection Availble");
            return;
        }
        new getPlaceDetailsTask(context, place_id).execute();
    }

    private void postPlaceDetailsErrorCallBack(String reason) {
        synchronized (mListeners) {
            for (Result l : mListeners) {
                l.getPlaceDetailsCallback(null, reason);
            }
        }
    }

    private class getPlaceDetailsTask extends AsyncTask<Void, Void, PlaceDetailsModel> {
        RestAdapter restAdapter;

        final Context mContext;
        ProgressDialog mProgressDialog;
        String mPlaceId;

        private getPlaceDetailsTask(Context context, String place_id) {
            mContext = context;
            mProgressDialog = new ProgressDialog(context, R.style.AlertDialogCustom);
            mPlaceId = place_id;
        }

        @Override
        protected void onPreExecute() {

            mProgressDialog.setTitle("Please wait");
            mProgressDialog.setCancelable(false);
            mProgressDialog.setIndeterminate(true);
            mProgressDialog.setMessage("Downloading...");
            mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            mProgressDialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
                public boolean onKey(DialogInterface info, int keycode, KeyEvent keyevent) {
                    return false;
                }
            });
            mProgressDialog.show();

            restAdapter = new RestAdapter.Builder()
                    .setEndpoint(BASE_URL)
                    .setLogLevel(RestAdapter.LogLevel.FULL)
                    .setLog(new RestAdapter.Log() {
                        @Override
                        public void log(String msg) {
                            Log.i(TAG, msg);
                        }
                    }).build();
        }

        @Override
        protected PlaceDetailsModel doInBackground(Void... params) {
            ApiService methods = restAdapter.create(ApiService.class);
            PlaceDetailsModel posts = null;
            try {
                posts = methods.getPlaceDetails(mPlaceId, "AIzaSyC9OpeSXAjldUge51N0PPjg5DajOMGNTog");
            } catch (Exception e) {
                e.printStackTrace();
            }
            return posts;
        }

        @Override
        protected void onPostExecute(PlaceDetailsModel posts) {
            mProgressDialog.dismiss();
            Log.e(TAG, "Status " + (posts.getStatus() != null));
            //Log.e(TAG, "html_attributes " + posts.getHtml_attributions());
            com.jsb.explorearround.parser.Result result = posts.getResult();


            Log.e(TAG, "Name " + result.getName());
            Log.e(TAG, "Lat " + result.getGeometry().getLocation().getLatitude());
            Log.e(TAG, "Long " + result.getGeometry().getLocation().getLongtitude());
            Log.e(TAG, "Phone number " + result.getInternational_phone_number());
            Log.e(TAG, "rating " + result.getRating());
            Log.e(TAG, "website " + result.getWebsite());
            Log.e(TAG, "Vicinity " + result.getVicinity());
            Log.e(TAG, "url " + result.getUrl());
            Log.e(TAG, "scope " + result.getScope());


            //Bundle respBundle = new Bundle();
            //respBundle.putStringArrayList("information", posts);
            synchronized (mListeners) {
                for (Result l : mListeners) {
                    l.getPlaceDetailsCallback(posts, null);
                }
            }
        }
    }
}
