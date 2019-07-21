package com.daugherty.pam.patient

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonValue

enum PRESCRIPTION_INTERVAL {
  TWICE_DAILY(1),
  DAILY(9),
  AS_NEEDED(17)

  final Integer value

  PRESCRIPTION_INTERVAL(final Integer value) {
    this.value = value
  }

  //Lookup table
  private static final Map<Integer, PRESCRIPTION_INTERVAL> lookup = [:]

  //Populate the lookup table on loading time
  static {
    for(PRESCRIPTION_INTERVAL enumeration : PRESCRIPTION_INTERVAL.values()) {
      lookup.put(enumeration.value, enumeration)
    }
  }

  @JsonCreator
  static PRESCRIPTION_INTERVAL forValue(Integer value) {
    return lookup.get(value)
  }

  @JsonValue
  Integer toValue() {
    for (Map.Entry<Integer, PRESCRIPTION_INTERVAL> entry : lookup.entrySet()) {
      if (entry.value == this)
        return entry.key
    }

    return null // or fail
  }
}