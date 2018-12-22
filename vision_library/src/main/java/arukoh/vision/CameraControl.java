package arukoh.vision;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.RequiresPermission;
import android.view.SurfaceHolder;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.vision.CameraSource;

import java.io.IOException;

import arukoh.vision.images.Size;

public class CameraControl {
    public static final int CAMERA_FACING_BACK = CameraSource.CAMERA_FACING_BACK;
    public static final int CAMERA_FACING_FRONT = CameraSource.CAMERA_FACING_FRONT;

    private CameraSource mCameraSource;
    private Detector mDetector;

    private CameraControl(CameraSource cameraSource, Detector detector) {
        mCameraSource = cameraSource;
        mDetector = detector;
    }

    public static boolean available(Context context) {
        int code = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(context);
        return code == ConnectionResult.SUCCESS;
    }

    public Size getPreviewSize() {
        if (mCameraSource.getPreviewSize() == null) {
            return null;
        }
        return new Size(mCameraSource.getPreviewSize());
    }

    public int getCameraFacing() { return mCameraSource.getCameraFacing(); }

    @SuppressLint("MissingPermission")
    @RequiresPermission("android.permission.CAMERA")
    public void start(SurfaceHolder holder) throws IOException {
        mDetector.init();
        mCameraSource.start(holder);
    }

    public void stop() {
        mDetector.abort();
        mCameraSource.stop();
    }

    public void release() {
        mDetector.init();
        mCameraSource.release();
    }

    public static class Builder {

        private Detector mDetector;
        private CameraSource.Builder mDelegate;

        public Builder(Context context, Detector detector) {
            mDetector = detector;
            mDelegate = new CameraSource.Builder(context, detector.entity());
            mDelegate.setAutoFocusEnabled(true)
                    .setRequestedPreviewSize(640, 480)
                    .setRequestedFps(30.0f);
        }

//        public Builder setRequestedFps(float fps) {
//            mDelegate.setRequestedFps(fps);
//            return this;
//        }
//
        public Builder setRequestedPreviewSize(int width, int height) {
            mDelegate.setRequestedPreviewSize(width, height);
            return this;
        }

        public Builder setFacing(int facing) {
            mDelegate.setFacing(facing);
            return this;
        }

//        public Builder setAutoFocusEnabled(boolean autoFocusEnabled) {
//            mDelegate.setAutoFocusEnabled(autoFocusEnabled);
//            return this;
//        }

        public CameraControl build() {
            return new CameraControl(mDelegate.build(), mDetector);
        }
    }
}
