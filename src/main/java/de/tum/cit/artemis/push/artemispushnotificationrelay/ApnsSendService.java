package de.tum.cit.artemis.push.artemispushnotificationrelay;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;

@Service
public class ApnsSendService implements SendService<NotificationRequest> {

    @Value("${APNS_TOKEN:#{null}}")
    private String apnsToken;

    @Value("${APNS_URL:#{null}}")
    private String apnsUrl;

    private final Logger log = LoggerFactory.getLogger(ApnsSendService.class);

    public ApnsSendService() {
        if (apnsUrl == null || apnsUrl.isEmpty() || apnsToken == null || apnsToken.isEmpty()) {
            log.error("Could not load APNS config");
        }
    }

    @Override
    public ResponseEntity<Void> send(NotificationRequest request) {
        if (apnsToken != null && !apnsToken.isEmpty() && apnsUrl != null && !apnsUrl.isEmpty()) {
            RestTemplate restTemplate = new RestTemplate();

            return sendApnsRequest(restTemplate, request);
        }
        return ResponseEntity.internalServerError().build();
    }

    @Async
    ResponseEntity<Void> sendApnsRequest(RestTemplate restTemplate, NotificationRequest request) {
        try {
            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.add("path", "/3/device/" + request.getToken());
            httpHeaders.setBearerAuth(apnsToken);
            httpHeaders.add("apns-push-type", "alert");

            String body = getApnsBody(request);
            HttpEntity<String> httpEntity = new HttpEntity<>(body, httpHeaders);
            log.debug("Send APNS request with body: " + body);

            restTemplate.postForObject(apnsUrl, httpEntity, String.class);
            return ResponseEntity.ok().build();
        } catch (RestClientException e) {
            log.error("Could not send APNS notifications", e);
            return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).build();
        }
    }

    private String getApnsBody(NotificationRequest request) {
        return new ApplePushNotificationRequest(
                request.getInitializationVector(),
                request.getPayloadCipherText(),
                request.getToken()).getApnsBody();
    }

}

record ApplePushNotificationRequest(String initializationVector, String payloadCiphertext, String token) {

    String getApnsBody() {
        return new Gson().toJson(new ApnsBody(new ApsBody(1), payloadCiphertext, initializationVector));
    }

    private record ApnsBody(@SerializedName("aps") ApsBody apsBody, @SerializedName("payload") String payload,
                            @SerializedName("iv") String iv) {
    }

    private record ApsBody(@SerializedName("content-available") int contentAvailable) {
    }

}
