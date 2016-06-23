package com.jszczygiel.compkit.adapter;

import android.content.Context;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.CallSuper;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.jszczygiel.compkit.collections.SortedList;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;

import rx.Observable;
import rx.functions.Func1;

public abstract class BaseRecyclerAdapter extends RecyclerView.Adapter<BaseViewHolder> {

    protected static final String LIST = "extra_list";
    protected static final String SIZE = "extra_size";
    protected static final int NO_TYPE = -1;
    protected static final String SELECTED_ID = "selected_id";
    protected final LayoutInflater inflater;
    protected SortedList<BaseViewModel> collection;
    protected final Context context;
    protected RecyclerView recyclerView;
    protected boolean isSelectable = false;
    protected String selectedId = "";

    public BaseRecyclerAdapter(Context context) {
        this.context = context;
        this.inflater = LayoutInflater.from(context);
        this.collection = new SortedList<>(BaseViewModel.class, new SortedList.Callback<BaseViewModel>() {
            @Override
            public int compare(BaseViewModel o1, BaseViewModel o2) {
                return getComparator().compare(o1, o2);
            }

            @Override
            public void onInserted(int position, int count) {
                notifyItemRangeInserted(position, count);
            }

            @Override
            public void onRemoved(int position, int count) {
                notifyItemRangeRemoved(position, count);
            }

            @Override
            public void onMoved(int fromPosition, int toPosition) {
                notifyItemMoved(fromPosition, toPosition);
            }

            @Override
            public void onChanged(int position, int count) {
            }

            @Override
            public boolean areContentsTheSame(BaseViewModel oldItem, BaseViewModel newItem) {
                return checkIfContentIsEqual(oldItem, newItem);
            }

            @Override
            public boolean areItemsTheSame(BaseViewModel item1, BaseViewModel item2) {
                return !(item1 == null || item2 == null) && item1.equals(item2);
            }
        });
    }

    protected abstract boolean checkIfContentIsEqual(BaseViewModel oldItem, BaseViewModel newItem);

    protected abstract Comparator<BaseViewModel> getComparator();

    @Override
    public void onViewAttachedToWindow(BaseViewHolder holder) {
        holder.onViewAttachedToWindow(recyclerView);
    }

    @Override
    public void onViewDetachedFromWindow(BaseViewHolder holder) {
        holder.onViewDetachedFromWindow();
    }

    // collection management
    public BaseViewModel getItem(int position) {
        return collection.get(position);
    }

    // collection management
    public BaseViewModel getItemSafe(int position) {
        return collection.getSafe(position);
    }

    @Override
    public int getItemCount() {
        return collection.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (position < collection.size()) {
            BaseViewModel item = getItem(position);
            if (item != null) {
                return getItem(position).getModelType();
            }
        }
        return NO_TYPE;
    }

    public synchronized boolean remove(BaseViewModel item) {
        return collection.remove(item);
    }

    public synchronized boolean removeById(String id) {
        return collection.removeById(id);
    }

    public synchronized int indexOfAdding(BaseViewModel toUpdate) {
        return collection.indexOfAdding(toUpdate);
    }

    public synchronized int indexOf(BaseViewModel toUpdate) {
        return collection.indexOf(toUpdate);
    }

    public synchronized int addOrUpdate(BaseViewModel toUpdate) {
        int index = collection.indexOf(toUpdate);
        if (index == SortedList.INVALID_POSITION) {
            return addInternal(toUpdate);
        } else {
            return updateInternal(index, toUpdate, null);
        }
    }

    private synchronized int addInternal(BaseViewModel toUpdate) {
        return collection.add(toUpdate);
    }

    private synchronized int updateInternal(int index, BaseViewModel toUpdate, Object payload) {
        if (collection.updateItemAt(index, toUpdate)) {
            notifyItemChanged(index, payload);
        }
        return index;
    }

    public synchronized int update(BaseViewModel toUpdate) {
        return update(toUpdate, null);
    }

