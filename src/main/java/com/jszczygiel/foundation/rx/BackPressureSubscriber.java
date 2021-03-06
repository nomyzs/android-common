package com.jszczygiel.foundation.rx;

import android.support.annotation.CallSuper;
import com.jszczygiel.foundation.helpers.L;
import rx.Subscriber;
import rx.exceptions.OnErrorNotImplementedException;

public class BackPressureSubscriber<T> extends Subscriber<T> {
  @Override
  @CallSuper
  public void onStart() {
    request(1);
  }

  @Override
  public void onCompleted() {}

  @Override
  public void onError(Throwable error) {
    L.print(error);
    throw new OnErrorNotImplementedException(error);
  }

  @Override
  @CallSuper
  public void onNext(T next) {
    request(1);
  }
}
