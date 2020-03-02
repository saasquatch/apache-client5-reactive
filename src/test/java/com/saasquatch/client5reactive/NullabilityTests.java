package com.saasquatch.client5reactive;

import static org.junit.jupiter.api.Assertions.assertThrows;
import org.apache.hc.client5.http.async.methods.SimpleHttpRequest;
import org.apache.hc.client5.http.async.methods.SimpleHttpRequests;
import org.apache.hc.client5.http.async.methods.SimpleRequestProducer;
import org.apache.hc.client5.http.impl.async.CloseableHttpAsyncClient;
import org.apache.hc.client5.http.impl.async.HttpAsyncClients;
import org.apache.hc.core5.http.nio.AsyncRequestProducer;
import org.apache.hc.core5.reactive.ReactiveResponseConsumer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.reactivestreams.Publisher;
import io.reactivex.rxjava3.core.Flowable;

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
        () -> reactiveClient.streamingExecute((AsyncRequestProducer) null, null, null));
    assertThrows(NullPointerException.class,
        () -> reactiveClient.streamingExecute((AsyncRequestProducer) null, null));
    assertThrows(NullPointerException.class,
        () -> reactiveClient.streamingExecute((AsyncRequestProducer) null));
    assertThrows(NullPointerException.class,
        () -> reactiveClient.streamingExecute((SimpleHttpRequest) null, null));
    assertThrows(NullPointerException.class,
        () -> reactiveClient.streamingExecute((SimpleHttpRequest) null));
  }

  @Test
  public void testEmptyPublisher() {
    final Publisher<Void> voidResultPublisher = reactiveClient.execute(
        SimpleRequestProducer.create(SimpleHttpRequests.get("https://example.com")),
        new ReactiveResponseConsumer());
    Flowable.fromPublisher(voidResultPublisher).test().assertEmpty();
  }

}
