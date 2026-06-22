package lld.practice.infra.notificationservice;

import lld.practice.infra.notificationservice.api.NotificationFacade;
import lld.practice.infra.notificationservice.dto.NotificationRequest;
import lld.practice.infra.notificationservice.dto.NotificationResponse;
import lld.practice.infra.notificationservice.enums.Channel;
import lld.practice.infra.notificationservice.enums.NotificationCategory;
import lld.practice.infra.notificationservice.enums.Priority;
import lld.practice.infra.notificationservice.model.Device;
import lld.practice.infra.notificationservice.model.Template;
import lld.practice.infra.notificationservice.model.User;
import lld.practice.infra.notificationservice.model.UserPreference;

import java.time.LocalTime;
import java.time.ZoneId;
import java.util.HashSet;
import java.util.Set;

public class NotificationMain {

    public static void main(String[] args) throws InterruptedException {
        NotificationFacade nf = NotificationFacade.getInstance();

        // 1. Seed user + device
        User alice = new User("u1", "Alice", "alice@example.com", "+15551234567", "en",
                ZoneId.of("America/Los_Angeles"));
        alice.addDevice(new Device("d1", "u1", Device.Platform.IOS, "ios-token-abc"));
        nf.registerUser(alice);

        // 2. Register templates
        nf.registerTemplate(new Template("t1", "ORDER_SHIPPED", Channel.EMAIL, "en", 1,
                "Your order #{{orderId}} has shipped",
                "Hi {{name}}, your order #{{orderId}} is on its way. Tracking: {{tracking}}"));
        nf.registerTemplate(new Template("t2", "ORDER_SHIPPED", Channel.SMS, "en", 1,
                null,
                "Hi {{name}}, order #{{orderId}} shipped. Track: {{tracking}}"));
        nf.registerTemplate(new Template("t3", "ORDER_SHIPPED", Channel.PUSH, "en", 1,
                "Order shipped",
                "Order #{{orderId}} is on its way."));
        nf.registerTemplate(new Template("t4", "ORDER_SHIPPED", Channel.IN_APP, "en", 1,
                "Order shipped",
                "Your order #{{orderId}} has been shipped."));
        nf.registerTemplate(new Template("t5", "PROMO_SUMMER", Channel.EMAIL, "en", 1,
                "Summer sale just for you, {{name}}",
                "Take {{percent}}% off this week only."));

        // 3. Preferences: opt out of promotional SMS, allow everything else
        UserPreference promoSms = new UserPreference("u1", NotificationCategory.PROMOTIONAL, Channel.SMS, false);
        nf.savePreference(promoSms);

        UserPreference promoEmail = new UserPreference("u1", NotificationCategory.PROMOTIONAL, Channel.EMAIL, true);
        promoEmail.setQuietHours(LocalTime.of(22, 0), LocalTime.of(7, 0));
        nf.savePreference(promoEmail);

        // 4. Send a transactional notification across multiple channels
        Set<Channel> channels = new HashSet<>();
        channels.add(Channel.EMAIL);
        channels.add(Channel.SMS);
        channels.add(Channel.PUSH);
        channels.add(Channel.IN_APP);

        NotificationRequest txReq = NotificationRequest.builder()
                .tenantId("acme")
                .userId("u1")
                .typeCode("ORDER_SHIPPED")
                .category(NotificationCategory.TRANSACTIONAL)
                .channels(channels)
                .priority(Priority.P0)
                .dedupeKey("order-9001-shipped")
                .addPayload("name", "Alice")
                .addPayload("orderId", "9001")
                .addPayload("tracking", "1Z999AA10123456784")
                .build();

        NotificationResponse txResp = nf.send(txReq);
        System.out.println(">> Submitted transactional: " + txResp);

        // 5. Idempotency: submit the same dedupe_key — should return existing id
        NotificationResponse dup = nf.send(txReq);
        System.out.println(">> Duplicate submit: " + dup);

        // 6. Send a promo — SMS will be suppressed, EMAIL may be suppressed during quiet hours
        Set<Channel> promoChannels = new HashSet<>();
        promoChannels.add(Channel.EMAIL);
        promoChannels.add(Channel.SMS);

        NotificationRequest promoReq = NotificationRequest.builder()
                .tenantId("acme")
                .userId("u1")
                .typeCode("PROMO_SUMMER")
                .category(NotificationCategory.PROMOTIONAL)
                .channels(promoChannels)
                .priority(Priority.P3)
                .addPayload("name", "Alice")
                .addPayload("percent", 20)
                .build();

        NotificationResponse promoResp = nf.send(promoReq);
        System.out.println(">> Submitted promo: " + promoResp);

        // 7. Wait for workers to drain
        Thread.sleep(800);

        // 8. Inspect delivery status
        System.out.println();
        System.out.println("=== Status: transactional ===");
        nf.getStatus(txResp.getNotificationId()).forEach(System.out::println);

        System.out.println();
        System.out.println("=== Status: promo ===");
        nf.getStatus(promoResp.getNotificationId()).forEach(System.out::println);

        nf.shutdown();
    }
}
