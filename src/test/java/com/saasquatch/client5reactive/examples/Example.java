package com.saasquatch.client5reactive.examples;

import static java.nio.charset.StandardCharsets.UTF_8;

import com.saasquatch.client5reactive.HttpReactiveClient;
import com.saasquatch.client5reactive.HttpReactiveClients;
import io.reactivex.rxjava3.core.Single;
import java.nio.ByteBuffer;
import org.apache.hc.client5.http.async.methods.SimpleRequestBuilder;
import org.apache.hc.client5.http.impl.async.CloseableHttpAsyncClient;
import org.apache.hc.client5.http.impl.async.HttpAsyncClients;

public class Example {

  public static void main(String[] args) throws Exception {
    // Create a CloseableHttpAsyncClient first
    try (CloseableHttpAsyncClient asyncClient = HttpAsyncClients.createDefault()) {
      /*
       * HttpReactiveClient does not support lifecycle management, so the underlying
       * CloseableHttpAsyncClient needs to be started and closed.
       */
      asyncClient.start();
      // HttpReactiveClient is just a thin wrapper
      final HttpReactiveClient reactiveClient = HttpReactiveClients.create(asyncClient);
      // Execute a simple in-memory request
      Single.fromPublisher(
          reactiveClient.execute(SimpleRequestBuilder.get("https://www.example.com").build()))
          .doOnSuccess(response -> {
            // Get the response status and body in memory
            System.out.println(response.getCode());
            System.out.println(response.getBodyText());
          })
          .blockingSubscribe();
      System.out.println("----------");
      // Execute a streaming request
      // In this case, the request is a simple in-memory request without a request body
      Single.fromPublisher(reactiveClient.streamingExecute(
          SimpleRequestBuilder.get("https://www.example.com").build()))
          .flatMapPublisher(message -> {
            // Get the status before subscribing to the streaming body
            System.out.println(message.getHead().getCode());
            return message.getBody();
          })
          // Collect the streaming body chunks into a List
          .toList()
          .map(byteBuffers -> {
            // Concatenate the body chunks and decode with UTF-8
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
