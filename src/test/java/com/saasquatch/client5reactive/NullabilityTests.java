package com.saasquatch.client5reactive;

import static org.junit.jupiter.api.Assertions.assertThrows;
import org.apache.hc.client5.http.async.methods.SimpleHttpRequest;
import org.apache.hc.client5.http.impl.async.CloseableHttpAsyncClient;
import org.apache.hc.client5.http.impl.async.HttpAsyncClients;
import org.apache.hc.core5.http.nio.AsyncRequestProducer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class NullabilityTests {

  private static CloseableHttpAsyncClient asyncClient;
  private static ReactiveHttpAsyncClient reactiveClient;

  @BeforeAll
  public static void beforeAll() {
    asyncClient = HttpAsyncClients.createDefault();
    asyncClient.start();
    reactiveClient = ReactiveHttpAsyncClients.create(asyncClient);
  }

  @AfterAll
  public static void afterAll() throws Exception {
    asyncClient.close();
  }

  @Test
  public void testNullability() {
    assertThrows(NullPointerException.class,
        () -> reactiveClient.execute((AsyncRequestProducer) null, null, null, null));
    assertThrows(NullPointerException.class,
        () -> reactiveClient.execute((AsyncRequestProducer) null, null, null));
    assertThrows(NullPointerException.class,
        () -> reactiveClient.execute((AsyncRequestProducer) null, null));
    assertThrows(NullPointerException.class,
        () -> reactiveClient.execute((SimpleHttpRequest) null, null));
    assertThrows(NullPointerException.class,
        () -> reactiveClient.execute((SimpleHttpRequest) null));
    assertThrows(NullPointerException.class,
        () -> reactiveClient.streamingExecute((AsyncRequestProducer) null, null));
    assertThrows(NullPointerException.class,
        () -> reactiveClient.streamingExecute((AsyncRequestProducer) null));
  }

}
