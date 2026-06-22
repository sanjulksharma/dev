package lld.practice.infra.notificationservice.repository;

import lld.practice.infra.notificationservice.enums.Channel;
import lld.practice.infra.notificationservice.model.Template;

import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class TemplateRepository {
    private final ConcurrentMap<String, Template> store = new ConcurrentHashMap<>();

    public void save(Template t) { store.put(key(t.getTypeCode(), t.getChannel(), t.getLocale()), t); }

    public Optional<Template> find(String typeCode, Channel channel, String locale) {
        Template t = store.get(key(typeCode, channel, locale));
        if (t != null) return Optional.of(t);
        return Optional.ofNullable(store.get(key(typeCode, channel, "en")));
    }

    private String key(String type, Channel ch, String locale) {
        return type + ":" + ch + ":" + locale;
    }
}
