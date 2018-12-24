package arukoh.vision;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.SparseArray;

import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.face.FaceDetector;

import java.time.Instant;

import arukoh.vision.core.Face;

public class Detector {
    public static final int MIM_DETECTION_TIME_SEC = 1;
    public static final int MAX_DETECTION_TIME_SEC = 90;

    private Delegate mDelegate;

    public Detector(Context context, int detectionTimeSec, Callback callback) {
        int time = Math.min(MAX_DETECTION_TIME_SEC, Math.max(MIM_DETECTION_TIME_SEC, detectionTimeSec));
        mDelegate = new Delegate(context, time, callback);
        mDelegate.setProcessor(new Processor());
    }

    public void init() { mDelegate.init(); }

    public void start() { mDelegate.start(); }

    public void abort() { mDelegate.abort(); }

    public int getDetectionTimeSecond() { return mDelegate.getDetectionTimeSecond(); }

    com.google.android.gms.vision.Detector<Void> entity() { return mDelegate; }

    public interface Callback {
        void detectionReadied();

        void detectionStarted();

        void onDetect(Result result);

        void detectionAborted();

        void detectionCompleted(Result result);

        void detectionFailed(Exception e);
    }

    public static class Result {
        private long mStartTimeSec;
        private int mDetectionTimeSec;
        private Face mFace;

        private Result(int detectionTimeSec) {
            mDetectionTimeSec = detectionTimeSec;
            clear();
        }

        public Face getFace() { return mFace; }

        public int getDetectionTimeSecond() { return mDetectionTimeSec; }

        public long getStartEpochSecond() { return mStartTimeSec; }

        public long getFinishEpochSecond() { return mStartTimeSec + mDetectionTimeSec; }

        public long getRemainingEpochSecond() { return Math.max(0, getFinishEpochSecond() - getCurrentEpochSecond()); }

        public boolean isFinish() { return getRemainingEpochSecond() == 0; }

        private void setStartEpochSecond(long time) { mStartTimeSec = time; }

        private void setStartEpochSecond() { setStartEpochSecond(getCurrentEpochSecond()); }

        private boolean update(Frame frame, SparseArray<com.google.android.gms.vision.face.Face> faces) {
            return faces.size() == 1 && updateFace(frame, faces.valueAt(0));
        }

        private boolean updateFace(Frame frame, com.google.android.gms.vision.face.Face face) {
            if (mFace == null) {
                mFace = new Face(face.getId());
            } else if (mFace.getId() != face.getId()) {
                return false;
            }
            mFace.update(
                    frame.getMetadata().getTimestampMillis(),
                    frame.getBitmap(),
                    face.getPosition(),
                    face.getWidth(),
                    face.getHeight()
            );
            return true;
        }

        private void finish() {
            if (mFace != null) {
                mFace.finalize();
            }
        }

        private void clear() {
            setStartEpochSecond(0L);
            if (mFace != null) {
                mFace.reset();
            }
        }

        @TargetApi(Build.VERSION_CODES.O)
        private long getCurrentEpochSecond() { return Instant.now().getEpochSecond(); }
    }

    public static class Exception extends java.lang.Exception {}
    public static class FrameOutException extends Exception {}
    public static class ManyFacesFoundException extends Exception {}

    static class Delegate extends com.google.android.gms.vision.Detector<Void> {
        private static final String TAG = "Detector.Delegate";
        private static final int STATUS_NOT_READY = 0;
        private static final int STATUS_STAY = 1;
        private static final int STATUS_PROCESSING = 2;
        private static final int STATUS_FINISH = 3;

        private int mStatus;
        private Result mResult;

        private Callback mCallback;
        private FaceDetector mFaceDetector;

        Delegate(Context context, int detectionTimeSec, Callback callback) {
            mResult = new Result(detectionTimeSec);

            mCallback = callback;
            mFaceDetector = new FaceDetector.Builder(context)
                    .setClassificationType(FaceDetector.NO_CLASSIFICATIONS)
                    .setProminentFaceOnly(true)
                    .setTrackingEnabled(true)
                    .build();
            init();
        }

        @Override
        public SparseArray<Void> detect(Frame frame) {
            SparseArray<com.google.android.gms.vision.face.Face> faces;
            switch (mStatus) {
                case STATUS_NOT_READY:
                    faces = mFaceDetector.detect(frame);
                    ready(faces);
                    break;
                case STATUS_STAY:
                    break;
                case STATUS_PROCESSING:
                    faces = mFaceDetector.detect(frame);
                    detect(frame, faces);
                    break;
                case STATUS_FINISH:
                    break;
            }

            return null;
        }

        private int getDetectionTimeSecond() { return mResult.getDetectionTimeSecond(); }

        private void init() {
            mResult.clear();
            mStatus = STATUS_NOT_READY;
        }

        private boolean start() {
            if (mStatus != STATUS_STAY) {
                return false;
            }
            mResult.setStartEpochSecond();
            mStatus = STATUS_PROCESSING;
            mCallback.detectionStarted();
            return true;
        }

        private boolean abort() {
            if (mStatus != STATUS_PROCESSING) {
                return false;
            }
            mStatus = STATUS_FINISH;
            mCallback.detectionAborted();
            return true;
        }

        private void ready(SparseArray<com.google.android.gms.vision.face.Face> faces) {
            if (faces.size() != 1) {
                return;
            }
            mStatus = STATUS_STAY;
            mCallback.detectionReadied();
        }

        private void detect(Frame frame, SparseArray<com.google.android.gms.vision.face.Face> faces) {
            if (mResult.isFinish()) {
                mStatus = STATUS_FINISH;
                mResult.finish();
                mCallback.detectionCompleted(mResult);
            } else if (mResult.update(frame, faces)) {
                mCallback.onDetect(mResult);
            } else {
                Exception e = new Exception();
                if (faces.size() == 0) {
                    e = new FrameOutException();
                } else if (faces.size() > 1) {
                    e = new ManyFacesFoundException();
                }
                mCallback.detectionFailed(e);
                init();
            }
        }
    }
}
