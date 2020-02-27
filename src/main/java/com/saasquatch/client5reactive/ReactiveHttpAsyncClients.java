package com.saasquatch.client5reactive;

import java.util.Objects;
import org.apache.hc.client5.http.async.HttpAsyncClient;
import org.apache.hc.client5.http.impl.async.HttpAsyncClients;

/**
 * Factory methods for {@link ReactiveHttpAsyncClient}.
 *
 * @author sli
 * @see HttpAsyncClients
 */
public final class ReactiveHttpAsyncClients {

  private ReactiveHttpAsyncClients() {}

  /**
   * Create a {@link ReactiveHttpAsyncClient} from a given {@link HttpAsyncClient}. Note that the
   * created {@link ReactiveHttpAsyncClient} is simply a wrapper of the {@link HttpAsyncClient} and
   * does not support lifecycle management, so you'll need to manage the lifecycle of the given
   * {@link HttpAsyncClient} yourself.
   */
  public static ReactiveHttpAsyncClient create(HttpAsyncClient httpAsyncClient) {
    Objects.requireNonNull(httpAsyncClient);
    return new ReactiveHttpAsyncClientImpl(httpAsyncClient);
  }

}
