package lld.practice.infra.notificationservice.service;

import lld.practice.infra.notificationservice.channel.RenderedMessage;
import lld.practice.infra.notificationservice.enums.Channel;
import lld.practice.infra.notificationservice.exception.TemplateNotFoundException;
import lld.practice.infra.notificationservice.model.Template;
import lld.practice.infra.notificationservice.repository.TemplateRepository;
import lld.practice.infra.notificationservice.util.TemplateRenderer;

import java.util.Map;

public class TemplateService {

    private final TemplateRepository templateRepository;

    public TemplateService(TemplateRepository repo) {
        this.templateRepository = repo;
    }

    public void registerTemplate(Template template) {
        templateRepository.save(template);
    }

    public RenderedMessage render(String typeCode, Channel channel, String locale, Map<String, Object> payload) {
        Template t = templateRepository.find(typeCode, channel, locale)
                .orElseThrow(() -> new TemplateNotFoundException(
                        "Template not found: type=" + typeCode + ", channel=" + channel + ", locale=" + locale));
        String subject = TemplateRenderer.render(t.getSubject(), payload);
        String body = TemplateRenderer.render(t.getBody(), payload);
        return new RenderedMessage(subject, body);
    }
}
