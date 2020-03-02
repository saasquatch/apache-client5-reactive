package com.saasquatch.client5reactive;

import java.io.IOException;
import java.util.concurrent.CancellationException;
import org.apache.hc.core5.concurrent.BasicFuture;
import org.junit.jupiter.api.Test;
import io.reactivex.rxjava3.core.Maybe;
import io.reactivex.rxjava3.core.Single;

public class FutureCallbacksTests {

  @Test
  public void testSingle() {
    Single.<Integer>create(emitter -> {
      final BasicFuture<Integer> future = new BasicFuture<>(FutureCallbacks.singleEmitter(emitter));
      future.completed(1);
    }).test().assertResult(1);
    Single.<Integer>create(emitter -> {
      final BasicFuture<Integer> future = new BasicFuture<>(FutureCallbacks.singleEmitter(emitter));
      future.completed(null);
    }).test().assertError(NullPointerException.class);
    Single.<Integer>create(emitter -> {
      final BasicFuture<Integer> future = new BasicFuture<>(FutureCallbacks.singleEmitter(emitter));
      future.failed(new IOException());
    }).test().assertError(IOException.class);
    Single.<Integer>create(emitter -> {
      final BasicFuture<Integer> future = new BasicFuture<>(FutureCallbacks.singleEmitter(emitter));
      future.cancel();
    }).test().assertError(CancellationException.class);
  }

  @Test
  public void testMaybe() {
    Maybe.<Integer>create(emitter -> {
      final BasicFuture<Integer> future = new BasicFuture<>(FutureCallbacks.maybeEmitter(emitter));
      future.completed(1);
    }).test().assertResult(1);
    Maybe.<Integer>create(emitter -> {
      final BasicFuture<Integer> future = new BasicFuture<>(FutureCallbacks.maybeEmitter(emitter));
      future.completed(null);
    }).test().assertComplete();
    Maybe.<Integer>create(emitter -> {
      final BasicFuture<Integer> future = new BasicFuture<>(FutureCallbacks.maybeEmitter(emitter));
      future.failed(new IOException());
    }).test().assertError(IOException.class);
    Maybe.<Integer>create(emitter -> {
      final BasicFuture<Integer> future = new BasicFuture<>(FutureCallbacks.maybeEmitter(emitter));
      future.cancel();
    }).test().assertError(CancellationException.class);
  }

}
