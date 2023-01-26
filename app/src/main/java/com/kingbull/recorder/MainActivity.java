/**
 * Copyright 2017 Kailash Dabhi (Kingbull Technology)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.kingbull.recorder;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

/**
 * @author Kailash Dabhi
 * @date 18-07-2016.
 * Copyright (c) 2017 Kingbull Technology. All rights reserved.
 */
public class MainActivity extends AppCompatActivity {
  private final static String DEMO_PCM = "Pcm Recorder";
  private final static String DEMO_WAV = "Wav Recorder";
  ListView listView;
  String[] demoArray = new String[] { "Pcm Recorder", "Wav Recorder" };

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    listView = (ListView) findViewById(android.R.id.list);
    listView.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, demoArray));
    listView.setOnItemClickListener(new OnItemClickListener() {
      @Override public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        switch (demoArray[i]) {
          case DEMO_PCM:
            startActivity(new Intent(MainActivity.this, PcmRecorderActivity.class));
            break;
          case DEMO_WAV:
            startActivity(new Intent(MainActivity.this, WavRecorderActivity.class));
            break;
        }
      }
    });
    requestAudioPermissions();
  }

  //Create placeholder for user's consent to record_audio permission.
//This will be used in handling callback
  private final int MY_PERMISSIONS_RECORD_AUDIO = 1;

  private void requestAudioPermissions() {
        // Show user dialog to grant permission to record audio
        ActivityCompat.requestPermissions(this,
                new String[]{android.Manifest.permission.RECORD_AUDIO, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                MY_PERMISSIONS_RECORD_AUDIO);
  }

  //Handling callback
  @Override
  public void onRequestPermissionsResult(int requestCode,
                                         String permissions[], int[] grantResults) {
    super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    switch (requestCode) {
      case MY_PERMISSIONS_RECORD_AUDIO: {
        if (grantResults.length > 0
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
          // permission was granted, yay!
        } else {
          // permission denied, boo! Disable the
          // functionality that depends on this permission.
          Toast.makeText(this, "Permissions Denied to record audio", Toast.LENGTH_LONG).show();
        }
        return;
      }
    }
  }
}
