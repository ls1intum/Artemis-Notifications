package de.tum.cit.artemis.push.artemispushnotificationrelay;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/push_notification")
public class RelayRestController {

    private final FirebaseSendService firebaseSendService;
    private final ApnsSendService apnsSendService;

    public RelayRestController(FirebaseSendService firebaseSendService, ApnsSendService apnsSendService) {
        this.firebaseSendService = firebaseSendService;
        this.apnsSendService = apnsSendService;
    }

    @PostMapping("send_firebase")
    public ResponseEntity send(@RequestBody FirebaseSendPushNotificationsRequest notificationRequests) {
        return firebaseSendService.send(notificationRequests.notificationRequest());
    }

    @PostMapping("send_apns")
    public ResponseEntity send(@RequestBody NotificationRequest notificationRequest) {
        return apnsSendService.send(notificationRequest);
    }
}
