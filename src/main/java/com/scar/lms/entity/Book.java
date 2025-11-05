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
@Table(name = "BOOKS")
public class Book {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }

    @Column(name = "ISBN", length = 20)
    private String isbn;
    public String getIsbn() {
        return isbn;
    }
    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    @Column(name = "TITLE", length = 100, nullable = false)
    @NonNull
    private String title;
    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }

    @Column(name = "LANGUAGE", length = 50)
    private String language;
    public String getLanguage() {
        return language;
    }
    public void setLanguage(String language) {
        this.language = language;
    }

    @Column(name = "RATING", length = 10)
    private Double rating = 0D;
    public Double getRating() {
        return rating;
    }
    public void setRating(Double rating) {
        this.rating = rating;
    }

    @Column(name = "PUBLICATION_YEAR", length = 10)
    private Integer publicationYear;
    public Integer getPublicationYear() {
        return publicationYear;
    }
    public void setPublicationYear(Integer publicationYear) {
        this.publicationYear = publicationYear;
    }

    @Column(name = "DESCRIPTION", columnDefinition = "LONGTEXT")
    private String description;
    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }

    @Column(name = "IMAGE_URL")
    private String imageUrl;
    public String getImageUrl() {
        return imageUrl;
    }
    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    @Column(name = "BORROW_COUNT")
    private int borrowCount = 0;
    public int getBorrowCount() {
        return borrowCount;
    }
    public void setBorrowCount(int borrowCount) {
        this.borrowCount = borrowCount;
    }

    @Column(name = "AUTHOR")
    private String author;
    public String getAuthor() {
        return author;
    }
    public void setAuthor(String author) {
        this.author = author;
    }

    @Column(name = "GENRE")
    private String genre;
    public String getGenre() {
        return genre;
    }
    public void setGenre(String genre) {
        this.genre = genre;
    }

    @Column(name = "PUBLISHER")
    private String publisher;
    public String getPublisher() {
        return publisher;
    }
    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    @ManyToMany(fetch = FetchType.EAGER, mappedBy = "favouriteBooks",
            cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
    private Set<User> favouriteUsers = new HashSet<>();
    public Set<User> getFavouriteUsers() {
        return favouriteUsers;
    }
    public void setFavouriteUsers(Set<User> favouriteUsers) {
        this.favouriteUsers = favouriteUsers;
    }

    @OneToMany(fetch = FetchType.EAGER,
            cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH},
            mappedBy = "book")
    private Set<Borrow> borrows = new HashSet<>();
    public Set<Borrow> getBorrows() {
        return borrows;
    }
    public void setBorrows(Set<Borrow> borrows) {
        this.borrows = borrows;
    }

    @OneToMany(fetch = FetchType.EAGER,
            cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH},
            mappedBy = "book")
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
        Book book = (Book) o;
        return id == book.id;
    }

    @Override
    public final int hashCode() {
        return this instanceof HibernateProxy
                ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass().hashCode()
                : getClass().hashCode();
    }
}