package de.tum.cit.artemis.push.artemispushnotificationrelay;

import org.springframework.http.ResponseEntity;

import java.util.List;

interface SendService<T> {

    ResponseEntity<Void> send(T request);
}
