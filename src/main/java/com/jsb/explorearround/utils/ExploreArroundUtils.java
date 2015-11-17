package com.jsb.explorearround.utils;

import android.os.AsyncTask;

/**
 * Created by JSB on 10/18/15.
 */
public class ExploreArroundUtils {

    /**
     * Run {@code r} on a worker thread, returning the AsyncTask
     *
     * @return the AsyncTask; this is primarily for use by unit tests, which
     * require the result of the task
     */
    public static AsyncTask<Void, Void, Void> runAsync(final Runnable r) {
        return new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                r.run();
                return null;
            }
        }.execute();
    }
}
