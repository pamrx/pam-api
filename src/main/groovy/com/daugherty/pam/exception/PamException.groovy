package com.daugherty.pam.exception

class PamException extends Exception {
  final ERROR_CODE errorCode

  PamException(ERROR_CODE errorCode) {
    this.errorCode = errorCode
  }
}
