package lld.practice.urlshortner.entity;

import java.util.concurrent.TimeUnit;

public class ShortURLDetails {

    private String originalURL;
    private String shortURL;
    private String user;
    private long creationTime;
    private long expiryTime;

    public long getCreationTime() {
        return creationTime;
    }

    public void setCreationTime(long creationTime) {
        this.creationTime = creationTime;
    }

    public long getExpiryTime() {
        return expiryTime;
    }

    public void setExpiryTime(long expiryTime) {
        this.expiryTime = expiryTime;
    }

    public String getOriginalURL() {
        return originalURL;
    }

    public void setOriginalURL(String originalURL) {
        this.originalURL = originalURL;
    }

    public String getShortURL() {
        return shortURL;
    }

    public void setShortURL(String shortURL) {
        this.shortURL = shortURL;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    @Override
    public String toString() {
        return "ShortURLDetails{" + "\n" +
                "creationTime=" + creationTime + "'\n" +
                ", originalURL='" + originalURL + "'\n" +
                ", shortURL='" + shortURL + "'\n" +
                ", user='" + user + "'\n" +
                ", expiryTime=" + expiryTime + "'\n" +
                '}';
    }

    public static class Builder {
        private String originalURL;
        private String shortURL;
        private String user;

        public Builder originalURL(String originalURL) {
            this.originalURL = originalURL;
            return this;
        }

        public Builder shortURL(String shortURL) {
            this.shortURL = shortURL;
            return this;
        }

        public Builder user(String user) {
            this.user = user;
            return this;
        }

        public ShortURLDetails build() {
            ShortURLDetails details = new ShortURLDetails();
            details.setOriginalURL(originalURL);
            details.setShortURL(shortURL);
            details.setUser(user);
            details.setCreationTime(System.currentTimeMillis());
            details.setExpiryTime(details.getCreationTime() + TimeUnit.SECONDS.toMillis(6));
            return details;
        }
    }
}
