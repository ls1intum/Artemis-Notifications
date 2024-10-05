package de.tum.cit.artemis.push.firebase;

import de.tum.cit.artemis.push.common.NotificationRequest;

import java.util.List;

public record FirebaseSendPushNotificationsRequest(List<NotificationRequest> notificationRequest) {
}
