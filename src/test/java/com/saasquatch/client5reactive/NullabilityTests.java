package com.saasquatch.client5reactive;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

import io.reactivex.rxjava3.core.Flowable;
import org.apache.hc.client5.http.async.methods.SimpleHttpRequest;
import org.apache.hc.client5.http.async.methods.SimpleRequestBuilder;
import org.apache.hc.client5.http.async.methods.SimpleRequestProducer;
import org.apache.hc.client5.http.impl.async.CloseableHttpAsyncClient;
import org.apache.hc.client5.http.impl.async.HttpAsyncClients;
import org.apache.hc.core5.http.nio.AsyncRequestProducer;
import org.apache.hc.core5.reactive.ReactiveResponseConsumer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.reactivestreams.Publisher;

public class NullabilityTests {

  private static CloseableHttpAsyncClient asyncClient;
  private static HttpReactiveClient reactiveClient;

  @BeforeAll
  public static void beforeAll() {
    asyncClient = HttpAsyncClients.createDefault();
    asyncClient.start();
    reactiveClient = HttpReactiveClients.create(asyncClient);
  }

  @AfterAll
  public static void afterAll() throws Exception {
    asyncClient.close();
  }

  @SuppressWarnings({"ConstantConditions", "RedundantCast"})
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
  public void testVoidPublisher() {
    final Publisher<Void> voidResultPublisher = reactiveClient.execute(
        SimpleRequestProducer.create(SimpleRequestBuilder.get("https://example.com").build()),
        new ReactiveResponseConsumer());
    assertDoesNotThrow(() -> Flowable.fromPublisher(voidResultPublisher).blockingSubscribe());
  }

}
