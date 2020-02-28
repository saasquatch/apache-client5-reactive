package com.saasquatch.client5reactive;

import org.apache.hc.client5.http.async.methods.SimpleHttpRequests;
import org.apache.hc.client5.http.impl.async.CloseableHttpAsyncClient;
import org.apache.hc.client5.http.impl.async.HttpAsyncClients;
import org.junit.jupiter.api.Test;
import io.reactivex.rxjava3.core.Flowable;

public class LifecycleTests {

  @Test
  public void testNonStarted() throws Exception {
    try (CloseableHttpAsyncClient asyncClient = HttpAsyncClients.createDefault()) {
      // Not started
      final ReactiveHttpAsyncClient reactiveClient = ReactiveHttpAsyncClients.create(asyncClient);
      Flowable.fromPublisher(reactiveClient.execute(SimpleHttpRequests.get("https://example.com")))
          .test().assertError(IllegalStateException.class);
    }
  }

  @Test
  public void testClosed() throws Exception {
    final ReactiveHttpAsyncClient reactiveClient;
    try (CloseableHttpAsyncClient asyncClient = HttpAsyncClients.createDefault()) {
      asyncClient.start();
      reactiveClient = ReactiveHttpAsyncClients.create(asyncClient);
    }
    // Closed
    Flowable.fromPublisher(reactiveClient.execute(SimpleHttpRequests.get("https://example.com")))
        .test().assertError(IllegalStateException.class);
  }

}
