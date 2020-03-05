package com.saasquatch.client5reactive;

import java.nio.ByteBuffer;
import java.util.Objects;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.apache.hc.client5.http.async.HttpAsyncClient;
import org.apache.hc.client5.http.impl.async.MinimalHttpAsyncClient;
import org.apache.hc.client5.http.protocol.HttpClientContext;
import org.apache.hc.core5.http.HttpResponse;
import org.apache.hc.core5.http.Message;
import org.apache.hc.core5.http.nio.AsyncPushConsumer;
import org.apache.hc.core5.http.nio.AsyncRequestProducer;
import org.apache.hc.core5.http.nio.AsyncResponseConsumer;
import org.apache.hc.core5.http.nio.HandlerFactory;
import org.apache.hc.core5.http.protocol.HttpContext;
import org.apache.hc.core5.reactive.ReactiveResponseConsumer;
import org.reactivestreams.Publisher;
import io.reactivex.rxjava3.core.Maybe;

/**
 * Concrete implementation of {@link HttpReactiveClient}.
 *
 * @author sli
 */
final class HttpReactiveClientImpl implements HttpReactiveClient {

  private final HttpAsyncClient httpAsyncClient;

  HttpReactiveClientImpl(HttpAsyncClient httpAsyncClient) {
    this.httpAsyncClient = httpAsyncClient;
  }

  @Override
  public <T> Publisher<T> execute(AsyncRequestProducer requestProducer,
      AsyncResponseConsumer<T> responseConsumer,
      HandlerFactory<AsyncPushConsumer> pushHandlerFactory, HttpContext context) {
    Objects.requireNonNull(requestProducer);
    Objects.requireNonNull(responseConsumer);
    return Maybe.<T>create(emitter -> {
      httpAsyncClient.execute(requestProducer, responseConsumer, pushHandlerFactory,
          defaultHttpContext(context), FutureCallbacks.maybeEmitter(emitter));
    }).toFlowable();
  }

  @Override
  public Publisher<Message<HttpResponse, Publisher<ByteBuffer>>> streamingExecute(
      AsyncRequestProducer requestProducer, HandlerFactory<AsyncPushConsumer> pushHandlerFactory,
      HttpContext context) {
    Objects.requireNonNull(requestProducer);
    /*
     * Semantically this should be a Single instead of a Maybe, but using Single here requires an
     * additional implementation of FutureCallback, and since we are returning a Publisher, it
     * doesn't really make a difference.
     */
    return Maybe.<Message<HttpResponse, Publisher<ByteBuffer>>>create(emitter -> {
      final ReactiveResponseConsumer responseConsumer =
          new ReactiveResponseConsumer(FutureCallbacks.maybeEmitter(emitter));
      httpAsyncClient.execute(requestProducer, responseConsumer, pushHandlerFactory,
          defaultHttpContext(context), null);
    }).toFlowable();
  }

  /**
   * Create a default {@link HttpContext} if the given one is null. In theory this method should not
   * be needed, since according to the JavaDoc in {@link HttpAsyncClient}, {@link HttpContext} can
   * be null. However, in Apache HttpClient version 5.0 there's a bug where some implementations of
   * {@link HttpAsyncClient} like {@link MinimalHttpAsyncClient} reject null {@link HttpContext}. An
   * issue has already been filed
   * <a href="https://issues.apache.org/jira/browse/HTTPCLIENT-2059">here</a>. Once that's fixed,
   * this method can be removed.
   */
  @Nonnull
  private static HttpContext defaultHttpContext(@Nullable HttpContext context) {
    return context == null ? HttpClientContext.create() : context;
  }

}
