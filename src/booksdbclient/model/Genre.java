package booksdbclient.model;

public enum Genre {

    HORROR("Horror"), DRAMA("drama"), ROMANCE("romance"), SCIENCE("science"), FANTASY("fantasy");

    private String value;

    private Genre(final String value) {
        this.value = value;
    }

    public String getValue() {
        return this.value;
    }
}
