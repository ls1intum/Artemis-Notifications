package de.tum.cit.artemis.push;

public record HealthReport(boolean isApnsConnected, boolean isFirebaseConnected, String versionNumber) {
}
