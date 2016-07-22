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
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.shadow3x3x3.pathbycar_yi.service.LocationService;
import com.shadow3x3x3.pathbycar_yi.service.LocationWebSocket;

import java.net.URI;
import java.net.URISyntaxException;


public class MainActivity extends Activity {
    protected LocationWebSocket locationWebSocket;

    protected Button mStartUpdatesButton;
    protected Button mStopUpdatesButton;

    protected Boolean mRequestingLocationUpdates;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mStartUpdatesButton     = (Button) findViewById(R.id.start_updates_button);
        mStopUpdatesButton      = (Button) findViewById(R.id.stop_updates_button);

        mRequestingLocationUpdates = false;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }

    /**
     Button actions
     **/
    public void startUpdatesButtonHandler(View view) {
        Intent intent = new Intent(this, LocationService.class);
        startService(intent);
        if (!mRequestingLocationUpdates) {
            mRequestingLocationUpdates = true;
            setButtonsEnabledState();
        }
    }

    public void stopUpdatesButtonHandler(View view) {
        Intent intent = new Intent(this, LocationService.class);
        stopService(intent);
        if (mRequestingLocationUpdates) {
            mRequestingLocationUpdates = false;
            setButtonsEnabledState();
        }
    }

    public void sendToWebsocket(View view) {

    }

    private void setButtonsEnabledState() {
        if (mRequestingLocationUpdates) {
            mStartUpdatesButton.setEnabled(false);
            mStopUpdatesButton.setEnabled(true);
        } else {
            mStartUpdatesButton.setEnabled(true);
            mStopUpdatesButton.setEnabled(false);
        }
    }



}
