package de.tum.cit.artemis.push;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class HealthController {
    private final NotificationHealthService healthService;

    public HealthController(NotificationHealthService healthService) {
        this.healthService = healthService;
    }

    @GetMapping("/health")
    public HealthReport getHealth() {
        return healthService.getHealthReport();
    }
}
