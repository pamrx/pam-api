package com.daugherty.pam.patient

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonValue
import org.springframework.util.StringUtils

enum PRESCRIPTION_FORM {
  TABLET('2'),
  PUFF('12'),
  CREAM('10'),
  UNITS('7')

  final String value

  PRESCRIPTION_FORM(final String value) {
    this.value = value
  }

  //Lookup table
  private static final Map<String, PRESCRIPTION_FORM> lookup = [:]

  //Populate the lookup table on loading time
  static {
    for(PRESCRIPTION_FORM enumeration : PRESCRIPTION_FORM.values()) {
      lookup.put(enumeration.value, enumeration)
    }
  }

  @JsonCreator
  static PRESCRIPTION_FORM forValue(String value) {
    return lookup.get(value.toLowerCase())
  }

  @JsonValue
  String toValue() {
    for (Map.Entry<String, PRESCRIPTION_FORM> entry : lookup.entrySet()) {
      if (entry.value == this)
        return entry.key
    }

    return null // or fail
  }
}