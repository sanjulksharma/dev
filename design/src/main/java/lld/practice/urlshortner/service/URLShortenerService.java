package lld.practice.urlshortner.service;

import lld.practice.urlshortner.entity.ShortURLDetails;
import lld.practice.urlshortner.store.URLRepository;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Base64;
import java.util.Set;

public class URLShortenerService {

    private static final String DOMAIN = "https://url.shortener.com/";
    private static final URLShortenerService INSTANCE = new URLShortenerService();
    private final URLRepository urlRepository;

    private URLShortenerService() {
        urlRepository = new URLRepository();
    }

    public static URLShortenerService getInstance() {
        return INSTANCE;
    }

    public String shorten(String originalURL, String alias, String userId) {
        validateUrl(originalURL);

        String shortUrl;
        if (alias == null || alias.isBlank()) {
            shortUrl = DOMAIN + new String(Base64.getUrlEncoder().encode(originalURL.getBytes()));
        } else {
            shortUrl = DOMAIN + alias;
            validateDuplicateURL(shortUrl);
        }

        ShortURLDetails shortURLDetails = urlRepository.getShortURLDetails(shortUrl);
        if (shortURLDetails != null) {
            return shortURLDetails.getShortURL();
        }
        ShortURLDetails.Builder builder = new ShortURLDetails.Builder().shortURL(shortUrl).originalURL(originalURL).user(userId);
        do {
        } while (!urlRepository.saveShortUrlDetails(builder.build()));
        return shortUrl;
    }

    public int totalCount() {
        return urlRepository.totalCount();
    }

    public Set<String> getShortURLs() {
        return urlRepository.getShortURLs();
    }

    private void validateUrl(String urlStr) {

        if (urlStr == null || urlStr.isBlank()) {
            System.out.println("Original URL is null or blank");
            throw new RuntimeException("Original URL is null or blank");
        }

        try {
            new URL(urlStr);
        } catch (MalformedURLException e) {
            System.out.println("Malformed URL: " + urlStr + " exception: " + e);
            throw new RuntimeException(e);
        }
    }

    private void validateDuplicateURL(String urlStr) {
        if (urlStr == null || urlStr.isBlank()) {
            return;
        }

        ShortURLDetails shortURLDetails = urlRepository.getShortURLDetails(urlStr);
        if (shortURLDetails != null) {
            throw new RuntimeException("URL: " + urlStr + " already redirected to " + shortURLDetails.getOriginalURL());
        }
    }

    public ShortURLDetails getURL(String url) {
        return urlRepository.getShortURLDetails(url);
    }
}
