package com.newrelic.event;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class InfrastructureEventOutput {
  @JsonProperty("name")
  private String name = "com.newrelic";
  @JsonProperty("protocol_version")
  private String protocol_version = "1";
  @JsonProperty("integration_version")
  private String integration_version = "1.0.0";

  @JsonProperty("events")
  private final List<InfrastructureEvent> events;

  public InfrastructureEventOutput(List<InfrastructureEvent> events) {
    this.events = events;
  }

  public static class InfrastructureEvent {
    private final String summary;
    private final String category;

    public InfrastructureEvent(String summary, String category) {
      this.summary = summary;
      this.category = category;
    }

    public String getCategory() {
      return category;
    }

    public String getSummary() {
      return summary;
    }
  }
}
