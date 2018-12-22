package arukoh.demo.camera;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.io.IOException;

import arukoh.demo.R;
import arukoh.vision.CameraControl;
import arukoh.vision.Detector;

public class PreviewActivity extends AppCompatActivity implements Detector.Callback {
    private static final String TAG = "PreviewActivity";

    private CameraControl mCameraControl = null;

    private CameraSourcePreview mPreview;
    private GraphicOverlay mGraphicOverlay;
    private FaceGraphic mFaceGraphic;
    private ProgressBar mProgressBarReady;
    private ImageView mButtonStartDetection;

    private Detector mDetector;

    private static final int RC_HANDLE_CAMERA_PERM = 2;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preview);

        mPreview = findViewById(R.id.preview);
        mGraphicOverlay = findViewById(R.id.faceOverlay);
        mFaceGraphic = new FaceGraphic(mGraphicOverlay);
        mProgressBarReady = findViewById(R.id.progressBarReady);
        mButtonStartDetection = findViewById(R.id.buttonStartDetection);
        mButtonStartDetection.setClickable(false);
        mButtonStartDetection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDetector.start();
            }
        });

        int rc = ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA);
        if (rc == PackageManager.PERMISSION_GRANTED) {
            createCameraControl();
        } else {
            requestCameraPermission();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        startCameraControl();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mDetector.abort();
        mPreview.stop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mDetector != null) {
            mDetector.init();
        }
        if (mCameraControl != null) {
            mCameraControl.release();
        }
    }

    private void createCameraControl() {
        Context context = getApplicationContext();
        mDetector = new Detector(context, 10, this);
        mCameraControl = new CameraControl.Builder(context, mDetector)
                .setRequestedPreviewSize(640, 480)
                .setFacing(CameraControl.CAMERA_FACING_FRONT)
                .build();
    }

    private void startCameraControl() {
        if (!CameraControl.available(getApplicationContext())) {
            String msg = "CameraControl unavailable"; // TODO
            Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
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

    private void requestCameraPermission() {
        Log.w(TAG, "Camera permission is not granted. Requesting permission");

        final String[] permissions = new String[]{Manifest.permission.CAMERA};

        if (!ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.CAMERA)) {
            ActivityCompat.requestPermissions(this, permissions, RC_HANDLE_CAMERA_PERM);
            return;
        }

        final Activity thisActivity = this;

        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ActivityCompat.requestPermissions(thisActivity, permissions, RC_HANDLE_CAMERA_PERM);
            }
        };

        Snackbar.make(mGraphicOverlay, R.string.permission_camera_rationale, Snackbar.LENGTH_INDEFINITE)
                .setAction(R.string.ok, listener)
                .show();
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

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                new AlertDialog.Builder(PreviewActivity.this)
                        .setTitle("Detection Aborted")
                        .setMessage("")
                        .setPositiveButton("Close", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {
                                PreviewActivity.this.finish();
                            }
                        })
                        .show();
            }
        });
    }

    @Override
    public void detectionCompleted(final Detector.Result result) {
        mGraphicOverlay.remove(mFaceGraphic);

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                final int score = result.getFace().getScore();
                new AlertDialog.Builder(PreviewActivity.this)
                        .setTitle("Detection Completed")
                        .setMessage("SCORE:" + String.valueOf(score))
                        .setPositiveButton("OK", null)
                        .show();
            }
        });
    }

    @Override
    public void detectionFailed(final Detector.Exception e) {
        mGraphicOverlay.remove(mFaceGraphic);

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                new AlertDialog.Builder(PreviewActivity.this)
                        .setTitle("Detection Failed")
                        .setMessage(e.getClass().getName())
                        .setPositiveButton("Close", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {
                                PreviewActivity.this.finish();
                            }
                        })
                        .show();
            }
        });
    }
}
