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

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.Key;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.time.Instant;
import java.util.Base64;

@Service
public class ApnsSendService implements SendService<NotificationRequest> {

    @Value("${APNS_KEY_PATH:#{null}}")
    private String apnsKeyPath;

    @Value("${APNS_URL:#{null}}")
    private String apnsUrl;

    private final Logger log = LoggerFactory.getLogger(ApnsSendService.class);

    public ApnsSendService() {
        if (apnsUrl == null || apnsUrl.isEmpty() || apnsKeyPath == null || apnsKeyPath.isEmpty()) {
            log.error("Could not load APNS config");
        }
    }

    @Override
    public ResponseEntity<Void> send(NotificationRequest request) {
        if (apnsKeyPath != null && !apnsKeyPath.isEmpty() && apnsUrl != null && !apnsUrl.isEmpty()) {
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

            Key key = getPrivateKey(apnsKeyPath, "ES256");
            String numberOfSecondsSinceEpoch = String.valueOf(Instant.now().getEpochSecond());

            String jwt = Jwts
                    .builder()
                    .setHeaderParam("kid", "RHP5G7CZH8")
                    .claim("iss", "2J3C6P6X3N")
                    .claim("iat", numberOfSecondsSinceEpoch)
                    .signWith(key, SignatureAlgorithm.ES256)
                    .compact();

            httpHeaders.setBearerAuth(jwt);
            httpHeaders.add("apns-push-type", "alert");

            String body = getApnsBody(request);
            HttpEntity<String> httpEntity = new HttpEntity<>(body, httpHeaders);
            log.debug("Send APNS request with body: " + body);

            restTemplate.postForObject(apnsUrl, httpEntity, String.class);
            return ResponseEntity.ok().build();
        } catch (RestClientException | IOException e) {
            log.error("Could not send APNS notifications", e);
            return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).build();
        }
    }

    private PrivateKey getPrivateKey(String filename, String algorithm) throws IOException {
        String content = new String(Files.readAllBytes(Paths.get(filename)), "utf-8");
        try {
            String privateKey = content.replace("-----BEGIN PRIVATE KEY-----", "")
                    .replace("-----END PRIVATE KEY-----", "")
                    .replaceAll("\\s+", "");

            KeyFactory kf = KeyFactory.getInstance(algorithm);
            return kf.generatePrivate(new PKCS8EncodedKeySpec(Base64.getDecoder().decode(privateKey)));
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Java did not support the algorithm:" + algorithm, e);
        } catch (InvalidKeySpecException e) {
            throw new RuntimeException("Invalid key format");
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
