package arukoh.demo;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;

import arukoh.demo.empty.EmptyActivity;

public class CardRecyclerView extends RecyclerView {
    public CardRecyclerView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        LinearLayoutManager llm = new LinearLayoutManager(context);
        setLayoutManager(llm);

        Class[] classArray = { EmptyActivity.class };
        setAdapter(new CardRecyclerAdapter(context, classArray));
    }
}
