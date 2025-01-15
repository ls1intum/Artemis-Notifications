package de.tum.cit.artemis.push.common;

import org.springframework.http.ResponseEntity;

public interface SendService<T> {

    ResponseEntity<Void> send(T request);

    boolean isHealthy();
}
