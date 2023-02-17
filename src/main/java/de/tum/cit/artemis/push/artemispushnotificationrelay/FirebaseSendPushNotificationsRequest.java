package de.tum.cit.artemis.push.artemispushnotificationrelay;

import java.util.List;

record FirebaseSendPushNotificationsRequest(
    List<NotificationRequest> notificationRequest
) {
}
