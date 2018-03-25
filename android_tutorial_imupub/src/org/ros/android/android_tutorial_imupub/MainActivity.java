/*
 * Copyright (C) 2011 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package org.ros.android.android_tutorial_imupub;

import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Bundle;
import org.ros.android.MessageCallable;
import org.ros.android.RosActivity;
import org.ros.android.view.RosTextView;
import org.ros.node.NodeConfiguration;
import org.ros.node.NodeMainExecutor;

/**
 * @author damonkohler@google.com (Damon Kohler)
 */
public class MainActivity extends RosActivity {
  private final SensorManager mSensorManager;
  private final Sensor mAccelerometer;

  private RosTextView<std_msgs.String> rosTextView;
  private ImuDataPublisher imu;

  public MainActivity() {
    // The RosActivity constructor configures the notification title and ticker
    // messages.
    super("Pubsub Tutorial", "Pubsub Tutorial");

    mSensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);
    mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
  }

  @SuppressWarnings("unchecked")
  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.main);
    rosTextView = (RosTextView<std_msgs.String>) findViewById(R.id.text);
    rosTextView.setTopicName("chatter");
    rosTextView.setMessageType(std_msgs.String._TYPE);
    rosTextView.setMessageToStringCallable(new MessageCallable<String, std_msgs.String>() {
      @Override
      public String call(std_msgs.String message) {
        return message.getData();
      }
    });
  }

  @Override
  protected void init(NodeMainExecutor nodeMainExecutor) {
    imu = new ImuDataPublisher();

    // At this point, the user has already been prompted to either enter the URI
    // of a master to use or to start a master locally.

    // The user can easily use the selected ROS Hostname in the master chooser
    // activity.
    NodeConfiguration nodeConfiguration = NodeConfiguration.newPublic(getRosHostname());
    nodeConfiguration.setMasterUri(getMasterUri());
    nodeMainExecutor.execute(imu, nodeConfiguration);
    // The RosTextView is also a NodeMain that must be executed in order to
    // start displaying incoming messages.
    nodeMainExecutor.execute(rosTextView, nodeConfiguration);
  }

  @Override
  protected void onResume() {
    super.onResume();
    mSensorManager.registerListener(imu, mAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);
  }

  @Override
  protected void onPause() {
    super.onPause();
    mSensorManager.unregisterListener(imu);
  }
}
