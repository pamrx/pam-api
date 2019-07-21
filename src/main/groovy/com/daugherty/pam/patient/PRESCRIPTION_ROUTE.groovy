package com.daugherty.pam.patient

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonValue

enum PRESCRIPTION_ROUTE {
  ORAL('1'),
  TO_AFFECTED_AREA('4'),
  SUB_Q('9')

  final String value

  PRESCRIPTION_ROUTE(final String value) {
    this.value = value
  }

  //Lookup table
  private static final Map<String, PRESCRIPTION_ROUTE> lookup = [:]

  //Populate the lookup table on loading time
  static {
    for(PRESCRIPTION_ROUTE enumeration : PRESCRIPTION_ROUTE.values()) {
      lookup.put(enumeration.value, enumeration)
    }
  }

  @JsonCreator
  static PRESCRIPTION_ROUTE forValue(String value) {
    return lookup.get(value.toLowerCase())
  }

  @JsonValue
  String toValue() {
    for (Map.Entry<String, PRESCRIPTION_ROUTE> entry : lookup.entrySet()) {
      if (entry.value == this)
        return entry.key
    }

    return null // or fail
  }
}