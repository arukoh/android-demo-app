package arukoh.vision;

import com.google.android.gms.vision.Detector;

class Processor implements Detector.Processor<Void> {
    @Override
    public void release() { }

    @Override
    public void receiveDetections(Detector.Detections<Void> detections) { }
}
