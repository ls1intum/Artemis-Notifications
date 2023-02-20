package de.tum.cit.artemis.push.artemispushnotificationrelay;

import com.eatthepath.pushy.apns.*;
import com.eatthepath.pushy.apns.util.SimpleApnsPayloadBuilder;
import com.eatthepath.pushy.apns.util.SimpleApnsPushNotification;
import com.eatthepath.pushy.apns.util.concurrent.PushNotificationFuture;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import java.io.File;
import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.ExecutionException;

@Service
public class ApnsSendService implements SendService<NotificationRequest> {

//    @Value("APNS_CERTIFICATE_PATH")
    private String apnsCertificatePath = "artemis-apns.p12";
//    @Value("APNS_CERTIFICATE_PWD")
    private String apnsCertificatePwd = "MbqErip6bqt5lSzThtl5DCs";

    private final Logger log = LoggerFactory.getLogger(ApnsSendService.class);
    private ApnsClient apnsClient;

    public ApnsSendService() {
        if (apnsCertificatePwd == null || apnsCertificatePath == null) {
            log.error("Could not init APNS service. Certificate information missing.");
            return;
        }
        try {
            apnsClient = new ApnsClientBuilder()
                    .setApnsServer(ApnsClientBuilder.DEVELOPMENT_APNS_HOST)
                    .setClientCredentials(new File(apnsCertificatePath), apnsCertificatePwd)
                    .build();
        } catch (IOException e) {
            log.error("Could not init APNS service", e);
        }
    }

    @Override
    public ResponseEntity<Void> send(NotificationRequest request) {
        return sendApnsRequest(request);
    }

    @Async
    ResponseEntity<Void> sendApnsRequest(NotificationRequest request) {
        String payload = new SimpleApnsPayloadBuilder()
                .setContentAvailable(true)
                .addCustomProperty("iv", request.getInitializationVector())
                .addCustomProperty("payload", request.getPayloadCipherText())
                .build();

        SimpleApnsPushNotification notification = new SimpleApnsPushNotification(request.getToken(),
                "de.tum.cit.artemis",
                payload,
                Instant.now().plus(Duration.ofDays(7)),
                DeliveryPriority.getFromCode(5),
                PushType.BACKGROUND);


        PushNotificationFuture<SimpleApnsPushNotification, PushNotificationResponse<SimpleApnsPushNotification>> responsePushNotificationFuture = apnsClient.sendNotification(notification);
        try {
            final PushNotificationResponse<SimpleApnsPushNotification> pushNotificationResponse =
                    responsePushNotificationFuture.get();
            if (pushNotificationResponse.isAccepted()) {
                log.info("Send notification to " + request.getToken()); // TODO: change to DEBUG
                return ResponseEntity.ok().build();
            } else {
                log.error("Notification rejected by the APNs gateway: " +
                        pushNotificationResponse.getRejectionReason());

                pushNotificationResponse.getTokenInvalidationTimestamp().ifPresent(timestamp -> {
                    log.error("\t…and the token is invalid as of " + timestamp);
                });
                return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).build();
            }
        } catch (ExecutionException | InterruptedException e) {
            log.error("Failed to send push notification.");
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).build();
        }
    }
}
