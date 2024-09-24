package com.in.compliance.controller.credential;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.in.compliance.models.credential.Alert;
import com.in.compliance.repository.credential.AlertRepository;

@RestController
@RequestMapping("compliancemanagement/alerts")
public class AlertController {

    @Autowired
    private AlertRepository alertRepository;

    // GET /alerts: Fetch active alerts for expiring or unverified credentials
    @GetMapping
    public ResponseEntity<List<Alert>> getActiveAlerts() {
        List<Alert> alerts = alertRepository.findByReadFalse();
        return new ResponseEntity<>(alerts, HttpStatus.OK);
    }

    // PUT /alerts/mark-read: Mark alerts as read
    @PutMapping("/mark-read")
    public ResponseEntity<Void> markAlertsAsRead(@RequestBody List<String> alertIds) {
        List<Alert> alerts = alertRepository.findAllById(alertIds);
        alerts.forEach(alert -> alert.setRead(true));
        alertRepository.saveAll(alerts);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
