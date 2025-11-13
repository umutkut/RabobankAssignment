package nl.rabobank.service;

import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class IdGenerator {
    public String generateUUID() {
        return UUID.randomUUID().toString();
    }
}
