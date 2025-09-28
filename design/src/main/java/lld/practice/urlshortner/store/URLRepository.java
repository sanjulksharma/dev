package lld.practice.urlshortner.store;

import lld.practice.urlshortner.entity.ShortURLDetails;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

public class URLRepository {

    private final Map<String, ShortURLDetails> shortURLs;
    private final ConcurrentHashMap<String, ReentrantLock> locks;

    public URLRepository() {
        shortURLs = new HashMap<>();
        locks = new ConcurrentHashMap<>();
        init();
    }

    public int totalCount() {
        return shortURLs.size();
    }

    public Set<String> getShortURLs() {
        return shortURLs.keySet();
    }

    public ShortURLDetails getShortURLDetails(String shortUrl) {
        return shortURLs.get(shortUrl);
    }

    public boolean saveShortUrlDetails(ShortURLDetails details) {
        ReentrantLock lock = getLock(details.getOriginalURL());

        try {
            lock.tryLock(2, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            System.out.println("Timeout while waiting for lock");
            return false;
        }

        try {
            shortURLs.put(details.getShortURL(), details);
        } finally {
            lock.unlock();
        }
        return true;
    }

    private void init() {
        ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
        executorService.scheduleAtFixedRate(() -> {
            Collection<ShortURLDetails> shortURLDetails = shortURLs.values();
            for (ShortURLDetails shortUrlDetails : shortURLDetails) {
                if (shortUrlDetails.getExpiryTime() <= System.currentTimeMillis()) {
                    System.out.println(shortUrlDetails.getShortURL() + " is expired");
                    ReentrantLock lock = getLock(shortUrlDetails.getOriginalURL());
                    try {
                        lock.tryLock(2, TimeUnit.SECONDS);
                        shortURLs.remove(shortUrlDetails.getShortURL());
                    } catch (InterruptedException e) {
                    } finally {
                        lock.unlock();
                    }

                }
            }
        }, 0, 2, TimeUnit.SECONDS);
    }


    private ReentrantLock getLock(String shortUrl) {
        return locks.computeIfAbsent(shortUrl, s -> new ReentrantLock());
    }
}
