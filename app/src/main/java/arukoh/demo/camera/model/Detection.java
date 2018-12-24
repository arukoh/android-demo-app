package arukoh.demo.camera.model;

import java.util.Date;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class Detection extends RealmObject {

    @PrimaryKey
    private String id;
    private Date timestamp;
    private int score;

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public Date getTimestamp() { return timestamp; }
    public void setTimestamp(Date timestamp) { this.timestamp = timestamp; }

    public int getScore() { return score; }
    public void setScore(int score) { this.score = score; }

}
