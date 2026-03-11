package es.dawgroup17.nexgym.model;

import java.sql.Blob;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Lob;
import jakarta.persistence.Transient;
import jakarta.persistence.Id;

@Entity
public class Image {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @Lob
    private Blob image;

    private String text;

    @Transient // Is not saved in the data base, just used for passing info to the template
               // (indicating is the first)
    private boolean isFirst;

    public Image() {
    }

    public Image(Blob image, String text) {
        super();
        this.image = image;
        this.text = text;
    }

    public long getId() {
        return id;
    }

    public Blob getImage() {
        return image;
    }

    public void setImage(Blob image) {
        this.image = image;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public boolean isFirst() {
        return isFirst;
    }
    public boolean getIsFirst() {
        return isFirst;
    }

    public void setIsFirst(boolean isFirst) {
        this.isFirst = isFirst;
    }

    @Override
    public String toString() {
        return "Image [" +
                "id=" + id +
                ", text='" + text + ']';
    }

}