package com.saasquatch.client5reactive;

import java.nio.ByteBuffer;
import org.apache.hc.client5.http.async.HttpAsyncClient;
import org.apache.hc.client5.http.async.methods.SimpleHttpRequest;
import org.apache.hc.client5.http.async.methods.SimpleHttpResponse;
import org.apache.hc.client5.http.async.methods.SimpleRequestProducer;
import org.apache.hc.client5.http.async.methods.SimpleResponseConsumer;
import org.apache.hc.client5.http.impl.async.CloseableHttpAsyncClient;
import org.apache.hc.client5.http.protocol.HttpClientContext;
import org.apache.hc.core5.http.HttpResponse;
import org.apache.hc.core5.http.Message;
import org.apache.hc.core5.http.nio.AsyncPushConsumer;
import org.apache.hc.core5.http.nio.AsyncRequestProducer;
import org.apache.hc.core5.http.nio.AsyncResponseConsumer;
import org.apache.hc.core5.http.nio.HandlerFactory;
import org.apache.hc.core5.http.protocol.HttpContext;
import org.reactivestreams.Publisher;

/**
 * Thin wrapper around Apache {@link HttpAsyncClient} to expose Reactive Streams interfaces.<br>
 * The methods in this interface aim to mirror the ones in {@link HttpAsyncClient} and
 * {@link CloseableHttpAsyncClient}.
 *
 * @author sli
 */
public interface ReactiveHttpAsyncClient {

  <T> Publisher<T> execute(AsyncRequestProducer requestProducer,
      AsyncResponseConsumer<T> responseConsumer,
      HandlerFactory<AsyncPushConsumer> pushHandlerFactory, HttpContext context);

  default <T> Publisher<T> execute(AsyncRequestProducer requestProducer,
      AsyncResponseConsumer<T> responseConsumer, HttpContext context) {
    return execute(requestProducer, responseConsumer, null, context);
  }

  default <T> Publisher<T> execute(AsyncRequestProducer requestProducer,
      AsyncResponseConsumer<T> responseConsumer) {
    return execute(requestProducer, responseConsumer, null, HttpClientContext.create());
  }

  default Publisher<SimpleHttpResponse> execute(SimpleHttpRequest request, HttpContext context) {
    return execute(SimpleRequestProducer.create(request), SimpleResponseConsumer.create(), null,
        context);
  }

  default Publisher<SimpleHttpResponse> execute(SimpleHttpRequest request) {
    return execute(request, HttpClientContext.create());
  }

  Publisher<Message<HttpResponse, Publisher<ByteBuffer>>> streamingExecute(
      AsyncRequestProducer requestProducer, HttpContext context);

  default Publisher<Message<HttpResponse, Publisher<ByteBuffer>>> streamingExecute(
      AsyncRequestProducer requestProducer) {
    return streamingExecute(requestProducer, HttpClientContext.create());
  }

  default Publisher<Message<HttpResponse, Publisher<ByteBuffer>>> streamingExecute(
      SimpleHttpRequest request, HttpContext context) {
    return streamingExecute(SimpleRequestProducer.create(request), context);
  }

  default Publisher<Message<HttpResponse, Publisher<ByteBuffer>>> streamingExecute(
      SimpleHttpRequest request) {
    return streamingExecute(request, HttpClientContext.create());
  }

}
