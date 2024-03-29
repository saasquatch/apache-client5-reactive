package com.saasquatch.client5reactive;

import io.reactivex.rxjava3.core.MaybeEmitter;
import java.util.concurrent.CancellationException;
import org.apache.hc.core5.concurrent.FutureCallback;

/**
 * Utilities for {@link FutureCallback}s. Not public.
 *
 * @author sli
 */
final class FutureCallbacks {

  private FutureCallbacks() {}

  public static <T> FutureCallback<T> maybeEmitter(MaybeEmitter<T> emitter) {
    return new FutureCallback<T>() {

      @Override
      public void completed(T result) {
        if (result == null) {
          emitter.onComplete();
        } else {
          emitter.onSuccess(result);
        }
      }

      @Override
      public void failed(Exception ex) {
        emitter.onError(ex);
      }

      @Override
      public void cancelled() {
        emitter.onError(cancelledException());
      }

    };
  }

  private static Exception cancelledException() {
    return new CancellationException("Future cancelled");
  }

}
