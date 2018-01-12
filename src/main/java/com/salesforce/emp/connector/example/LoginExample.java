/* 
 * Copyright (c) 2016, salesforce.com, inc.
 * All rights reserved.
 * Licensed under the BSD 3-Clause license. 
 * For full license text, see LICENSE.TXT file in the repo root  or https://opensource.org/licenses/BSD-3-Clause
 */
package com.salesforce.emp.connector.example;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.newrelic.event.InfrastructureEventOutput;
import com.newrelic.event.InfrastructureEventOutput.InfrastructureEvent;
import com.salesforce.emp.connector.BayeuxParameters;
import com.salesforce.emp.connector.EmpConnector;
import com.salesforce.emp.connector.TopicSubscription;

import java.util.Arrays;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

import static com.salesforce.emp.connector.LoginHelper.login;

/**
 * An example of using the EMP connector using login credentials
 *
 * @author hal.hildebrand
 * @since 202
 */
public class LoginExample {
  public static void main(String[] argv) throws Exception {
    ObjectMapper objectMapper = new ObjectMapper();

    if (argv.length < 3 || argv.length > 4) {
      System.err.println("Usage: LoginExample username password topic [replayFrom]");
      System.exit(1);
    }
    long replayFrom = EmpConnector.REPLAY_FROM_EARLIEST;
    if (argv.length == 4) {
      replayFrom = Long.parseLong(argv[3]);
    }

    BayeuxParameters params;
    try {
      params = login(argv[0], argv[1]);
    } catch (Exception e) {
      e.printStackTrace(System.err);
      System.exit(1);
      throw e;
    }

    Consumer<Map<String, Object>> consumer = event -> {
      try {
        Map<String, Object> payload = (Map) event.get("payload");
        String content = (String) payload.get("News_Content__c");
        String location = (String) payload.get("Location__c");
        String urgent = String.valueOf(payload.get("Urgent__c"));
        InfrastructureEvent ev = new InfrastructureEvent(urgent + " " + content + " " + location, argv[2]);
        InfrastructureEventOutput out = new InfrastructureEventOutput(Collections.singletonList(ev));
        //System.out.println(objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(out));
        System.out.println(objectMapper.writeValueAsString(out));
      } catch (JsonProcessingException e) {
        System.err.println(Arrays.toString(e.getStackTrace()));
      }
    };

    EmpConnector connector = new EmpConnector(params);

    connector.start().get(5, TimeUnit.SECONDS);

    TopicSubscription subscription = connector.subscribe(argv[2], replayFrom, consumer).get(5, TimeUnit.SECONDS);

    System.err.println(String.format("Subscribed: %s", subscription));
  }
}
