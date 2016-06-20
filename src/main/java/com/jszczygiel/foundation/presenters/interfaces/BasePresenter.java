package com.jszczygiel.foundation.presenters.interfaces;

import android.os.Bundle;

import rx.Subscription;

public interface BasePresenter<T> {
    void onAttach(T view);

    void onDetach();

    boolean isViewAvailable();

    T getView();

    void setIsTablet(boolean isTablet);

    void addSubscriptionToLifeCycle(Subscription subscription);

    void removeSubscriptionFromLifeCycle(Subscription subscription);

    void onSaveInstanceState(Bundle outState);

    void onRestoreInstanceState(Bundle savedInstanceState);

    void setOrientation(int orientation);
}