    public synchronized int update(BaseViewModel toUpdate, Object payload) {
        int index = collection.indexOf(toUpdate);
        if (index != SortedList.INVALID_POSITION) {
            return updateInternal(index, toUpdate, payload);
        }
        return SortedList.INVALID_POSITION;
    }

    public void clear() {
        clear(true);
    }

    public void clear(boolean animate) {
        int size = collection.size();
        collection.clear();
        if (animate) {
            notifyItemRangeRemoved(0, size);
        } else {
            notifyDataSetChanged();
        }
    }

    public synchronized BaseViewModel remove(int position) {
        return collection.removeItemAt(position);
    }

    public boolean isEmpty() {
        return collection.size() == 0;
    }

    public synchronized void addAll(Collection<? extends BaseViewModel> toAddCollection) {
        collection.addAll(toAddCollection);
    }

    // view holder lifecycle
    @Override
    @CallSuper
    public BaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return onCreateDefaultViewHolder(parent, viewType);
    }

    protected abstract BaseViewHolder onCreateDefaultViewHolder(ViewGroup parent, int viewType);

    @Override
    @CallSuper
    public void onBindViewHolder(BaseViewHolder holder, int position) {
        BaseViewModel item = collection.get(position);
        holder.itemView.setSelected(selectedId.equals(item.getId()));
        holder.onBind(item, getListener());
    }

    protected abstract BaseViewHolder.BaseInteractionListener getListener();

    @Override
    @CallSuper
    public void onBindViewHolder(BaseViewHolder holder, int position, List<Object> payloads) {
        super.onBindViewHolder(holder, position, payloads);
        BaseViewModel item = collection.get(position);
        holder.itemView.setSelected(selectedId.equals(item.getId()));
        holder.onBind(item, getListener(), payloads);
    }

    public void setSelectedId(String newSelectedId) {
        if (isSelectable) {
            String oldSelectedGroup = selectedId;
            selectedId = newSelectedId;
            for (int i = 0; i < getItemCount(); i++) {
                BaseViewModel item = getItem(i);
                if (item.getId().equals(oldSelectedGroup) || item.getId().equals(selectedId)) {
                    notifyItemChanged(i);
                }
            }
        }
    }

    public String getSelectedId() {
        return selectedId;
    }

    public void isSelectable(boolean isSelectable) {
        this.isSelectable = isSelectable;
    }

    public void setSelectable(boolean selectable) {
        isSelectable = selectable;
    }

    @Override
    public void onDetachedFromRecyclerView(RecyclerView recyclerView) {
        this.recyclerView = null;
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        this.recyclerView = recyclerView;
    }

    public void updateAll(Object payload) {
        notifyItemRangeChanged(0, collection.size(), payload);
    }

    public void updateAll() {
        notifyItemRangeChanged(0, collection.size());
    }

    public BaseViewModel getItemById(final String itemId) {
        return Observable.from(collection).filter(new Func1<BaseViewModel, Boolean>() {
            @Override
            public Boolean call(BaseViewModel filter) {
                return filter.getId().equals(itemId);
            }
        }).defaultIfEmpty(null).toBlocking().first();
    }

    public Bundle getBundle() {
        Bundle bundle = new Bundle();
        bundle.putParcelableArray(LIST, collection.getAll());
        bundle.putInt(SIZE, collection.size());
        bundle.putString(SELECTED_ID, selectedId);
        return bundle;
    }

    public void setBundle(Bundle in) {
        if (in != null) {
            selectedId = in.getString(SELECTED_ID);
            if (in.containsKey(LIST)) {
                Parcelable[] list = in.getParcelableArray(LIST);
                if (list instanceof BaseViewModel[]) {
                    collection.setAll((BaseViewModel[]) in.getParcelableArray(LIST), in.getInt(SIZE));
                }
            }
            notifyDataSetChanged();
        }
    }

    public <K extends BaseViewModel> K getFirstItemOfType(Class<K> klass) {
        for (BaseViewModel baseViewModel : collection) {
            if (baseViewModel.getClass().equals(klass)) {
                return (K) baseViewModel;
            }
        }
        return null;
    }
}