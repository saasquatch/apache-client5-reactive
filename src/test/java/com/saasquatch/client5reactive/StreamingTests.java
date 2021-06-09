package com.saasquatch.client5reactive;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;

import io.reactivex.rxjava3.core.Flowable;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.WritableByteChannel;
import org.apache.hc.client5.http.async.methods.SimpleRequestBuilder;
import org.apache.hc.client5.http.async.methods.SimpleRequestProducer;
import org.apache.hc.client5.http.async.methods.SimpleResponseConsumer;
import org.apache.hc.client5.http.impl.async.CloseableHttpAsyncClient;
import org.apache.hc.client5.http.impl.async.HttpAsyncClients;
import org.apache.hc.client5.http.impl.async.MinimalHttpAsyncClient;
import org.apache.hc.core5.http.Message;
import org.apache.hc.core5.http.protocol.BasicHttpContext;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.reactivestreams.Publisher;

public class StreamingTests {

  private static final String FLOWABLE_SOURCE_URL =
      "https://cdn.jsdelivr.net/gh/ReactiveX/RxJava@81f0569a8b9b7d27059f127b90fd7335118b2ee4/src/main/java/io/reactivex/rxjava3/core/Flowable.java";
  private static CloseableHttpAsyncClient asyncClient;
  private static HttpReactiveClient reactiveClient;
  private static byte[] flowableSourceBytes;

  @BeforeAll
  public static void beforeAll() throws Exception {
    asyncClient = HttpAsyncClients.createDefault();
    asyncClient.start();
    reactiveClient = HttpReactiveClients.create(asyncClient);
    flowableSourceBytes = asyncClient.execute(
        SimpleRequestBuilder.get(FLOWABLE_SOURCE_URL).build(), null).get().getBodyBytes();
  }

  @AfterAll
  public static void afterAll() throws Exception {
    asyncClient.close();
  }

  @Test
  public void testVanillaExecuteWorks() {
    final byte[] bodyBytes = Flowable.fromPublisher(reactiveClient.execute(
        SimpleRequestProducer.create(SimpleRequestBuilder.get(FLOWABLE_SOURCE_URL).build()),
        SimpleResponseConsumer.create())).blockingSingle().getBodyBytes();
    assertArrayEquals(flowableSourceBytes, bodyBytes);
  }

  @Test
  public void testVanillaStreamingWorks() {
    final byte[] bodyBytes = Flowable
        .fromPublisher(reactiveClient.streamingExecute(
            SimpleRequestProducer.create(SimpleRequestBuilder.get(FLOWABLE_SOURCE_URL).build())))
        .concatMap(Message::getBody).to(this::toByteArray);
    assertArrayEquals(flowableSourceBytes, bodyBytes);
  }

  @Test
  public void testBasicStreamingWorks() {
    final byte[] bodyBytes = Flowable
        .fromPublisher(
            reactiveClient.streamingExecute(SimpleRequestBuilder.get(FLOWABLE_SOURCE_URL).build()))
        .concatMap(Message::getBody).to(this::toByteArray);
    assertArrayEquals(flowableSourceBytes, bodyBytes);
  }

  @Test
  public void testWithMinimalClient() {
    try (MinimalHttpAsyncClient asyncClient = HttpAsyncClients.createMinimal()) {
      asyncClient.start();
      final HttpReactiveClient reactiveClient = HttpReactiveClients.create(asyncClient);
      {
        final byte[] bodyBytes = Flowable
            .fromPublisher(
                reactiveClient
                    .streamingExecute(SimpleRequestBuilder.get(FLOWABLE_SOURCE_URL).build(), null))
            .concatMap(Message::getBody).to(this::toByteArray);
        assertArrayEquals(flowableSourceBytes, bodyBytes);
      }
      {
        final byte[] bodyBytes = Flowable
            .fromPublisher(reactiveClient.streamingExecute(
                SimpleRequestBuilder.get(FLOWABLE_SOURCE_URL).build(), new BasicHttpContext()))
            .concatMap(Message::getBody).to(this::toByteArray);
        assertArrayEquals(flowableSourceBytes, bodyBytes);
      }
    }
  }

  private byte[] toByteArray(Publisher<ByteBuffer> pub) {
    try (ByteArrayOutputStream out = new ByteArrayOutputStream();
        WritableByteChannel channel = Channels.newChannel(out)) {
      Flowable.fromPublisher(pub).blockingForEach(channel::write);
      return out.toByteArray();
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
  }

}
