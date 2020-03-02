package com.saasquatch.client5reactive;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import java.net.URL;
import java.nio.ByteBuffer;
import org.apache.hc.client5.http.async.methods.SimpleHttpRequests;
import org.apache.hc.client5.http.async.methods.SimpleRequestProducer;
import org.apache.hc.client5.http.async.methods.SimpleResponseConsumer;
import org.apache.hc.client5.http.impl.async.CloseableHttpAsyncClient;
import org.apache.hc.client5.http.impl.async.HttpAsyncClients;
import org.apache.hc.core5.http.Message;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import com.google.common.io.ByteStreams;
import com.google.common.primitives.Bytes;
import io.reactivex.rxjava3.core.Flowable;

public class StreamingTests {

  private static final String FLOWABLE_URL =
      "https://cdn.jsdelivr.net/gh/ReactiveX/RxJava@81f0569a8b9b7d27059f127b90fd7335118b2ee4/src/main/java/io/reactivex/rxjava3/core/Flowable.java";
  private static CloseableHttpAsyncClient asyncClient;
  private static ReactiveHttpAsyncClient reactiveClient;
  private static byte[] flowableSourceBytes;

  @BeforeAll
  public static void beforeAll() throws Exception {
    asyncClient = HttpAsyncClients.createDefault();
    asyncClient.start();
    reactiveClient = ReactiveHttpAsyncClients.create(asyncClient);
    flowableSourceBytes = ByteStreams.toByteArray(new URL(FLOWABLE_URL).openStream());
  }

  @AfterAll
  public static void afterAll() throws Exception {
    asyncClient.close();
  }

  @Test
  public void testVanillaExecuteWorks() {
    final byte[] bodyBytes = Flowable.fromPublisher(
        reactiveClient.execute(SimpleRequestProducer.create(SimpleHttpRequests.get(FLOWABLE_URL)),
            SimpleResponseConsumer.create()))
        .blockingSingle().getBodyBytes();
    assertArrayEquals(flowableSourceBytes, bodyBytes);
  }

  @Test
  public void testVanillaStreamingWorks() {
    final byte[] bodyBytes = Flowable
        .fromPublisher(reactiveClient.streamingExecute(
            SimpleRequestProducer.create(SimpleHttpRequests.get(FLOWABLE_URL)), null))
        .concatMap(Message::getBody).map(bb -> byteBufferToArray(bb)).reduce(Bytes::concat)
        .blockingGet();
    assertArrayEquals(flowableSourceBytes, bodyBytes);
  }

  @Test
  public void testBasicStreamingWorks() {
    final byte[] bodyBytes = Flowable
        .fromPublisher(reactiveClient.streamingExecute(SimpleHttpRequests.get(FLOWABLE_URL)))
        .concatMap(Message::getBody).map(bb -> byteBufferToArray(bb)).reduce(Bytes::concat)
        .blockingGet();
    assertArrayEquals(flowableSourceBytes, bodyBytes);
  }

  private static byte[] byteBufferToArray(ByteBuffer bb) {
    final byte[] arr = new byte[bb.remaining()];
    bb.get(arr);
    return arr;
  }

}
