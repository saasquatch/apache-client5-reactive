package com.saasquatch.client5reactive;

import java.util.NoSuchElementException;
import org.apache.hc.core5.concurrent.FutureCallback;
import io.reactivex.rxjava3.core.MaybeEmitter;
import io.reactivex.rxjava3.core.SingleEmitter;

final class FutureCallbacks {

  FutureCallbacks() {}

  public static <T> FutureCallback<T> singleEmitter(SingleEmitter<T> emitter) {
    return new FutureCallback<T>() {
      @Override
      public void completed(T result) {
        emitter.onSuccess(result);
      }

      @Override
      public void failed(Exception ex) {
        emitter.onError(ex);
      }

      @Override
      public void cancelled() {
        emitter.onError(new NoSuchElementException());
      }
    };
  }

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
        emitter.onError(new NoSuchElementException());
      }
    };
  }

}
