package arukoh.demo.camera.results;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import arukoh.demo.R;
import arukoh.demo.camera.model.Result;
import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.Sort;

public class ResultsFragment extends Fragment {

    private RecyclerView mRecyclerView;
    private ResultsAdapter mAdapter;
    private Realm realm;

    public static ResultsFragment newInstance() {
        ResultsFragment fragment = new ResultsFragment();
        // Bundle args = new Bundle();
        // fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_detection_list, container, false);

        realm = Realm.getDefaultInstance();
        final RealmResults<Result> result = realm
                .where(Result.class)
                .sort("timestamp", Sort.DESCENDING)
                .findAll();
        mAdapter = new ResultsAdapter(result);

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
