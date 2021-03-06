package com.example.instabug.ui.activities;

import android.content.DialogInterface;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import android.view.View;

import com.example.instabug.BaseActivity;
import com.example.instabug.R;
import com.instabug.bug.BugReporting;
import com.instabug.library.Instabug;
import com.instabug.library.InstabugColorTheme;
import com.instabug.library.invocation.InstabugInvocationEvent;
import com.skydoves.colorpickerview.ColorEnvelope;
import com.skydoves.colorpickerview.ColorPickerDialog;
import com.skydoves.colorpickerview.listeners.ColorEnvelopeListener;

import java.util.ArrayList;
import java.util.Arrays;


public class SettingsActivity extends BaseActivity {

    final String[] invocationEvents = getInvocationEventsNames(InstabugInvocationEvent.class);
    final boolean[] invocationEventsState = {false, false, false, false, false};

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        for (int i = 0; i < invocationEvents.length; i++) {
            invocationEvents[i] = invocationEvents[i].replace("_", " ");
        }
    }

    public void onShowInvocationEventsClicked(View view) {
        // reset events
        for (int i = 0; i < invocationEventsState.length; i++) {
            invocationEventsState[i] = false;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select Invocation Events");
        builder.setMultiChoiceItems(invocationEvents, null, new DialogInterface.OnMultiChoiceClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                invocationEventsState[which] = isChecked;
            }
        });
        builder.setPositiveButton("Done", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                ArrayList<InstabugInvocationEvent> selectedEvents = new ArrayList();
                for (int i = 0; i < invocationEvents.length; i++) {
                    if (invocationEventsState[i]) {
                        selectedEvents.add(InstabugInvocationEvent.valueOf(invocationEvents[i].toUpperCase().replace(" ", "_")));
                    }
                }
                // set new invocation events here
                BugReporting.setInvocationEvents(selectedEvents.toArray(new InstabugInvocationEvent[selectedEvents.size()]));
            }
        });
        builder.show();
    }

    public static String[] getInvocationEventsNames(Class<? extends Enum<?>> e) {
        return Arrays.toString(e.getEnumConstants()).replaceAll("^.|.$", "").split(", ");
    }

    public void onChangeThemeClicked(View view) {
        final CharSequence[] items = {"Light", "Dark"};

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Change Theme");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int item) {
                switch (item) {
                    case 0:
                        Instabug.setColorTheme(InstabugColorTheme.InstabugColorThemeLight);
                        break;
                    case 1:
                        Instabug.setColorTheme(InstabugColorTheme.InstabugColorThemeDark);
                        break;
                }
            }
        });
        builder.show();
    }

    public void onChangePrimaryColorClicked(View view) {
        new ColorPickerDialog.Builder(this)
                .setTitle("ColorPicker Dialog")
                .setPreferenceName("MyColorPickerDialog")
                .setPositiveButton(getString(android.R.string.ok),
                        new ColorEnvelopeListener() {
                            @Override
                            public void onColorSelected(ColorEnvelope envelope, boolean fromUser) {
                                Instabug.setPrimaryColor(envelope.getColor());
                            }
                        })
                .setNegativeButton(getString(android.R.string.cancel),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                            }
                        })
                .attachAlphaSlideBar(true) // the default value is true.
                .attachBrightnessSlideBar(true)  // the default value is true.
                .setBottomSpace(12) // set a bottom space between the last slidebar and buttons.
                .show();

    }
}
