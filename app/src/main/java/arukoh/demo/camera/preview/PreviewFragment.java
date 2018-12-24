package arukoh.demo.camera.preview;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;

import java.io.IOException;

import arukoh.demo.R;
import arukoh.vision.CameraControl;
import arukoh.vision.Detector;

public class PreviewFragment extends Fragment implements Detector.Callback {
    private static final String TAG = "PreviewFragment";

    private OnFragmentInteractionListener mListener;

    private CameraControl mCameraControl = null;

    private CameraSourcePreview mPreview;
    private GraphicOverlay mGraphicOverlay;
    private FaceGraphic mFaceGraphic;
    private ProgressBar mProgressBarReady;
    private ImageView mButtonStartDetection;

    private Detector mDetector;

    public PreviewFragment() {
    }

    public static PreviewFragment newInstance() {
        PreviewFragment fragment = new PreviewFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
        startCameraControl();
    }

    @Override
    public void onPause() {
        super.onPause();
        mDetector.abort();
        mPreview.stop();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mDetector != null) {
            mDetector.init();
        }
        if (mPreview != null) {
            mPreview.release();
            mPreview = null;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_preview, container, false);

        mPreview = view.findViewById(R.id.preview);
        mGraphicOverlay = view.findViewById(R.id.faceOverlay);
        mFaceGraphic = new FaceGraphic(mGraphicOverlay);
        mProgressBarReady = view.findViewById(R.id.progressBarReady);
        mButtonStartDetection = view.findViewById(R.id.buttonStartDetection);
        mButtonStartDetection.setClickable(false);
        mButtonStartDetection.setOnClickListener(view1 -> mDetector.start());

        createCameraControl();
        return view;
    }

    private void createCameraControl() {
        Context context = getActivity();
        mDetector = new Detector(context, 10, this);
        mCameraControl = new CameraControl.Builder(context, mDetector)
                .setRequestedPreviewSize(640, 480)
                .setFacing(CameraControl.CAMERA_FACING_FRONT)
                .build();
    }

    private void startCameraControl() {
        final Activity activity = getActivity();
        if (!CameraControl.available(activity)) {
            // TODO
            new AlertDialog.Builder(activity)
                    .setTitle("ERROR")
                    .setMessage("CameraControl unavailable")
                    .setPositiveButton("Close", (dialog, id) -> activity.finish())
                    .show();
        }

        if (mCameraControl != null) {
            try {
                mPreview.start(mCameraControl, mGraphicOverlay);
            } catch (IOException e) {
                Log.e(TAG, "Unable to start camera control.", e);
                mCameraControl.release();
                mCameraControl = null;
            }
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void detectionReadied() {
        mProgressBarReady.setVisibility(View.INVISIBLE);
        mButtonStartDetection.setClickable(true);
    }

    @Override
    public void detectionStarted() {
        mButtonStartDetection.setClickable(false);
        mButtonStartDetection.setVisibility(View.GONE);
    }

    @Override
    public void onDetect(Detector.Result result) {
        if (result.getFace() != null) {
            mGraphicOverlay.add(mFaceGraphic);
            mFaceGraphic.updateFace(result.getFace());
        }
    }

    @Override
    public void detectionAborted() {
        mGraphicOverlay.remove(mFaceGraphic);

        final Activity activity = getActivity();
        activity.runOnUiThread(() -> new AlertDialog.Builder(activity)
                .setTitle("Result Aborted")
                .setMessage("")
                .setPositiveButton("Close", (dialog, id) -> activity.finish())
                .show());
    }

    @Override
    public void detectionCompleted(final Detector.Result result) {
        mGraphicOverlay.remove(mFaceGraphic);
        mListener.onPreviewFinished(result);
    }

    @Override
    public void detectionFailed(final Detector.Exception e) {
        mGraphicOverlay.remove(mFaceGraphic);

        final Activity activity = getActivity();
        activity.runOnUiThread(() -> new AlertDialog.Builder(activity)
                .setTitle("Result Failed")
                .setMessage(e.getClass().getName())
                .setPositiveButton("Close", (dialog, id) -> activity.finish())
                .show());
    }

    public interface OnFragmentInteractionListener {
        void onPreviewFinished(Detector.Result result);
    }

}
