package lld.practice.urlshortner;

import lld.practice.urlshortner.service.URLShortenerService;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

public class Application {

    private static final ExecutorService executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors() + 1);


    public static void main(String[] args) throws InterruptedException {
        int totalThreads = 1000;
        CountDownLatch latch = new CountDownLatch(totalThreads);
        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger failedCount = new AtomicInteger(0);
        AtomicInteger count = new AtomicInteger(0);
        long startTime = System.currentTimeMillis();
        for (int i = 0; i < totalThreads; i++) {
            executorService.submit(() -> {
                try {
                    String url = "http://sanjul.com/sanjul.com/" + count.incrementAndGet();
                    String shortUrl = URLShortenerService.getInstance().shorten(url, null, "123");
                    successCount.incrementAndGet();
                    System.out.println(shortUrl + ":\t" + url);
                } catch (Exception eX) {
                    eX.printStackTrace();
                    failedCount.incrementAndGet();
                }
                latch.countDown();
            });
        }

        latch.await();
        long endTime = System.currentTimeMillis();


        System.out.printf("Total time taken to shorten %s urls: %d second%n", totalThreads, TimeUnit.MILLISECONDS.toSeconds(endTime - startTime));
        System.out.println("Total ");
        System.out.println("Total failed " + failedCount.get());
        System.out.println("Total success " + successCount.get());

        System.out.println("Total shortUrls "  + URLShortenerService.getInstance().totalCount());

        Thread.sleep(10000);
        System.out.println("Total shortUrls "  + URLShortenerService.getInstance().totalCount());

    }
}
