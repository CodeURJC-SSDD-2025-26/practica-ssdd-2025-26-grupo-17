package es.dawgroup17.nexgym.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.CascadeType;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.FetchType;
import jakarta.persistence.Column;

@Entity
public class AppUser {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    private String name;
    private String surname;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String encodedPassword;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate birthdate;
    private String phone;

    @OneToOne(cascade = { CascadeType.MERGE, CascadeType.REFRESH })
    private Image image;

    // This are the roles that a user can have, the admin is going to have "user"
    // and "admin", the user only "user"
    // Is used for the security of the app

    @ElementCollection(fetch = FetchType.EAGER)
    private List<String> roles = new ArrayList<>();

    @ManyToMany
    private List<Session> userSessions = new ArrayList<>();

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "appUser")

    private List<Comment> comments = new ArrayList<>();

    public AppUser() {
    }

    public AppUser(String name, String surname, String email, String encodedPassword, LocalDate birthdate, String phone,
            String... roles) {
        this.name = name;
        this.surname = surname;
        this.email = email;
        this.encodedPassword = encodedPassword;
        this.birthdate = birthdate;
        this.phone = phone;

        this.roles = new ArrayList<>();
        if (roles != null) {
            for (String role : roles) {
                this.roles.add(role);
            }
        }
    }

    // GETTERS OF THE CLASS
    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getSurname() {
        return surname;
    }

    public String getEmail() {
        return email;
    }

    public String getEncodedPassword() {
        return encodedPassword;
    }

    public LocalDate getBirthdate() {
        return birthdate != null ? birthdate : LocalDate.of(2000, 1, 1);
    }

    public String getPhone() {
        return phone != null ? phone : "";
    }

    public Image getImage() {
        return image;
    }

    public List<String> getRoles() {
        return this.roles;
    }

    public List<Session> getUserSessions() {
        return userSessions;
    }

    public List<Comment> getComments() {
        return this.comments;
    }

    // SETTERS OF THE CLASS
    public void setSession(Session session) {
        userSessions.add(session);
    }

    public void setComment(Comment comment) {
        comments.add(comment);
    }

    public void setRoles(String... roles) {
        this.roles = new ArrayList<>(List.of(roles));
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setEncodedPassword(String encodedPassword) {
        this.encodedPassword = encodedPassword;
    }

    public void setBirthdate(LocalDate birthdate) {
        this.birthdate = birthdate;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setImage(Image image) {
        this.image = image;
    }

    public void removeSession(Session session) {
        userSessions.remove(session);
    }

}
