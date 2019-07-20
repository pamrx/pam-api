package com.daugherty.pam.notification

import groovy.util.logging.Slf4j
import org.springframework.web.bind.annotation.RestController

@Slf4j
@RestController('/notify')
class NotifcationController {
  private final NotificationService notificationService

  NotifcationController(final NotificationService notificationService) {
    this.notificationService = notificationService
  }
}
