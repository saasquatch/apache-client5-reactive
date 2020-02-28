package com.saasquatch.client5reactive;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import java.net.URL;
import org.apache.hc.client5.http.async.methods.SimpleHttpRequests;
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
  public void testBasicStreamingWorks() throws Exception {
    final byte[] bodyBytes1 = ByteStreams.toByteArray(new URL(FLOWABLE_URL).openStream());
    final byte[] bodyBytes2 = Flowable
        .fromPublisher(reactiveClient.streamingExecute(SimpleHttpRequests.get(FLOWABLE_URL)))
        .concatMap(Message::getBody).map(bb -> {
          final byte[] arr = new byte[bb.remaining()];
          bb.get(arr);
          return arr;
        }).reduce(Bytes::concat).blockingGet();
    assertArrayEquals(bodyBytes1, bodyBytes2);
  }

}
