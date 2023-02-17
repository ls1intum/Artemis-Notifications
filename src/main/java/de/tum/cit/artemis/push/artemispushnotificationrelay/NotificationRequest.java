package de.tum.cit.artemis.push.artemispushnotificationrelay;

public class NotificationRequest {
    private final String initializationVector;
    private final String payloadCipherText;

    private final String token;

    public NotificationRequest(String initializationVector, String payloadCipherText, String token) {
        this.initializationVector = initializationVector;
        this.payloadCipherText = payloadCiphertext;
        this.token = token;
    }

    public String getInitializationVector() {
        return initializationVector;
    }

    public String getPayloadCipherText() {
        return payloadCipherText;
    }

    public String getToken() {
        return token;
    }
}
