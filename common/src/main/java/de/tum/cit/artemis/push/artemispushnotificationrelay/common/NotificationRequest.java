package de.tum.cit.artemis.push.artemispushnotificationrelay.common;

public record NotificationRequest(String initializationVector, String payloadCipherText, String token) {
}
