package com.company.news.springMVC.beans.propertyeditors;

import java.beans.PropertyEditorSupport;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;

import org.springframework.util.StringUtils;

public class CustomTimestampEditor extends PropertyEditorSupport  {

  public CustomTimestampEditor(DateFormat dateFormat, boolean allowEmpty) {
    this.dateFormat = dateFormat;
    this.allowEmpty = allowEmpty;
    exactDateLength = -1;
  }

  public CustomTimestampEditor(DateFormat dateFormat, boolean allowEmpty, int exactDateLength) {
    this.dateFormat = dateFormat;
    this.allowEmpty = allowEmpty;
    this.exactDateLength = exactDateLength;
  }

  public void setAsText(String text) throws IllegalArgumentException {
    if (allowEmpty && !StringUtils.hasText(text)) {
      setValue(null);
    } else {
      if (text != null && exactDateLength >= 0 && text.length() != exactDateLength)
        throw new IllegalArgumentException((new StringBuilder(
            "Could not parse Timestamp: it is not exactly")).append(exactDateLength).append(
            "characters long").toString());
      try {
        setValue(new Timestamp(dateFormat.parse(text).getTime()));
      } catch (ParseException ex) {
        throw new IllegalArgumentException((new StringBuilder("Could not parse Timestamp: ")).append(
            ex.getMessage()).toString(), ex);
      }
    }
  }

  public String getAsText() {
    Timestamp value = (Timestamp) getValue();
    return value == null ? "" : dateFormat.format(value);
  }

  private final DateFormat dateFormat;
  private final boolean allowEmpty;
  private final int exactDateLength;
}
