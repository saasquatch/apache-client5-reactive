package com.saasquatch.client5reactive;

import io.reactivex.rxjava3.core.Flowable;
import org.apache.hc.client5.http.async.methods.SimpleRequestBuilder;
import org.apache.hc.client5.http.impl.async.CloseableHttpAsyncClient;
import org.apache.hc.client5.http.impl.async.HttpAsyncClients;
import org.junit.jupiter.api.Test;

public class LifecycleTests {

  private static final String EXAMPLE_URL = "https://www.example.com";

  @Test
  public void testNonStarted() throws Exception {
    try (CloseableHttpAsyncClient asyncClient = HttpAsyncClients.createDefault()) {
      // Not started
      final HttpReactiveClient reactiveClient = HttpReactiveClients.create(asyncClient);
      Flowable.fromPublisher(reactiveClient.execute(SimpleRequestBuilder.get(EXAMPLE_URL).build()))
          .test().assertError(IllegalStateException.class);
    }
  }

  @Test
  public void testClosed() throws Exception {
    final HttpReactiveClient reactiveClient;
    try (CloseableHttpAsyncClient asyncClient = HttpAsyncClients.createDefault()) {
      asyncClient.start();
      reactiveClient = HttpReactiveClients.create(asyncClient);
    }
    // Closed
    Flowable.fromPublisher(reactiveClient.execute(SimpleRequestBuilder.get(EXAMPLE_URL).build()))
        .test().assertError(IllegalStateException.class);
  }

}
