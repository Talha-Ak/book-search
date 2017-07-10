package com.example.talha.booksearch;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
    }

    // Create a fragment that extends PreferenceFragment, which contains everything related to
    // the preferences in the app.
    public static class BookPreferenceFragment extends PreferenceFragment implements
            Preference.OnPreferenceChangeListener {

        @Override
        public void onCreate(@Nullable Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.settings_main);

            // Find the maxResults preference using it's key and set it's summary.
            Preference maxResults = findPreference(getString(R.string.settings_max_results_key));
            bindPreferenceSummaryToValue(maxResults);

            // Find the orderBy preference using it's key and set it's summary.
            Preference orderBy = findPreference(getString(R.string.settings_order_by_key));
            bindPreferenceSummaryToValue(orderBy);

            // Find the printTyle preference using it's key and set it's summary
            Preference printType = findPreference(getString(R.string.settings_print_type_key));
            bindPreferenceSummaryToValue(printType);
        }

        private void bindPreferenceSummaryToValue(Preference preference) {
            // Set the current BookPreferenceFragment instance as the listener
            preference.setOnPreferenceChangeListener(this);

            //Retrieve the SharedPreferences
            SharedPreferences sharedPreferences =
                    PreferenceManager.getDefaultSharedPreferences(preference.getContext());

            // Test if the preference we have is a NumberPickerPreference
            if (preference instanceof NumberPickerPreference) {
                // Retrieve the value of the given preference
                int preferenceInt = sharedPreferences.getInt(preference.getKey(), 15);

                // Set the summary of the given preference to the value obtained.
                onPreferenceChange(preference, preferenceInt);

            } else {
                // Retrieve the value of the given preference
                String preferenceString = sharedPreferences.getString(preference.getKey(), "");

                // Set the summary of the given preference to the value obtained.
                onPreferenceChange(preference, preferenceString);
            }

        }

        @Override
        public boolean onPreferenceChange(Preference preference, Object newValue) {

            // Test if the preference we have is a ListPreference
            if (preference instanceof ListPreference) {

                // Cast the preference back into a ListPreference
                ListPreference listPreference = (ListPreference) preference;

                // Find the position of the updated value in the entry values array, which was given
                // in settings_main.xml
                int prefIndex = listPreference.findIndexOfValue(newValue.toString());

                // Test if the index position is valid
                if (prefIndex >= 0) {

                    // Get the array of entries from the listPreference, again which was given in
                    // settings_main.xml
                    CharSequence[] labels = listPreference.getEntries();

                    // Set the summary to be the label at the updated value's position in the array.
                    preference.setSummary(labels[prefIndex]);
                }

            } else {
                preference.setSummary(newValue.toString());
            }
            return true;
        }

    }

}
