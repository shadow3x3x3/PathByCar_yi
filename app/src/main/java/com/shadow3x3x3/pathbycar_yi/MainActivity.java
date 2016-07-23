/**
 * Copyright 2014 Google Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.shadow3x3x3.pathbycar_yi;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.shadow3x3x3.pathbycar_yi.service.LocationService;


public class MainActivity extends Activity {
    private Button startUpdatesButton;
    private Button stopUpdatesButton;
    private EditText recognitionEditText;

    private Boolean requestingLocationUpdates;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        startUpdatesButton = (Button) findViewById(R.id.start_updates_button);
        stopUpdatesButton  = (Button) findViewById(R.id.stop_updates_button);

        recognitionEditText = (EditText) findViewById(R.id.recognition_edit_text);

        requestingLocationUpdates = false;
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    /**
     Button Actions
     **/
    public void startUpdatesButtonHandler(View view) {
        Intent intent = new Intent(this, LocationService.class);
        intent.putExtra("recognitionName", recognitionEditText.getText().toString());
        startService(intent);
        if (!requestingLocationUpdates) {
            requestingLocationUpdates = true;
            setButtonsEnabledState();
        }
    }

    public void stopUpdatesButtonHandler(View view) {
        Intent intent = new Intent(this, LocationService.class);
        stopService(intent);
        if (requestingLocationUpdates) {
            requestingLocationUpdates = false;
            setButtonsEnabledState();
        }
    }

    private void setButtonsEnabledState() {
        if (requestingLocationUpdates) {
            startUpdatesButton.setEnabled(false);
            recognitionEditText.setEnabled(false);
            stopUpdatesButton.setEnabled(true);
        } else {
            startUpdatesButton.setEnabled(true);
            recognitionEditText.setEnabled(true);
            stopUpdatesButton.setEnabled(false);
        }
    }
}
