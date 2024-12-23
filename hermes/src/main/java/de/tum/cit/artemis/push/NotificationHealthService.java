package de.tum.cit.artemis.push;

import de.tum.cit.artemis.push.apns.ApnsSendService;
import de.tum.cit.artemis.push.firebase.FirebaseSendService;
import org.springframework.boot.info.BuildProperties;
import org.springframework.stereotype.Service;

@Service
public class NotificationHealthService {
    private final FirebaseSendService firebaseSendService;
    private final ApnsSendService apnsSendService;
    private final BuildProperties buildProperties;

    NotificationHealthService(FirebaseSendService firebaseSendService, ApnsSendService apnsSendService, BuildProperties buildProperties) {
        this.firebaseSendService = firebaseSendService;
        this.apnsSendService = apnsSendService;
        this.buildProperties = buildProperties;
    }

    public HealthReport getHealthReport() {
        return new HealthReport(apnsSendService.isHealthy(), firebaseSendService.isHealthy(), buildProperties.getVersion());
    }
}