package arukoh.demo.camera.results;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;

import arukoh.demo.R;
import arukoh.demo.camera.model.Result;
import io.realm.OrderedRealmCollection;
import io.realm.RealmRecyclerViewAdapter;

class ResultsAdapter extends RealmRecyclerViewAdapter<Result, ResultsAdapter.DetectionViewHolder> {
    private OrderedRealmCollection<Result> objects;

    public ResultsAdapter(@Nullable OrderedRealmCollection<Result> data) {
        super(data, true);
        this.objects = data;
        setHasStableIds(true);
    }

    @NonNull
    @Override
    public DetectionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_detection, parent, false);
        return new DetectionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DetectionViewHolder holder, int position) {
        final Result obj = getItem(position);
        //noinspection ConstantConditions
        holder.timestamp.setText(toISO8601(obj.getTimestamp()));
        holder.score.setText(String.valueOf(obj.getScore()));
    }

    @Override
    public int getItemCount() {
        return objects.size();
    }

    static class DetectionViewHolder extends RecyclerView.ViewHolder {
        TextView timestamp;
        TextView score;

        public DetectionViewHolder(@NonNull View view) {
            super(view);
            timestamp = view.findViewById(R.id.textViewTimestamp);
            score = view.findViewById(R.id.textViewScore);
        }
    }

    private String toISO8601(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        return sdf.format(date);
    }
}
