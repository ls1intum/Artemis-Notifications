package de.tum.cit.artemis.push;

public class HealthReport {
    private final boolean isApnsConnected;
    private final boolean isFirebaseConnected;
    private final String versionNumber;

    public HealthReport(boolean isApnsConnected, boolean isFirebaseConnected, String versionNumber) {
        this.isApnsConnected = isApnsConnected;
        this.isFirebaseConnected = isFirebaseConnected;
        this.versionNumber = versionNumber;
    }

    public boolean isApnsConnected() {
        return isApnsConnected;
    }

    public boolean isFirebaseConnected() {
        return isFirebaseConnected;
    }

    public String getVersionNumber() {
        return versionNumber;
    }
}
