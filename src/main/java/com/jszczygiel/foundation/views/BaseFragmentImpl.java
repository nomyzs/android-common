package com.jszczygiel.foundation.views;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.CallSuper;
import android.support.annotation.ColorInt;
import android.support.annotation.ColorRes;
import android.support.annotation.DimenRes;
import android.support.annotation.Nullable;
import android.support.annotation.PluralsRes;
import android.support.annotation.StringRes;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import com.jszczygiel.foundation.helpers.SystemHelper;
import com.jszczygiel.foundation.presenters.interfaces.BasePresenter;
import com.jszczygiel.foundation.views.interfaces.BaseFragment;
import rx.Subscription;
import rx.subscriptions.CompositeSubscription;

public abstract class BaseFragmentImpl<T extends BasePresenter> extends Fragment
    implements BaseFragment<T> {

  CompositeSubscription subscriptionList;
  /**
   * instance of presenter
   */
  private T presenter;

  private boolean isTablet;

  @Override
  @CallSuper
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setPresenter();
    setUpPresenter(presenter);
    subscriptionList = new CompositeSubscription();
    getPresenter().onAttach(this, getArguments());
  }

  private void setPresenter() {
    if (presenter == null) {
      this.presenter = initializePresenter();
    }
  }

  /**
   * This function can be overridden to setup presenter. It is being called in onCreate after
   * initializing presenter
   *
   * @param presenter presenter to setup
   */
  @CallSuper
  @Override
  public void setUpPresenter(T presenter) {
    isTablet = SystemHelper.isTablet(getActivity());
    presenter.setIsTablet(isTablet);
    presenter.setOrientation(getResources().getConfiguration().orientation);
  }

  /**
   * @return provides new instance of presenter
   */
  public abstract T initializePresenter();

  @Override
  public T getPresenter() {
    return presenter;
  }

  @Override
  public boolean isAvailable() {
    return !isDetached()
        && !isRemoving()
        && getPresenter() != null
        && getActivity() != null
        && !getActivity().isFinishing();
  }

  @Override
  public boolean isTablet() {
    return isTablet;
  }

  @Override
  public void finish() {
    ActivityCompat.finishAfterTransition(getActivity());
  }

  @Override
  public void setResult(int resultCode, Intent data) {
    getActivity().setResult(resultCode, data);
  }

  @Override
  public void setResult(int resultCode) {
    getActivity().setResult(resultCode);
  }

  @Override
  public void showToast(@StringRes final int resId, final String... formatArgs) {
    if (isAvailable()) {
      Toast.makeText(getContext(), getString(resId, (Object[]) formatArgs), Toast.LENGTH_LONG)
          .show();
    }
  }

  @Override
  public void showShortToast(@StringRes final int resId, final String... formatArgs) {
    if (isAvailable()) {
      Toast.makeText(getContext(), getString(resId, (Object[]) formatArgs), Toast.LENGTH_SHORT)
          .show();
    }
  }

  @Override
  public void showToast(@PluralsRes final int id, final int quantity, final String... formatArgs) {
    if (isAvailable()) {
      Toast.makeText(
          getContext(),
          getQuantityString(id, quantity, (Object[]) formatArgs),
          Toast.LENGTH_LONG)
          .show();
    }
  }

  public String getQuantityString(@PluralsRes int id, int quantity, Object... formatArgs) {
    return getResources().getQuantityString(id, quantity, formatArgs);
  }

  @Nullable
  @Override
  public View onCreateView(
      LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
    View view = inflater.inflate(getLayoutId(), container, false);
    return view;
  }

  @Override
  public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    if (savedInstanceState != null) {
      getPresenter().onRestoreInstanceState(savedInstanceState);
    } else {
      getPresenter().onLoad(getArguments());
    }
  }

  @Override
  @CallSuper
  public void onDestroyView() {
    if (!isStaticView()) {
      clear();
    }
    super.onDestroyView();
  }

  public boolean isStaticView() {
    return false;
  }

  public void clear() {
    subscriptionList.unsubscribe();
    getPresenter().onDetach();
    presenter = null;
  }

  protected abstract int getLayoutId();

  public boolean onBackPressed() {
    SystemHelper.hideKeyboard(getActivity(), getActivity().getCurrentFocus());
    finish();
    return true;
  }

  public void setIntent(Intent intent) {
    getActivity().setIntent(intent);
  }

  @ColorInt
  public int getColor(@ColorRes int colorId) {
    return ContextCompat.getColor(getContext(), colorId);
  }

  public void finishWithResult(int result, Intent intent) {
    getActivity().setResult(result, intent);
    finish();
  }

  protected float getDimension(@DimenRes int dimenRes) {
    return getResources().getDimension(dimenRes);
  }

  public String getTitle() {
    return (String) getActivity().getTitle();
  }

  public void onNewIntent(Intent intent) {
  }

  @Override
  public void addSubscriptionToLifeCycle(Subscription subscription) {
    subscriptionList.add(subscription);
  }

  @Override
  public void removeSubscriptionFromLifeCycle(Subscription subscription) {
    if (subscription != null) {
      subscriptionList.remove(subscription);
    }
  }
}
