package com.saasquatch.client5reactive;

import java.nio.ByteBuffer;
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
import io.reactivex.rxjava3.core.Maybe;
import io.reactivex.rxjava3.core.Single;

final class ReactiveHttpAsyncClientImpl implements ReactiveHttpAsyncClient {

  private final HttpAsyncClient httpAsyncClient;

  ReactiveHttpAsyncClientImpl(HttpAsyncClient httpAsyncClient) {
    this.httpAsyncClient = httpAsyncClient;
  }

  @Override
  public <T> Publisher<T> execute(AsyncRequestProducer requestProducer,
      AsyncResponseConsumer<T> responseConsumer,
      HandlerFactory<AsyncPushConsumer> pushHandlerFactory, HttpContext context) {
    return Maybe.<T>create(emitter -> {
      httpAsyncClient.execute(requestProducer, responseConsumer, pushHandlerFactory, context,
          FutureCallbacks.maybeEmitter(emitter));
    }).toFlowable();
  }

  @Override
  public Publisher<Message<HttpResponse, Publisher<ByteBuffer>>> streamingExecute(
      AsyncRequestProducer requestProducer, HttpContext context) {
    return Single.<Message<HttpResponse, Publisher<ByteBuffer>>>create(emitter -> {
      final ReactiveResponseConsumer responseConsumer =
          new ReactiveResponseConsumer(FutureCallbacks.singleEmitter(emitter));
      httpAsyncClient.execute(requestProducer, responseConsumer, null, context, null);
    }).toFlowable();
  }

}
