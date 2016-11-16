package amhamogus.com.daysoff;

import android.Manifest;
import android.accounts.AccountManager;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.util.ExponentialBackOff;
import com.google.api.services.calendar.CalendarScopes;

import java.util.Arrays;
import java.util.List;

import amhamogus.com.daysoff.fragments.MainListFragment;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

public class MainActivity extends AppCompatActivity
        implements MainListFragment.OnListFragmentInteractionListener,
        EasyPermissions.PermissionCallbacks {

    public Toast mOutputText;

    String TAG = "MAIN ACTIVITY";

    /**
     * The key for the list parameter.
     */
    private static final String ARG_CALENDAR_NAME = "calendarName";
    private static final String PREF_FILE = "calendarSessionData";
    private static final String PREF_ACCOUNT_NAME = "accountName";
    private static final String PREF_CALENDAR_NAME = "calendarName";
    private static final String PREF_CALENDAR_ID = "calendarId";

    GoogleAccountCredential mCredential;
    static final int REQUEST_ACCOUNT_PICKER = 1000;
    static final int REQUEST_AUTHORIZATION = 1001;
    static final int REQUEST_GOOGLE_PLAY_SERVICES = 1002;
    static final int REQUEST_PERMISSION_GET_ACCOUNTS = 1003;
    private static final String[] SCOPES = {CalendarScopes.CALENDAR};

    /**
     * A instance of {@link MainListFragment} that displays a
     * collection of events.
     */
    private MainListFragment mList;

    SharedPreferences settings;
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FragmentManager fragmentManager = getSupportFragmentManager();
        mList = (MainListFragment) fragmentManager.findFragmentByTag("list");

        if (mList == null) {
            mList = mList.newInstance(1);
            fragmentManager.beginTransaction()
                    .add(R.id.list_wrapper, mList, "list").commit();
        }

        settings = getSharedPreferences(PREF_FILE, Context.MODE_PRIVATE);

        // Initialize credentials and service object.
        mCredential = GoogleAccountCredential.usingOAuth2(
                getApplicationContext(), Arrays.asList(SCOPES))
                .setBackOff(new ExponentialBackOff());

        if (!settings.contains(PREF_ACCOUNT_NAME)) {
            getCalendarList();
        }

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
    }

    @Override
    public void onCalendarSelectedInteraction(String calendarID, String calendarName) {

        if (calendarID != null & calendarName != null) {
            String name = settings.getString(PREF_ACCOUNT_NAME, null);

            editor = settings.edit();
            editor.commit();

            editor.putString(PREF_CALENDAR_NAME, calendarName);
            editor.putString(PREF_CALENDAR_ID, calendarID);
            editor.commit();

            Intent intent = new Intent(getApplicationContext(), CalendarActivity.class);
            intent.putExtra(ARG_CALENDAR_NAME, calendarName);
            intent.putExtra(PREF_ACCOUNT_NAME, name);

            startActivity(intent);
        } else {
            // User calendar has not been loaded.
            Toast.makeText(MainActivity.this,
                    "Please restart the app.", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * The following method was provided by the Google Calendar API Quickstart guide:
     * https://developers.google.com/google-apps/calendar/quickstart/android
     */
    @AfterPermissionGranted(REQUEST_PERMISSION_GET_ACCOUNTS)
    private void chooseAccount() {
        if (EasyPermissions.hasPermissions(
                this, Manifest.permission.GET_ACCOUNTS)) {
            String accountName =
                    getPreferences(Context.MODE_PRIVATE).getString(PREF_ACCOUNT_NAME, null);

            Log.d(TAG, "CHOOSE ACCOUNT METHOD BEING RUN");

            if (accountName != null) {
                mCredential.setSelectedAccountName(accountName);
                getCalendarList();
            } else {
                // Start a dialog from which the user can choose an account
                startActivityForResult(mCredential.newChooseAccountIntent(), REQUEST_ACCOUNT_PICKER);
            }
        } else {
            // Request the GET_ACCOUNTS permission via a user dialog
            EasyPermissions.requestPermissions(
                    this,
                    "This app needs to access your Google account (via Contacts).",
                    REQUEST_PERMISSION_GET_ACCOUNTS,
                    Manifest.permission.GET_ACCOUNTS);
        }
    }

    /**
     * The following method was provided by the Google Calendar API Quickstart guide:
     * https://developers.google.com/google-apps/calendar/quickstart/android
     *
     * @param requestCode  The request code passed in
     *                     requestPermissions(android.app.Activity, String, int, String[])
     * @param permissions  The requested permissions. Never null.
     * @param grantResults The grant results for the corresponding permissions
     *                     which is either PERMISSION_GRANTED or PERMISSION_DENIED. Never null.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(
                requestCode, permissions, grantResults, this);
    }

    /**
     * The following method was provided by the Google Calendar API Quickstart guide:
     * https://developers.google.com/google-apps/calendar/quickstart/android
     *
     * @param requestCode The request code associated with the requested
     *                    permission
     * @param list        The requested permission list. Never null.
     */
    @Override
    public void onPermissionsGranted(int requestCode, List<String> list) {
        // Do nothing.
    }

    /**
     * Callback for when a permission is denied using the EasyPermissions
     * library.
     *
     * @param requestCode The request code associated with the requested
     *                    permission
     * @param list        The requested permission list. Never null.
     */
    @Override
    public void onPermissionsDenied(int requestCode, List<String> list) {
        // Do nothing.
        Log.d(TAG, "PERMISSION DENIED");
    }

    /**
     * Checks whether the device currently has a network connection.
     *
     * @return true if the device has a network connection, false otherwise.
     */
    private boolean isDeviceOnline() {
        ConnectivityManager connMgr =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnected());
    }

    /**
     * Check that Google Play services APK is installed and up to date.
     *
     * @return true if Google Play Services is available and up to
     * date on this device; false otherwise.
     */
    private boolean isGooglePlayServicesAvailable() {
        GoogleApiAvailability apiAvailability =
                GoogleApiAvailability.getInstance();
        final int connectionStatusCode =
                apiAvailability.isGooglePlayServicesAvailable(this);
        return connectionStatusCode == ConnectionResult.SUCCESS;
    }

    /**
     * Attempt to resolve a missing, out-of-date, invalid or disabled Google
     * Play Services installation via a user dialog, if possible.
     */
    private void acquireGooglePlayServices() {
        GoogleApiAvailability apiAvailability =
                GoogleApiAvailability.getInstance();
        final int connectionStatusCode =
                apiAvailability.isGooglePlayServicesAvailable(this);
        if (apiAvailability.isUserResolvableError(connectionStatusCode)) {
            showGooglePlayServicesAvailabilityErrorDialog(connectionStatusCode);
        }
    }

    /**
     * The following method was provided by the Google Calendar API Quickstart guide:
     * https://developers.google.com/google-apps/calendar/quickstart/android
     * <p/>
     * Display an error dialog showing that Google Play Services is missing
     * or out of date.
     *
     * @param connectionStatusCode code describing the presence (or lack of)
     *                             Google Play Services on this device.
     */
    void showGooglePlayServicesAvailabilityErrorDialog(
            final int connectionStatusCode) {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        Dialog dialog = apiAvailability.getErrorDialog(
                MainActivity.this,
                connectionStatusCode,
                REQUEST_GOOGLE_PLAY_SERVICES);
        dialog.show();
    }

    /**
     * The following method was provided by the Google Calendar API Quickstart guide:
     * https://developers.google.com/google-apps/calendar/quickstart/android
     * <p/>
     * Attempt to call the API, after verifying that all the preconditions are
     * satisfied. The preconditions are: Google Play Services installed, an
     * account was selected and the device currently has online access. If any
     * of the preconditions are not satisfied, the app will prompt the user as
     * appropriate.
     */
    private void getCalendarList() {
        if (!isGooglePlayServicesAvailable()) {
            acquireGooglePlayServices();
        } else if (mCredential.getSelectedAccountName() == null) {
            chooseAccount();
        } else if (!isDeviceOnline()) {
            mOutputText.makeText(this,
                    "Network connection not available.", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(
            int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        Log.d(TAG, "ON ACTIVITY RESULT CALLED");
        switch (requestCode) {
            case REQUEST_GOOGLE_PLAY_SERVICES:
                if (resultCode != RESULT_OK) {
                    Log.d(TAG, "Not okay");
                    mOutputText.setText(
                            "This app requires Google Play Services. Please install " +
                                    "Google Play Services on your device and relaunch this app.");
                } else {
                    getCalendarList();
                }
                break;
            case REQUEST_ACCOUNT_PICKER:
                if (resultCode == RESULT_OK
                        && data != null && data.getExtras() != null) {

                    Log.d(TAG, "RESULT OK");
                    String accountName =
                            data.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);

                    // Add users' account name to shared preferences
                    if (accountName != null) {
                        SharedPreferences settings =
                                getSharedPreferences(PREF_FILE, Context.MODE_PRIVATE);

                        SharedPreferences.Editor editor = settings.edit();
                        editor.putString(PREF_ACCOUNT_NAME, accountName);
                        editor.apply();

                        mCredential.setSelectedAccountName(accountName);
                        getCalendarList();
                    }
                }
                break;
            case REQUEST_AUTHORIZATION:
                if (resultCode == RESULT_OK) {
                    Log.d(TAG, "RESULT OKAY AND CAL");
                    getCalendarList();
                }
                break;
        }
    }

}
