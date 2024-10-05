package de.tum.cit.artemis.push.common;

public record NotificationRequest(String initializationVector, String payloadCipherText, String token) {
}
