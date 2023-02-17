package de.tum.cit.artemis.push.artemispushnotificationrelay;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;

import java.util.List;

@Service
public class ApnsSendService implements SendService<NotificationRequest> {

    @Value("${artemis.push-notification.apns.token:#{null}}")
    private String apnsToken;

    @Value("${artemis.push-notification.apns.url:#{null}}")
    private String apnsUrl;

    private final Logger log = LoggerFactory.getLogger(ApnsSendService.class);

    public ApnsSendService() {
        if (apnsUrl == null || apnsUrl.isEmpty() || apnsToken == null || apnsToken.isEmpty()) {
            log.debug("Could not load APNS config");
        }
    }

    @Override
    public ResponseEntity<Void> send(NotificationRequest request) {
        if (apnsToken != null && !apnsToken.isEmpty() && apnsUrl != null && !apnsUrl.isEmpty()) {
            RestTemplate restTemplate = new RestTemplate();

            sendApnsRequest(restTemplate, request);
        }
    }

    @Async
    void sendApnsRequest(RestTemplate restTemplate, NotificationRequest request) {
        try {
            HttpHeaders httpHeaders = new HttpHeaders();
                httpHeaders.add("path", "/3/device/" + request.getToken());
                httpHeaders.setBearerAuth(apnsToken);
                httpHeaders.add("apns-push-type", "alert");

                String body = getApnsBody(request);
                HttpEntity<String> httpEntity = new HttpEntity<>(body, httpHeaders);
                log.debug("Send APNS request with body: " + body);
                restTemplate.postForObject(apnsUrl, httpEntity, String.class);

                return null;
            });
        }
        catch (RestClientException e) {
            log.error("Could not send APNS notifications", e);
        }
    }

    private String getApnsBody(NotificationRequest request) {

    }

}
