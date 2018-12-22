package arukoh.vision.core;

import android.graphics.Bitmap;
import android.graphics.PointF;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class Face {
    private final int mId;
    private PointF mPosition;
    private float mWidth;
    private float mHeight;
    private List<HashMap<Long, Float>> mData;
    private int mScore;

    public Face(int id) {
        mId = id;
        reset();
    }

    public int getId() { return mId; }

    public PointF getPosition() { return mPosition; }

    public float getWidth() {
        return mWidth;
    }

    public float getHeight() {
        return mHeight;
    }

    public int getScore() { return mScore; }

    public void update(long timestamp, Bitmap bitmap, PointF position, float width, float height) {
        update(position, width, height);

        HashMap<Long, Float> raw = new HashMap<>();
        raw.put(timestamp, 1.0F);
        mData.add(raw);
    }

    public void reset() {
        update(new PointF(), 0, 0);
        mData = new ArrayList<>();
        mScore = 0;
    }

    public void finalize() {
        mScore = mData.size();
    }

    private void update(PointF position, float width, float height) {
        mPosition = position;
        mWidth = width;
        mHeight = height;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Face faceI = (Face) o;
        return mId == faceI.mId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(mId);
    }
}
