package es.dawgroup17.nexgym.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;

@Entity
public class Session {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    private LocalDateTime date;

    @ManyToMany(mappedBy = "userSessions")
    private List<AppUser> signedUsers;

    @ManyToOne // Bidirectional relationship to class (session knows the class and the class
               // knows the sessions)
    private GymClass gymClass;

    private int capacity;
    private int joined;

    public Session() {

    }

    public Session(LocalDateTime date, int capacity, GymClass gymClass) {
        this.date = date;
        if (capacity > 0) {
            this.capacity = capacity;
        } else
            this.capacity = 20;
        this.joined = 0;
        this.gymClass = gymClass;
        this.signedUsers = new ArrayList<AppUser>(this.capacity);
    }

    // getters
    public LocalDateTime getDate() {
        return this.date;
    }

    public int getCapacity() {
        return this.capacity;
    }

    public int getMembers() {
        return this.joined;
    }

    public List<AppUser> getSignedUsers() {
        return this.signedUsers;
    }

    public int getJoined() {
        return this.joined;
    }

    public GymClass getGymClass() {
        return gymClass;
    }

    // setters
    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public void setJoined(int joined) {
        this.joined = joined;
    }

    public void setGymClass(GymClass gymClass) {
        this.gymClass = gymClass;
    }

    public void increaseJoined() {
        this.joined++;
    }

    public void decreaseJoined() {
        this.joined--;
    }

    public boolean addUser(AppUser user) {
        if (this.joined < this.capacity) {
            return this.signedUsers.add(user);
        }
        return false;
    }

    public boolean removeUser(AppUser user) {
        return this.signedUsers.remove(user);
    }

    // Auxiliar getters and setters

    public String getDateFormatted() {
        return date.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
    }

    public String getTimeFormatted() {
        return date.format(DateTimeFormatter.ofPattern("HH:mm"));
    }

    // Mustache-friendly getters for nested objects (Mustache doesn't support dot
    // notation)
    public Long getGymClassId() {
        return gymClass != null ? gymClass.getId() : null;
    }

    public String getGymClassName() {
        return gymClass != null ? gymClass.getName() : "";
    }

    public String getGymClassDescription() {
        return gymClass != null ? gymClass.getDescription() : "";
    }

    public int getPercentFilled() {
        if (capacity == 0)
            return 0;
        return (int) ((joined * 100.0) / capacity);
    }

    @jakarta.persistence.Transient
    private boolean signedUp = false;

    public boolean isSignedUp() {
        return signedUp;
    }

    public void setSignedUp(boolean signedUp) {
        this.signedUp = signedUp;
    }

    @jakarta.persistence.Transient
    private boolean hasConflict = false;

    public boolean isHasConflict() {
        return hasConflict;
    }

    public void setHasConflict(boolean hasConflict) {
        this.hasConflict = hasConflict;
    }

}