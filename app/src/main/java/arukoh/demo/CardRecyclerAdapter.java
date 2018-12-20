package arukoh.demo;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class CardRecyclerAdapter extends RecyclerView.Adapter<CardRecyclerAdapter.ViewHolder> {
    private Class[] list;
    private Context context;

    public CardRecyclerAdapter(Context context, Class[] classArray) {
        super();
        this.list = classArray;
        this.context = context;
    }

    @Override
    public int getItemCount() {
        return list.length;
    }

    @Override
    public void onBindViewHolder(ViewHolder vh, final int position) {
        String name = list[position].getSimpleName().replace("Activity", "");
        vh.textView.setText(name);
        vh.imageView.setImageResource(R.mipmap.ic_launcher);
        vh.layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, list[position]);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public CardRecyclerAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View v = layoutInflater.inflate(R.layout.layout_recycler, parent, false);
        ViewHolder viewHolder = new ViewHolder(v);
        return viewHolder;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView textView;
        LinearLayout layout;
        ImageView imageView;

        public ViewHolder(View v) {
            super(v);
            textView = v.findViewById(R.id.textView);
            layout = v.findViewById(R.id.layout);
            imageView = v.findViewById(R.id.imageView);
        }
    }
}
