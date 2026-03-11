package es.dawgroup17.nexgym.model;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Transient;

@Entity
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @ManyToOne // Bidirectional relationship to class (comment knows the class and the class
               // knows the comment)
    private GymClass gymClass;

    @ManyToOne // Bidirectional relationship to user (comment knows the user and the user knows
               // the comment)
    private AppUser appUser;

    private int rating;
    private String comment;

    @Transient // This means it won't be stored in the data base, just used temporarily
    private boolean isOwner; // This variable is important for differentiate if a user is owner of the
                             // comment or not (if it has the rights to update and delete each comment)

    public Comment() {
    }

    public Comment(AppUser appUser, int rating, String comment) {
        super();
        this.appUser = appUser;
        this.rating = rating;
        this.comment = comment;
        this.isOwner = false;
    }

    // getters
    public long getId() {
        return this.id;
    }

    public GymClass getGymClass() {
        return this.gymClass;
    }

    public AppUser getAppUser() {
        return this.appUser;
    }

    public int getRating() {
        return this.rating;
    }

    public String getComment() {
        return this.comment;
    }

    // setters
    public void setId(long id) {
        this.id = id;
    }

    public void setGymClass(GymClass gymClass) {
        this.gymClass = gymClass;
    }

    public void setAppUser(AppUser appUser) {
        this.appUser = appUser;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public List<Boolean> getStars() {
        List<Boolean> stars = new ArrayList<>();
        for (int i = 1; i <= 5; i++) {
            stars.add(i <= this.rating);
        }
        return stars;
    }

    public void setIsOwner(boolean isOwner) {
        this.isOwner = isOwner;
    }

    public boolean getIsOwner() {
        return this.isOwner;
    }

    // Mustache-friendly getters for nested appUser object
    public String getAppUserName() {
        return appUser != null ? appUser.getName() : "";
    }

    public String getAppUserSurname() {
        return appUser != null ? appUser.getSurname() : "";
    }

    public Image getAppUserImage() {
        return appUser != null ? appUser.getImage() : null;
    }

    public Long getGymClassId() {
        return gymClass != null ? gymClass.getId() : null;
    }
}