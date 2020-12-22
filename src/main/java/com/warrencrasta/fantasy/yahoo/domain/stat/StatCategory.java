package com.warrencrasta.fantasy.yahoo.domain.stat;

import lombok.Data;

@Data
public class StatCategory {

  private String id;
  private String name;
  private String value;
  private boolean isBad;
}
