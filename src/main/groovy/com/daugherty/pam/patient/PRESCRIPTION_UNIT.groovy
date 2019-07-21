package com.daugherty.pam.patient

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonValue

enum PRESCRIPTION_UNIT {
  MG('1'),
  MCG('7'),
  G('8')

  final String value

  PRESCRIPTION_UNIT(final String value) {
    this.value = value
  }

  //Lookup table
  private static final Map<String, PRESCRIPTION_UNIT> lookup = [:]

  //Populate the lookup table on loading time
  static {
    for(PRESCRIPTION_UNIT enumeration : PRESCRIPTION_UNIT.values()) {
      lookup.put(enumeration.value, enumeration)
    }
  }

  @JsonCreator
  static PRESCRIPTION_UNIT forValue(String value) {
    return lookup.get(value.toLowerCase())
  }

  @JsonValue
  String toValue() {
    for (Map.Entry<String, PRESCRIPTION_UNIT> entry : lookup.entrySet()) {
      if (entry.value == this)
        return entry.key
    }

    return null // or fail
  }
}