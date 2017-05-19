package org.pustule.fanfarepustule.ui.fragments;

import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.ViewFlipper;

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

import org.pustule.fanfarepustule.R;
import org.pustule.fanfarepustule.ui.bases.BaseFragment;
import org.pustule.fanfarepustule.utils.DateHelper;
import org.pustule.fanfarepustule.utils.DeviceUtils;
import org.pustule.fanfarepustule.utils.StringHelper;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

import static android.app.Activity.RESULT_OK;

/**
 * Created by Paul Mougin on 19/05/2017.
 */

public class HomeFragment extends BaseFragment {

    private static final String TAG = HomeFragment.class.getSimpleName();

    static final int LOADING_VIEW = 0;
    static final int EVENT_VIEW = 1;
    static final int ERROR_VIEW = 2;

    @BindView(R.id.card_flipper_view) protected ViewFlipper cardViewFlipper;

    @BindView(R.id.event_title) protected TextView eventTitleView;
    @BindView(R.id.event_subtitle) protected TextView eventSubtitleView;
    @BindView(R.id.event_detail) protected TextView eventDetailView;

    @BindView(R.id.error_view) protected TextView errorView;

    GoogleCredential mCredential;

    @Override
    protected int getLayoutRes() {
        return R.layout.fragment_home;
    }

    @Override
    protected void bindView(View root) {
        super.bindView(root);

        // Initialize credentials and service object.
        try {
            mCredential = GoogleCredential.fromStream(getContext().getAssets().open("fanfare_pustule_133ac261ac5e.json"))
                    .createScoped(Collections.singleton(CalendarScopes.CALENDAR_READONLY));
        } catch (IOException e) {
            // TODO: Handle error Credential
            Log.e(TAG, e.getLocalizedMessage());
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        getEventFromCalendarApi();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch(requestCode) {
            case DeviceUtils.REQUEST_GOOGLE_PLAY_SERVICES:
                if (resultCode != RESULT_OK) {
                    // TODO: Handle Missing Google Play Service Error
                    Log.e(TAG, "Missing Google Play Services");
                } else {
                    getEventFromCalendarApi();
                }
                break;
        }
    }

    private void getEventFromCalendarApi() {
        if (!DeviceUtils.isDeviceOnline(getContext())) {
            cardViewFlipper.setDisplayedChild(ERROR_VIEW);
            errorView.setText(getString(R.string.error_no_connection));
        } else {
            new MakeRequestTask(mCredential).execute();
        }
    }

    //region Clicked Listener

    @OnClick(R.id.retry_button)
    public void onRetryButtonClicked() {
        getEventFromCalendarApi();
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
                    DeviceUtils.showGooglePlayServicesAvailabilityErrorDialog(
                            ((GooglePlayServicesAvailabilityIOException) mLastError).getConnectionStatusCode(),
                            getActivity());
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
