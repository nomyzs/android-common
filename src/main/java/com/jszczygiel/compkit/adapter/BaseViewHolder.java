package com.jszczygiel.compkit.adapter;

import android.content.Context;
import android.support.annotation.CallSuper;
import android.support.annotation.ColorInt;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import java.util.List;

public abstract class BaseViewHolder<T> extends RecyclerView.ViewHolder {
  protected final Context context;
  protected RecyclerView recyclerView;

  public BaseViewHolder(View itemView, Context context) {
    super(itemView);
    this.context = context;
  }

  @CallSuper
  public void onViewAttachedToWindow(RecyclerView recyclerView) {
    this.recyclerView = recyclerView;
  }

  @CallSuper
  public void onViewDetachedFromWindow() {
    this.recyclerView = null;
  }

  @CallSuper
  public void onBind(T model, BaseInteractionListener listener, List<Object> payloads) {
    if (payloads == null || payloads.size() == 0) {
      onBind(model, listener);
    }
  }

  public void onBind(T model, BaseInteractionListener listener) {}

  protected final String getString(int resourceId) {
    return context.getString(resourceId);
  }

  protected final String[] getStringArray(int resourceId) {
    return context.getResources().getStringArray(resourceId);
  }

  protected final String getString(int resourceId, Object... formatArgs) {
    return context.getString(resourceId, formatArgs);
  }

  protected final String getQuantityString(int id, int quantity, Object... formatArgs) {
    return context.getResources().getQuantityString(id, quantity, formatArgs);
  }

  @ColorInt
  protected final int getColor(int resourceId) {
    return context.getResources().getColor(resourceId);
  }

  protected final double getDimension(int dimenId) {
    return context.getResources().getDimension(dimenId);
  }

  protected boolean isViewAvailable() {
    return itemView != null;
  }

  /**
   * Abstract click listener, by default this methods do nothing. This should also apply to classes
   * that extend this one.
   */
  public abstract static class BaseInteractionListener {
    public void onItemClick(BaseViewModel model, View itemView) {}

    public void onDetailsClick(BaseViewModel model) {}

    public void onHeaderClick(View header, int position, long headerId) {}
  }
}
