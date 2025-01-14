/**
 * Copyright 2017 Kailash Dabhi (Kingbull Technology)
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
package com.kingbull.recorder;

import android.media.AudioFormat;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.kailashdabhi.omrecorder.AudioChunk;
import com.kailashdabhi.omrecorder.AudioRecordConfig;
import com.kailashdabhi.omrecorder.OmRecorder;
import com.kailashdabhi.omrecorder.PullTransport;
import com.kailashdabhi.omrecorder.PullableSource;
import com.kailashdabhi.omrecorder.Recorder;
import com.kailashdabhi.omrecorder.WriteAction;

import java.io.File;
import java.io.IOException;

/**
 * @author Kailash Dabhi
 * @date 18-07-2016. Copyright (c) 2017 Kingbull Technology. All rights reserved.
 */
public class WavRecorderActivity extends AppCompatActivity {
  Recorder recorder;
  ImageView recordButton;
  CheckBox skipSilence;
  private Button pauseResumeButton;

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_recorder);
    getSupportActionBar().setTitle("Wav Recorder");
    setupRecorder();
    skipSilence = (CheckBox) findViewById(R.id.skipSilence);
    skipSilence.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
      @Override public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
        if (isChecked) {
          setupNoiseRecorder();
        } else {
          setupRecorder();
        }
      }
    });
    recordButton = (ImageView) findViewById(R.id.recordButton);
    recordButton.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View view) {
        recorder.startRecording();
        skipSilence.setEnabled(false);
      }
    });
    findViewById(R.id.stopButton).setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View view) {
        try {
          recorder.stopRecording();
        } catch (IOException e) {
          e.printStackTrace();
        }
        skipSilence.setEnabled(true);
        recordButton.post(new Runnable() {
          @Override public void run() {
            animateVoice(0);
          }
        });
      }
    });
    pauseResumeButton = (Button) findViewById(R.id.pauseResumeButton);
    pauseResumeButton.setOnClickListener(new View.OnClickListener() {
      boolean isPaused = false;

      @Override public void onClick(View view) {
        if (recorder == null) {
          Toast.makeText(WavRecorderActivity.this, "Please start recording first!",
              Toast.LENGTH_SHORT).show();
          return;
        }
        if (!isPaused) {
          pauseResumeButton.setText(getString(R.string.resume_recording));
          recorder.pauseRecording();
          pauseResumeButton.postDelayed(new Runnable() {
            @Override public void run() {
              animateVoice(0);
            }
          }, 100);
        } else {
          pauseResumeButton.setText(getString(R.string.pause_recording));
          recorder.resumeRecording();
        }
        isPaused = !isPaused;
      }
    });
  }

  private void setupRecorder() {
    recorder = OmRecorder.wav(
        new PullTransport.Default(mic(), new PullTransport.OnAudioChunkPulledListener() {
          @Override public void onAudioChunkPulled(AudioChunk audioChunk) {
            animateVoice((float) (audioChunk.maxAmplitude() / 200.0));
          }
        }), file());
  }

  private void setupNoiseRecorder() {
    recorder = OmRecorder.wav(
        new PullTransport.Noise(mic(),
            new PullTransport.OnAudioChunkPulledListener() {
              @Override public void onAudioChunkPulled(AudioChunk audioChunk) {
                animateVoice((float) (audioChunk.maxAmplitude() / 200.0));
              }
            },
            new WriteAction.Default(),
            new Recorder.OnSilenceListener() {
              @Override public void onSilence(long silenceTime) {
                Log.e("silenceTime", String.valueOf(silenceTime));
                Toast.makeText(WavRecorderActivity.this, "silence of " + silenceTime + " detected",
                    Toast.LENGTH_SHORT).show();
              }
            }, 200
        ), file()
    );
  }

  private void animateVoice(final float maxPeak) {
    recordButton.animate().scaleX(1 + maxPeak).scaleY(1 + maxPeak).setDuration(10).start();
  }

  private PullableSource mic() {
      return new PullableSource.Default(
              new AudioRecordConfig.Default(
                      MediaRecorder.AudioSource.MIC, AudioFormat.ENCODING_PCM_FLOAT,
                      AudioFormat.CHANNEL_IN_STEREO, 48000
              )
      );
  }

  private File file() {
    File[] allExternalFilesDirs = ContextCompat.getExternalFilesDirs(this, null);
    File dir = null;
    if(allExternalFilesDirs.length > 0) {
      dir = allExternalFilesDirs[allExternalFilesDirs.length - 1];
    }
    if (dir == null)  {
      dir = this.getExternalFilesDir(null);
    }

    return new File(dir, "kailashdabhi.wav");
  }
}
