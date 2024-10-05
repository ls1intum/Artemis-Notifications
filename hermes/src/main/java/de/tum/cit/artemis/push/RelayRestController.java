package de.tum.cit.artemis.push;

import de.tum.cit.artemis.push.apns.ApnsSendService;
import de.tum.cit.artemis.push.common.NotificationRequest;
import de.tum.cit.artemis.push.firebase.FirebaseSendPushNotificationsRequest;
import de.tum.cit.artemis.push.firebase.FirebaseSendService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
    public ResponseEntity<Void> send(@RequestBody FirebaseSendPushNotificationsRequest notificationRequests) {
        return firebaseSendService.send(notificationRequests.notificationRequest());
    }

    @PostMapping("send_apns")
    public ResponseEntity<Void> send(@RequestBody NotificationRequest notificationRequest) {
        return apnsSendService.send(notificationRequest);
    }

    @GetMapping("alive")
    public ResponseEntity<Void> alive() {
        return ResponseEntity.ok().build();
    }
}
