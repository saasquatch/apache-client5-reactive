package com.saasquatch.client5reactive.examples;

import static java.nio.charset.StandardCharsets.UTF_8;
import java.nio.ByteBuffer;
import org.apache.hc.client5.http.async.methods.SimpleHttpRequests;
import org.apache.hc.client5.http.impl.async.CloseableHttpAsyncClient;
import org.apache.hc.client5.http.impl.async.HttpAsyncClients;
import com.saasquatch.client5reactive.HttpReactiveClient;
import com.saasquatch.client5reactive.HttpReactiveClients;
import io.reactivex.rxjava3.core.Single;

public class Example {

  private static final String EXAMPLE_URL = "https://www.example.com";

  public static void main(String[] args) throws Exception {
    try (CloseableHttpAsyncClient asyncClient = HttpAsyncClients.createDefault()) {
      asyncClient.start();
      final HttpReactiveClient reactiveClient = HttpReactiveClients.create(asyncClient);
      Single.fromPublisher(reactiveClient.execute(SimpleHttpRequests.get(EXAMPLE_URL)))
          .doOnSuccess(response -> {
            System.out.println(response.getCode());
            System.out.println(response.getBodyText());
          })
          .blockingSubscribe();
      System.out.println("----------");
      Single.fromPublisher(reactiveClient.streamingExecute(SimpleHttpRequests.get(EXAMPLE_URL)))
          .flatMapPublisher(message -> {
            System.out.println(message.getHead().getCode());
            return message.getBody();
          })
          .toList()
          .map(byteBuffers -> {
            final int totalLength = byteBuffers.stream().mapToInt(ByteBuffer::remaining).sum();
            final ByteBuffer combined = ByteBuffer.allocate(totalLength);
            byteBuffers.forEach(combined::put);
            combined.flip();
            return UTF_8.decode(combined).toString();
          })
          .doOnSuccess(System.out::println)
          .blockingSubscribe();
    }
  }

}
