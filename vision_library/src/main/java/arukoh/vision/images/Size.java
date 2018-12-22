package arukoh.vision.images;

public class Size {

    private com.google.android.gms.common.images.Size mDelegate;

    public Size(com.google.android.gms.common.images.Size delegate) {  mDelegate = delegate; }

    public final int getWidth() { return mDelegate.getWidth(); }

    public final int getHeight() {
        return mDelegate.getHeight();
    }

    public final boolean equals(Object obj) { return mDelegate.equals(obj); }

    public final String toString() { return mDelegate.toString(); }

    public final int hashCode() {
        return mDelegate.hashCode();
    }

    public static Size parseSize(String size) throws NumberFormatException {
        return new Size(com.google.android.gms.common.images.Size.parseSize(size));
    }
}
