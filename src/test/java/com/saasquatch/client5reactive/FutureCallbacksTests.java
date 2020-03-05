package com.saasquatch.client5reactive;

import java.io.IOException;
import java.util.concurrent.CancellationException;
import org.apache.hc.core5.concurrent.BasicFuture;
import org.junit.jupiter.api.Test;
import io.reactivex.rxjava3.core.Maybe;

public class FutureCallbacksTests {

  @Test
  public void testMaybeEmitter() {
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
