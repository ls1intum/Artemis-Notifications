<div align="center">
    <h1 align="center">Artemis-Notifications</h1>
</div>

Notification Relay for Artemis Push Notifications.  
Allows secure and private push notifications from [Artemis](https://github.com/ls1intum/Artemis) to the mobile apps for [iOS](https://github.com/ls1intum/artemis-ios) and [Android](https://github.com/ls1intum/artemis-android).

How to run:
docker run -v path_to_google_credentials_json:/firebase.json -e GOOGLE_APPLICATION_CREDENTIALS="/firebase.json" -p 17333:8080 --name artemis_notification_relay notification_relay
