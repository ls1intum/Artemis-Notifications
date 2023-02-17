<div align="center">
    <h1 align="center">Artemis-Notifications</h1>
</div>

Notification Relay for Artemis Push Notifications.  
Allows secure and private push notifications from [Artemis](https://github.com/ls1intum/Artemis) to the mobile apps for [iOS](https://github.com/ls1intum/artemis-ios) and [Android](https://github.com/ls1intum/artemis-android).

How to run:
`docker run -v path_to_google_credentials_json:/firebase.json -e GOOGLE_APPLICATION_CREDENTIALS="/firebase.json" -p 17333:8080 --name artemis_notification_relay notification_relay`

To run the services as an APNS relay the following Environment Variables are required:
- APNS_TOKEN: String - The APNs token as described [here](https://developer.apple.com/documentation/usernotifications/setting_up_a_remote_notification_server/establishing_a_token-based_connection_to_apns)
- APNS_URL: String - The APNS Url, e.g.: https://api.sandbox.push.apple.com/ or https://api.push.apple.com/

To run the services as a Firebase relay the following Environment Variable is required:
- GOOGLE_APPLICATION_CREDENTIALS: String - Path to the firebase.json
Furthermore the Firebase.json needs to be mounted into the Docker under the above specified path.

To run both APNS and Firebase configure the Environment Variables for both.
