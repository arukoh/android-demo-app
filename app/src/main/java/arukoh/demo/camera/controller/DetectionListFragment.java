package arukoh.demo.camera.controller;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import arukoh.demo.R;
import arukoh.demo.camera.model.Detection;
import io.realm.Realm;
import io.realm.RealmResults;

public class DetectionListFragment extends Fragment {

    private RecyclerView mRecyclerView;
    private DetectionAdapter mAdapter;
    private Realm realm;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_detection_list, container, false);

        realm = Realm.getDefaultInstance();
        final RealmResults<Detection> result = realm.where(Detection.class).findAll();
        mAdapter = new DetectionAdapter(result);

        mRecyclerView = view.findViewById(R.id.recyclerViewDetectionList);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL));

        // TODO swipe

        return view;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mRecyclerView.setAdapter(null);
        realm.close();
    }

}
