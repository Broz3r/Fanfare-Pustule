package org.pustule.fanfarepustule.activities;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.TextView;
import android.widget.ViewFlipper;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.extensions.android.gms.auth.GooglePlayServicesAvailabilityIOException;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.CalendarScopes;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.Events;
import com.google.firebase.crash.FirebaseCrash;

import org.pustule.fanfarepustule.R;
import org.pustule.fanfarepustule.base.BaseActivity;
import org.pustule.fanfarepustule.utils.DateHelper;
import org.pustule.fanfarepustule.utils.StringHelper;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

public class HomeActivity extends BaseActivity {

    private static final String TAG = HomeActivity.class.getSimpleName();

    static final int REQUEST_GOOGLE_PLAY_SERVICES = 1002;

    static final int LOADING_VIEW = 0;
    static final int EVENT_VIEW = 1;
    static final int ERROR_VIEW = 2;

    GoogleCredential mCredential;

    @BindView(R.id.card_flipper_view) protected ViewFlipper cardViewFlipper;

    @BindView(R.id.event_title) protected TextView eventTitleView;
    @BindView(R.id.event_subtitle) protected TextView eventSubtitleView;
    @BindView(R.id.event_detail) protected TextView eventDetailView;

    @BindView(R.id.error_view) protected TextView errorView;

    @Override
    protected int getLayoutRes() {
        return R.layout.activity_home;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initialize credentials and service object.
        try {
            mCredential = GoogleCredential.fromStream(getAssets().open("fanfare_pustule_133ac261ac5e.json"))
                    .createScoped(Collections.singleton(CalendarScopes.CALENDAR_READONLY));
        } catch (IOException e) {
            Log.e(TAG, e.getLocalizedMessage());
        }
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        getEventFromCalendarApi();
    }

    private void getEventFromCalendarApi() {
        if (! isGooglePlayServicesAvailable()) {
            acquireGooglePlayServices();
        } else if (! isDeviceOnline()) {
            cardViewFlipper.setDisplayedChild(ERROR_VIEW);
            errorView.setText(getString(R.string.error_no_connection));
        } else {
            new MakeRequestTask(mCredential).execute();
        }
    }

    @Override
    protected void onActivityResult(
            int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch(requestCode) {
            case REQUEST_GOOGLE_PLAY_SERVICES:
                if (resultCode != RESULT_OK) {
                    Log.e(TAG, "Missing Google Play Services");
                } else {
                    getEventFromCalendarApi();
                }
                break;
        }
    }

    /**
     * Checks whether the device currently has a network connection.
     * @return true if the device has a network connection, false otherwise.
     */
    private boolean isDeviceOnline() {
        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnected());
    }

    /**
     * Check that Google Play services APK is installed and up to date.
     * @return true if Google Play Services is available and up to
     *     date on this device; false otherwise.
     */
    private boolean isGooglePlayServicesAvailable() {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        final int connectionStatusCode = apiAvailability.isGooglePlayServicesAvailable(this);
        return connectionStatusCode == ConnectionResult.SUCCESS;
    }

    /**
     * Attempt to resolve a missing, out-of-date, invalid or disabled Google
     * Play Services installation via a user dialog, if possible.
     */
    private void acquireGooglePlayServices() {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        final int connectionStatusCode = apiAvailability.isGooglePlayServicesAvailable(this);
        if (apiAvailability.isUserResolvableError(connectionStatusCode)) {
            showGooglePlayServicesAvailabilityErrorDialog(connectionStatusCode);
        }
    }

    /**
     * Display an error dialog showing that Google Play Services is missing
     * or out of date.
     * @param connectionStatusCode code describing the presence (or lack of)
     *     Google Play Services on this device.
     */
    void showGooglePlayServicesAvailabilityErrorDialog(final int connectionStatusCode) {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        Dialog dialog = apiAvailability.getErrorDialog(
                HomeActivity.this,
                connectionStatusCode,
                REQUEST_GOOGLE_PLAY_SERVICES);
        dialog.show();
    }

    //region Clicked Listener

    @OnClick(R.id.retry_button)
    public void onRetryButtonClicked() {
        getEventFromCalendarApi();
    }

    @OnClick(R.id.musics_button)
    public void onMusicsButtonClicked() {
        FirebaseCrash.report(new Exception("My first Android non-fatal error"));
    }

    //endregion

    //region Tasks

    /**
     * An asynchronous task that handles the Google Calendar API call.
     * Placing the API calls in their own task ensures the UI stays responsive.
     */
    private class MakeRequestTask extends AsyncTask<Void, Void, Event> {
        private com.google.api.services.calendar.Calendar mService = null;
        private Exception mLastError = null;

        MakeRequestTask(GoogleCredential credential) {
            HttpTransport transport = AndroidHttp.newCompatibleTransport();
            JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();

            mService = new Calendar.Builder(transport, jsonFactory, credential)
                    .setApplicationName("Fanfare Pustule")
                    .build();

        }

        /**
         * Background task to call Google Calendar API.
         * @param params no parameters needed for this task.
         */
        @Override
        protected Event doInBackground(Void... params) {
            try {
                return getNextEventFromApi();
            } catch (Exception e) {
                mLastError = e;
                cancel(true);
                return null;
            }
        }

        /**
         * Fetch the next event from the fanfare calendar.
         * @return next event of the calendar.
         * @throws IOException
         */
        private Event getNextEventFromApi() throws IOException {
            // List the next event from the fanfare calendar.

            DateTime now = new DateTime(System.currentTimeMillis());
            Events events = mService.events().list("75c4061rn1t05hjeff2rjdp9r8@group.calendar.google.com")
                    .setMaxResults(1)
                    .setTimeMin(now)
                    .setOrderBy("startTime")
                    .setSingleEvents(true)
                    .setShowDeleted(false)
                    .execute();
            List<Event> items = events.getItems();

            return items.get(0);
        }


        @Override
        protected void onPreExecute() {
            cardViewFlipper.setDisplayedChild(LOADING_VIEW);
        }

        @Override
        protected void onPostExecute(Event event) {
            if (event != null) {
                cardViewFlipper.setDisplayedChild(EVENT_VIEW);
                eventTitleView.setText(event.getSummary());
                // Subtitle as Start Date and location

                String subtitle;
                DateTime startDateTime = event.getStart().getDate() != null ? event.getStart().getDate() : event.getStart().getDateTime();
                subtitle = DateHelper.getDateFormattedFromDateTime(startDateTime);
                String location = event.getLocation();
                if (!StringHelper.isEmptyOrNull(location)) {
                    subtitle += " - " + location;
                }
                eventSubtitleView.setText(subtitle);
                eventDetailView.setText(event.getDescription());
            }
            else {
                cardViewFlipper.setDisplayedChild(ERROR_VIEW);
                errorView.setText(getString(R.string.error_no_event));
            }
        }

        @Override
        protected void onCancelled() {
            cardViewFlipper.setDisplayedChild(ERROR_VIEW);
            if (mLastError != null) {
                if (mLastError instanceof GooglePlayServicesAvailabilityIOException) {
                    showGooglePlayServicesAvailabilityErrorDialog(
                            ((GooglePlayServicesAvailabilityIOException) mLastError)
                                    .getConnectionStatusCode());
                } else {
                    errorView.setText(getString(R.string.error_unknown));
                    Log.e(TAG, "The following error occurred:\n"
                            + mLastError.getMessage());
                }
            } else {
                errorView.setText(getString(R.string.error_cancel));
                Log.e(TAG, "Request cancelled.");
            }
        }
    }

    //endregion
}
