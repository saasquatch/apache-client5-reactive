package com.saasquatch.client5reactive;

import java.util.Objects;
import org.apache.hc.client5.http.async.HttpAsyncClient;

public class ReactiveHttpAsyncClients {

  public static ReactiveHttpAsyncClient create(HttpAsyncClient httpAsyncClient) {
    Objects.requireNonNull(httpAsyncClient);
    return new ReactiveHttpAsyncClientImpl(httpAsyncClient);
  }

}
