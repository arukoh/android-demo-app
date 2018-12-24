package arukoh.demo.camera;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import arukoh.demo.R;
import arukoh.demo.camera.model.Migration;
import arukoh.demo.camera.model.Result;
import arukoh.demo.camera.preview.PreviewFragment;
import arukoh.demo.camera.results.ResultsFragment;
import arukoh.vision.Detector;
import io.realm.Realm;
import io.realm.RealmConfiguration;

public class PreviewActivity extends AppCompatActivity implements PreviewFragment.OnFragmentInteractionListener {
    private static final String TAG = "PreviewActivity";

    BottomNavigationView mNavigation;
    FragmentManager mFragmentManager = getSupportFragmentManager();

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = item -> {
                switch (item.getItemId()) {
                    case R.id.navigation_home:
                        this.finish();
                        return true;
                    case R.id.navigation_preview:
                        mFragmentManager.beginTransaction()
                                .replace(R.id.fragmentContainer, PreviewFragment.newInstance())
                                .commit();
                        return true;
                    case R.id.navigation_results:
                        mFragmentManager.beginTransaction()
                                .replace(R.id.fragmentContainer, ResultsFragment.newInstance())
                                .commit();
                        return true;
                }
                return false;
            };

    private static final int RC_HANDLE_CAMERA_PERM = 2;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preview);

        mNavigation = findViewById(R.id.navigation);
        mNavigation.setSelectedItemId(R.id.navigation_preview);
        mNavigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        int rc = ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA);
        if (rc != PackageManager.PERMISSION_GRANTED) {
            requestCameraPermission();
        }

        Realm.init(this);
        RealmConfiguration config = new RealmConfiguration.Builder()
                .name("default.realm")
                .schemaVersion(1)
                .migration(new Migration())
                .build();
        // Realm.deleteRealm(config);
        Realm.setDefaultConfiguration(config);

        mFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, PreviewFragment.newInstance())
                .commit();
    }

    private void requestCameraPermission() {
        Log.w(TAG, "Camera permission is not granted. Requesting permission");

        final String[] permissions = new String[]{Manifest.permission.CAMERA};

        if (!ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.CAMERA)) {
            ActivityCompat.requestPermissions(this, permissions, RC_HANDLE_CAMERA_PERM);
            return;
        }

        final Activity thisActivity = this;

        View.OnClickListener listener = view -> ActivityCompat.requestPermissions(thisActivity, permissions, RC_HANDLE_CAMERA_PERM);

        BottomNavigationView navigation = findViewById(R.id.navigation);
        Snackbar.make(navigation, R.string.permission_camera_rationale, Snackbar.LENGTH_INDEFINITE)
                .setAction(R.string.ok, listener)
                .show();
    }

    @Override
    public void onPreviewFinished(Detector.Result result) {
        Realm realm = Realm.getDefaultInstance();
        realm.executeTransaction(r -> {
            arukoh.vision.core.Face face = result.getFace();
            String id = String.valueOf(result.getFinishEpochSecond());
            Result obj = r.createObject(Result.class, id);
            obj.setTimestamp(new java.util.Date(result.getFinishEpochSecond() * 1000));
            obj.setScore(face.getScore());
        });
        runOnUiThread(() -> mNavigation.setSelectedItemId(R.id.navigation_results));
    }

}
