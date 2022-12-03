package com.saasquatch.client5reactive;

import io.reactivex.rxjava3.core.Maybe;
import java.nio.ByteBuffer;
import java.util.Objects;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.apache.hc.client5.http.async.HttpAsyncClient;
import org.apache.hc.core5.http.HttpResponse;
import org.apache.hc.core5.http.Message;
import org.apache.hc.core5.http.nio.AsyncPushConsumer;
import org.apache.hc.core5.http.nio.AsyncRequestProducer;
import org.apache.hc.core5.http.nio.AsyncResponseConsumer;
import org.apache.hc.core5.http.nio.HandlerFactory;
import org.apache.hc.core5.http.protocol.HttpContext;
import org.apache.hc.core5.reactive.ReactiveResponseConsumer;
import org.reactivestreams.Publisher;

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
  public <T> Publisher<T> execute(@Nonnull AsyncRequestProducer requestProducer,
      @Nonnull AsyncResponseConsumer<T> responseConsumer,
      @Nullable HandlerFactory<AsyncPushConsumer> pushHandlerFactory,
      @Nullable HttpContext context) {
    Objects.requireNonNull(requestProducer);
    Objects.requireNonNull(responseConsumer);
    //noinspection CodeBlock2Expr
    return Maybe.<T>create(emitter -> {
      httpAsyncClient.execute(requestProducer, responseConsumer, pushHandlerFactory, context,
          FutureCallbacks.maybeEmitter(emitter));
    }).toFlowable();
  }

  @Override
  public Publisher<Message<HttpResponse, Publisher<ByteBuffer>>> streamingExecute(
      @Nonnull AsyncRequestProducer requestProducer,
      @Nullable HandlerFactory<AsyncPushConsumer> pushHandlerFactory,
      @Nullable HttpContext context) {
    Objects.requireNonNull(requestProducer);
    /*
     * Semantically this should be a Single instead of a Maybe, but using Single here requires an
     * additional implementation of FutureCallback, and since we are returning a Publisher, it
     * doesn't really make a difference.
     */
    return Maybe.<Message<HttpResponse, Publisher<ByteBuffer>>>create(emitter -> {
      final ReactiveResponseConsumer responseConsumer =
          new ReactiveResponseConsumer(FutureCallbacks.maybeEmitter(emitter));
      httpAsyncClient.execute(requestProducer, responseConsumer, pushHandlerFactory, context, null);
    }).toFlowable();
  }

}
