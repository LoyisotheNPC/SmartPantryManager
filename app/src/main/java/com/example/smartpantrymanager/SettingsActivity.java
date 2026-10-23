
package com.example.smartpantrymanager;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class SettingsActivity extends AppCompatActivity {

    private static final String PREFS_NAME = "SmartPantrySettings";
    private static final String KEY_SHOW_EXPIRY = "show_expiry_dates";
    private static final String KEY_NOTIFICATIONS = "expiry_reminders";

    private SharedPreferences preferences;
    private CheckBox expiryCheckBox;
    private CheckBox remindersCheckBox;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        preferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);

        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(24, 24, 24, 24);
        root.setBackgroundColor(0xFFF5F7FA);

        TextView title = new TextView(this);
        title.setText("Settings");
        title.setTextSize(28);
        title.setTextColor(0xFF263238);
        title.setGravity(Gravity.CENTER);
        title.setPadding(0, 12, 0, 20);
        root.addView(title);

        ScrollView scrollView = new ScrollView(this);

        LinearLayout settingsList = new LinearLayout(this);
        settingsList.setOrientation(LinearLayout.VERTICAL);

        TextView displayHeading = new TextView(this);
        displayHeading.setText("Display");
        displayHeading.setTextSize(20);
        displayHeading.setTextColor(0xFF263238);
        settingsList.addView(displayHeading);

        expiryCheckBox = new CheckBox(this);
        expiryCheckBox.setText("Show ingredient expiry dates");
        expiryCheckBox.setTextSize(16);
        expiryCheckBox.setChecked(
                preferences.getBoolean(KEY_SHOW_EXPIRY, true)
        );
        settingsList.addView(expiryCheckBox);

        TextView reminderHeading = new TextView(this);
        reminderHeading.setText("Reminders");
        reminderHeading.setTextSize(20);
        reminderHeading.setTextColor(0xFF263238);
        reminderHeading.setPadding(0, 24, 0, 4);
        settingsList.addView(reminderHeading);

        remindersCheckBox = new CheckBox(this);
        remindersCheckBox.setText("Enable expiry reminder preference");
        remindersCheckBox.setTextSize(16);
        remindersCheckBox.setChecked(
                preferences.getBoolean(KEY_NOTIFICATIONS, false)
        );
        settingsList.addView(remindersCheckBox);

        TextView reminderNote = new TextView(this);
        reminderNote.setText(
                "This saves your reminder preference. Automatic notifications " +
                        "are not implemented in this version."
        );
        reminderNote.setTextSize(14);
        reminderNote.setTextColor(0xFF607D8B);
        reminderNote.setPadding(8, 4, 8, 16);
        settingsList.addView(reminderNote);

        TextView aboutHeading = new TextView(this);
        aboutHeading.setText("About");
        aboutHeading.setTextSize(20);
        aboutHeading.setTextColor(0xFF263238);
        aboutHeading.setPadding(0, 24, 0, 4);
        settingsList.addView(aboutHeading);

        TextView aboutText = new TextView(this);
        aboutText.setText(
                "Smart Pantry Manager\n" +
                        "Version 1.0\n\n" +
                        "Manage pantry ingredients, track quantities and explore recipes."
        );
        aboutText.setTextSize(16);
        aboutText.setTextColor(0xFF546E7A);
        aboutText.setPadding(8, 8, 8, 16);
        settingsList.addView(aboutText);

        scrollView.addView(settingsList);

        LinearLayout.LayoutParams scrollParams =
                new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        0,
                        1
                );

        root.addView(scrollView, scrollParams);

        Button saveButton = new Button(this);
        saveButton.setText("Save Settings");
        saveButton.setOnClickListener(v -> saveSettings());
        root.addView(saveButton);

        Button backButton = new Button(this);
        backButton.setText("Back");
        backButton.setOnClickListener(v -> finish());
        root.addView(backButton);

        setContentView(root);
    }

    private void saveSettings() {
        boolean saved = preferences.edit()
                .putBoolean(KEY_SHOW_EXPIRY, expiryCheckBox.isChecked())
                .putBoolean(KEY_NOTIFICATIONS, remindersCheckBox.isChecked())
                .commit();

        if (saved) {
            Toast.makeText(
                    this,
                    "Settings saved successfully",
                    Toast.LENGTH_SHORT
            ).show();
        } else {
            Toast.makeText(
                    this,
                    "Could not save settings",
                    Toast.LENGTH_SHORT
            ).show();
        }
    }
}

