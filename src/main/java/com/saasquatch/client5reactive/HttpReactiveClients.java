package com.saasquatch.client5reactive;

import java.util.Objects;
import javax.annotation.Nonnull;
import org.apache.hc.client5.http.async.HttpAsyncClient;
import org.apache.hc.client5.http.impl.async.CloseableHttpAsyncClient;
import org.apache.hc.client5.http.impl.async.HttpAsyncClients;

/**
 * Factory methods for {@link HttpReactiveClient}.
 *
 * @author sli
 * @see HttpAsyncClients
 */
public final class HttpReactiveClients {

  private HttpReactiveClients() {}

  /**
   * Create a {@link HttpReactiveClient} from a given {@link HttpAsyncClient}. Note that the created
   * {@link HttpReactiveClient} is simply a wrapper of the {@link HttpAsyncClient} and does not
   * support state management, so you'll need to manage the state of the given
   * {@link HttpAsyncClient} yourself by calling {@link CloseableHttpAsyncClient#start()},
   * {@link CloseableHttpAsyncClient#close()}, etc.
   */
  @Nonnull
  public static HttpReactiveClient create(@Nonnull HttpAsyncClient httpAsyncClient) {
    Objects.requireNonNull(httpAsyncClient);
    return new HttpReactiveClientImpl(httpAsyncClient);
  }

}
