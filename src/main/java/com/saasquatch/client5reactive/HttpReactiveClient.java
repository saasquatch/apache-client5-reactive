package com.saasquatch.client5reactive;

import java.nio.ByteBuffer;
import java.util.concurrent.Future;
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
 * Thin wrapper around Apache {@link HttpAsyncClient} to expose
 * <a href="https://www.reactive-streams.org/">Reactive Streams</a> interfaces.<br>
 * The methods in this interface aim to mirror the ones in {@link HttpAsyncClient} and
 * {@link CloseableHttpAsyncClient}.
 *
 * @author sli
 * @see HttpReactiveClients
 */
public interface HttpReactiveClient {

  /**
   * Execute the given request. This method is equivalent to
   * {@link HttpAsyncClient#execute(AsyncRequestProducer, AsyncResponseConsumer, HandlerFactory, HttpContext , FutureCallback)}.
   * If the {@link Future} produced by the equivalent {@link HttpAsyncClient} method completes with
   * {@code null}, then the returning {@link Publisher} of this method will complete with no
   * element.
   */
  <T> Publisher<T> execute(@Nonnull AsyncRequestProducer requestProducer,
      @Nonnull AsyncResponseConsumer<T> responseConsumer,
      @Nullable HandlerFactory<AsyncPushConsumer> pushHandlerFactory,
      @Nullable HttpContext context);

  /**
   * Convenience method for
   * {@link #execute(AsyncRequestProducer, AsyncResponseConsumer, HandlerFactory, HttpContext)},
   * equivalent to
   * {@link CloseableHttpAsyncClient#execute(AsyncRequestProducer, AsyncResponseConsumer, HttpContext, FutureCallback)}
   */
  default <T> Publisher<T> execute(@Nonnull AsyncRequestProducer requestProducer,
      @Nonnull AsyncResponseConsumer<T> responseConsumer, @Nullable HttpContext context) {
    return execute(requestProducer, responseConsumer, null, context);
  }

  /**
   * Convenience method for
   * {@link #execute(AsyncRequestProducer, AsyncResponseConsumer, HandlerFactory, HttpContext)},
   * equivalent to
   * {@link CloseableHttpAsyncClient#execute(AsyncRequestProducer, AsyncResponseConsumer, FutureCallback)}.
   */
  default <T> Publisher<T> execute(@Nonnull AsyncRequestProducer requestProducer,
      @Nonnull AsyncResponseConsumer<T> responseConsumer) {
    return execute(requestProducer, responseConsumer, null);
  }

  /**
   * Execute a simple in-memory request and get a simple in-memory response. This method is
   * equivalent to
   * {@link CloseableHttpAsyncClient#execute(SimpleHttpRequest, HttpContext, FutureCallback)}. The
   * returning {@link Publisher} completes with exactly 1 element.
   */
  default Publisher<SimpleHttpResponse> execute(@Nonnull SimpleHttpRequest request,
      @Nullable HttpContext context) {
    return execute(SimpleRequestProducer.create(request), SimpleResponseConsumer.create(), context);
  }

  /**
   * Convenience method for {@link #execute(SimpleHttpRequest, HttpContext)}, equivalent to
   * {@link CloseableHttpAsyncClient#execute(SimpleHttpRequest, FutureCallback)}.
   */
  default Publisher<SimpleHttpResponse> execute(@Nonnull SimpleHttpRequest request) {
    return execute(request, null);
  }

  /**
   * Execute the given request and get a streaming response body as a {@link Publisher} of
   * {@link ByteBuffer}s. The returning {@link Publisher} completes with exactly 1 element. The
   * {@link Publisher} within the returning {@link Publisher} may contain 0 to n elements.
   */
  Publisher<Message<HttpResponse, Publisher<ByteBuffer>>> streamingExecute(
      @Nonnull AsyncRequestProducer requestProducer,
      @Nullable HandlerFactory<AsyncPushConsumer> pushHandlerFactory,
      @Nullable HttpContext context);

  /**
   * Convenience method for
   * {@link #streamingExecute(AsyncRequestProducer, HandlerFactory, HttpContext)}
   */
  default Publisher<Message<HttpResponse, Publisher<ByteBuffer>>> streamingExecute(
      @Nonnull AsyncRequestProducer requestProducer, @Nullable HttpContext context) {
    return streamingExecute(requestProducer, null, context);
  }

  /**
   * Convenience method for
   * {@link #streamingExecute(AsyncRequestProducer, HandlerFactory, HttpContext)}
   */
  default Publisher<Message<HttpResponse, Publisher<ByteBuffer>>> streamingExecute(
      @Nonnull AsyncRequestProducer requestProducer) {
    return streamingExecute(requestProducer, null);
  }

  /**
   * Execute a simple in-memory request and get a streaming response. Convenience method for
   * {@link #streamingExecute(AsyncRequestProducer, HandlerFactory, HttpContext)}
   */
  default Publisher<Message<HttpResponse, Publisher<ByteBuffer>>> streamingExecute(
      @Nonnull SimpleHttpRequest request, @Nullable HttpContext context) {
    return streamingExecute(SimpleRequestProducer.create(request), context);
  }

  /**
   * Convenience method for
   * {@link #streamingExecute(AsyncRequestProducer, HandlerFactory, HttpContext)}
   */
  default Publisher<Message<HttpResponse, Publisher<ByteBuffer>>> streamingExecute(
      @Nonnull SimpleHttpRequest request) {
    return streamingExecute(request, null);
  }

}
