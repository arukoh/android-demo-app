package arukoh.demo.camera.controller;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import arukoh.demo.R;
import arukoh.demo.camera.model.Detection;
import io.realm.OrderedRealmCollection;
import io.realm.RealmRecyclerViewAdapter;

class DetectionAdapter extends RealmRecyclerViewAdapter<Detection, DetectionAdapter.DetectionViewHolder> {
    private OrderedRealmCollection<Detection> objects;

    public DetectionAdapter(@Nullable OrderedRealmCollection<Detection> data) {
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
        final Detection obj = getItem(position);
        //noinspection ConstantConditions
        holder.timestamp.setText(obj.getTimestamp().toString());
        holder.score.setText(obj.getScore());
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
}
