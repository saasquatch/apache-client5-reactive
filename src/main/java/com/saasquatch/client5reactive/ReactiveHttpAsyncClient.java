package com.saasquatch.client5reactive;

import java.nio.ByteBuffer;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.apache.hc.client5.http.async.HttpAsyncClient;
import org.apache.hc.client5.http.async.methods.SimpleHttpRequest;
import org.apache.hc.client5.http.async.methods.SimpleHttpResponse;
import org.apache.hc.client5.http.async.methods.SimpleRequestProducer;
import org.apache.hc.client5.http.async.methods.SimpleResponseConsumer;
import org.apache.hc.client5.http.impl.async.CloseableHttpAsyncClient;
import org.apache.hc.core5.concurrent.FutureCallback;
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
 * @see ReactiveHttpAsyncClients
 */
public interface ReactiveHttpAsyncClient {

  /**
   * @see HttpAsyncClient#execute(AsyncRequestProducer, AsyncResponseConsumer, HandlerFactory,
   *      HttpContext, FutureCallback)
   */
  <T> Publisher<T> execute(@Nonnull AsyncRequestProducer requestProducer,
      @Nonnull AsyncResponseConsumer<T> responseConsumer,
      @Nullable HandlerFactory<AsyncPushConsumer> pushHandlerFactory,
      @Nullable HttpContext context);

  /**
   * @see CloseableHttpAsyncClient#execute(AsyncRequestProducer, AsyncResponseConsumer, HttpContext,
   *      FutureCallback)
   */
  default <T> Publisher<T> execute(@Nonnull AsyncRequestProducer requestProducer,
      @Nonnull AsyncResponseConsumer<T> responseConsumer, @Nullable HttpContext context) {
    return execute(requestProducer, responseConsumer, null, context);
  }

  /**
   * @see CloseableHttpAsyncClient#execute(AsyncRequestProducer, AsyncResponseConsumer,
   *      FutureCallback)
   */
  default <T> Publisher<T> execute(@Nonnull AsyncRequestProducer requestProducer,
      @Nonnull AsyncResponseConsumer<T> responseConsumer) {
    return execute(requestProducer, responseConsumer, null);
  }

  /**
   * @see CloseableHttpAsyncClient#execute(SimpleHttpRequest, HttpContext, FutureCallback)
   */
  default Publisher<SimpleHttpResponse> execute(@Nonnull SimpleHttpRequest request,
      @Nullable HttpContext context) {
    return execute(SimpleRequestProducer.create(request), SimpleResponseConsumer.create(), context);
  }

  /**
   * @see CloseableHttpAsyncClient#execute(SimpleHttpRequest, FutureCallback)
   */
  default Publisher<SimpleHttpResponse> execute(@Nonnull SimpleHttpRequest request) {
    return execute(request, null);
  }

  /**
   * Execute the given {@link AsyncRequestProducer} and get a streaming response.
   */
  Publisher<Message<HttpResponse, Publisher<ByteBuffer>>> streamingExecute(
      @Nonnull AsyncRequestProducer requestProducer, @Nullable HttpContext context);

  /**
   * @see #streamingExecute(AsyncRequestProducer, HttpContext)
   */
  default Publisher<Message<HttpResponse, Publisher<ByteBuffer>>> streamingExecute(
      @Nonnull AsyncRequestProducer requestProducer) {
    return streamingExecute(requestProducer, null);
  }

  /**
   * @see #streamingExecute(AsyncRequestProducer, HttpContext)
   */
  default Publisher<Message<HttpResponse, Publisher<ByteBuffer>>> streamingExecute(
      @Nonnull SimpleHttpRequest request, @Nullable HttpContext context) {
    return streamingExecute(SimpleRequestProducer.create(request), context);
  }

  /**
   * @see #streamingExecute(AsyncRequestProducer, HttpContext)
   */
  default Publisher<Message<HttpResponse, Publisher<ByteBuffer>>> streamingExecute(
      @Nonnull SimpleHttpRequest request) {
    return streamingExecute(request, null);
  }

}
