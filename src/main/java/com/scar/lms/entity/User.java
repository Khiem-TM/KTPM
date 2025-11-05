package com.scar.lms.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.proxy.HibernateProxy;

import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "USERS")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "USERNAME", nullable = false, unique = true)
    @NonNull
    private String username;

    @Column(name = "PASSWORD")
    private String password;

    @Column(name = "DISPLAY_NAME")
    @NonNull
    private String displayName;

    @Column(name = "EMAIL", unique = true)
    private String email;

    @Column(name = "ROLE", nullable = false)
    @NonNull
    private Role role;

    @Column(name = "USER_POINTS")
    private long points = 0;

    @Column(name = "PROFILE_PICTURE_URL")
    private String profilePictureUrl;

    @Column(name = "ABOUT_ME")
    private String aboutMe;

    @OneToMany(fetch = FetchType.EAGER,
            mappedBy = "user")
    private Set<Borrow> borrows = new HashSet<>();
    public Set<Borrow> getBorrows() {
        return borrows;
    }
    public void setBorrows(Set<Borrow> borrows) {
        this.borrows = borrows;
    }

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "FAVOURITES",
            joinColumns = { @JoinColumn(name = "USER_ID") },
            inverseJoinColumns = { @JoinColumn(name = "BOOK_ID") })
    private Set<Book> favouriteBooks = new HashSet<>();
    public Set<Book> getFavouriteBooks() {
        return favouriteBooks;
    }
    public void setFavouriteBooks(Set<Book> favouriteBooks) {
        this.favouriteBooks = favouriteBooks;
    }

    @OneToMany(fetch = FetchType.EAGER,
            mappedBy = "user")
    private Set<Notify> notifies = new HashSet<>();
    public Set<Notify> getNotifys() {
        return notifies;
    }
    public void setNotifys(Set<Notify> notifies) {
        this.notifies = notifies;
    }

    @OneToMany(fetch = FetchType.EAGER,
            mappedBy = "user")
    private Set<Rating> ratings = new HashSet<>();
    public Set<Rating> getRatings() {
        return ratings;
    }
    public void setRatings(Set<Rating> ratings) {
        this.ratings = ratings;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return id == user.id;
    }

    @Override
    public final int hashCode() {
        return this instanceof HibernateProxy
                ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass().hashCode()
                : getClass().hashCode();
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public long getPoints() {
        return points;
    }

    public void setPoints(long points) {
        this.points = points;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getProfilePictureUrl() {
        return profilePictureUrl;
    }

    public void setProfilePictureUrl(String profilePictureUrl) {
        this.profilePictureUrl = profilePictureUrl;
    }

    public String getAboutMe() {
        return aboutMe;
    }

    public void setAboutMe(String aboutMe) {
        this.aboutMe = aboutMe;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPassword() {
        return password;
    }
}