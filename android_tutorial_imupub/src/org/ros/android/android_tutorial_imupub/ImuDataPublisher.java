package org.ros.android.android_tutorial_imupub;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;

import org.ros.concurrent.CancellableLoop;
import org.ros.internal.message.RawMessage;
import org.ros.namespace.GraphName;
import org.ros.node.AbstractNodeMain;
import org.ros.node.ConnectedNode;
import org.ros.node.topic.Publisher;
import org.ros.rosjava_geometry.Vector3;

/**
 * Created by mv on 25/03/18.
 */

public class ImuDataPublisher extends AbstractNodeMain implements SensorEventListener {
    private String topic_name;
    private Publisher<sensor_msgs.Imu> publisher;

    public ImuDataPublisher() {
        this.topic_name = "android_imu";
    }

    public ImuDataPublisher(String topic) {
        this.topic_name = topic;
    }

    public GraphName getDefaultNodeName() {
        return GraphName.of("rosjava_tutorial_pubsub/talker");
    }

    public void onStart(ConnectedNode connectedNode) {
        publisher = connectedNode.newPublisher(this.topic_name, "sensor_msgs/Imu");
//        connectedNode.executeCancellableLoop(new CancellableLoop() {
//            private int sequenceNumber;
//
//            protected void setup() {
//                this.sequenceNumber = 0;
//            }
//
//            protected void loop() throws InterruptedException {
//                sensor_msgs.Imu str = (sensor_msgs.Imu) publisher.newMessage();
//                str.setData("Hello world! " + this.sequenceNumber);
//                publisher.publish(str);
//                ++this.sequenceNumber;
//                Thread.sleep(1000L);
//            }
//        });
    }
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }
    public void onSensorChanged(SensorEvent event) {
        final double alpha = 0.8;

        Vector3 accel = new Vector3(event.values[0], event.values[0], event.values[0]);

        sensor_msgs.Imu imu = (sensor_msgs.Imu) publisher.newMessage();

        imu.setLinearAcceleration(accel.toVector3Message());
        publisher.publish(imu);
//        imu.setOrientation();
    }
}

