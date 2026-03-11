package es.dawgroup17.nexgym.model;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;

@Entity(name = "ClassTable")
public class GymClass {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String name;

    private String coachName;

    @Column(columnDefinition = "TEXT")
    private String description;
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Image> images = new ArrayList<>();

    @OneToMany(cascade = CascadeType.ALL)
    private List<Comment> comments = new ArrayList<>();

    @OneToMany(mappedBy = "gymClass", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Session> sessions = new ArrayList<>();

    public GymClass() {

    }

    public GymClass(String name, String coachName, String description, List<Image> images, List<Session> sessions) {
        super();
        this.name = name;
        this.coachName = coachName;
        this.description = description;
        this.images = images != null ? images : new ArrayList<>();
        this.sessions = sessions != null ? sessions : new ArrayList<>();
        this.comments = new ArrayList<>();
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCoachName() {
        return coachName;
    }

    public void setCoachName(String coachName) {
        this.coachName = coachName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<Image> getImages() {
        return images;
    }

    public void setImages(List<Image> images) {
        this.images = images;
    }

    public List<Comment> getComments() {
        return comments;
    }

    public void setComments(Comment comment) {
        if (this.comments == null) {
            this.comments = new ArrayList<>();
        }
        this.comments.add(comment);
    }

    public List<Session> getSessions() {
        return sessions;
    }

    public void setSessions(Session session) {
        if (this.sessions == null) {
            this.sessions = new ArrayList<>();
        }
        this.sessions.add(session);
    }

    public void setAllSessions(List<Session> sessions) {
        this.sessions = sessions != null ? sessions : new ArrayList<>();
    }

    public void setSessions(List<Session> sessions) {
        this.sessions = sessions != null ? sessions : new ArrayList<>();
    }

}