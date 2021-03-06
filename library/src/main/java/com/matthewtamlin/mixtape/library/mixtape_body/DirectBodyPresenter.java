/*
 * Copyright 2017 Matthew Tamlin
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.matthewtamlin.mixtape.library.mixtape_body;

import android.view.MenuItem;

import com.matthewtamlin.java_utilities.testing.Tested;
import com.matthewtamlin.mixtape.library.base_mvp.BaseDataSource;
import com.matthewtamlin.mixtape.library.base_mvp.BasePresenter;
import com.matthewtamlin.mixtape.library.base_mvp.ListDataSource;
import com.matthewtamlin.mixtape.library.data.LibraryItem;

import java.util.List;

/**
 * A simple implementation of the BodyContract.Presenter interface where the list returned by the
 * data source is directly reflected in the view. Listeners can be registered to handle user input
 * events.
 *
 * @param <D>
 * 		the type of data to present
 * @param <S>
 * 		the type of data source to present from
 * @param <V>
 * 		the type of view to present to
 */
@Tested(testMethod = "automated")
public class DirectBodyPresenter<
		D extends LibraryItem,
		S extends ListDataSource<D>,
		V extends BodyView>
		implements BasePresenter<S, V>, ListDataSource.FullListener<D>, BodyView.FullListener {
	/**
	 * The data source to present from.
	 */
	private S dataSource;

	/**
	 * The view to present to.
	 */
	private V view;

	@Override
	public void setDataSource(final S dataSource) {
		unsubscribeFromDataSourceCallbacks(this.dataSource);
		this.dataSource = dataSource;
		subscribeToDataSourceCallbacks(this.dataSource);

		if (dataSource != null) {
			dataSource.loadData(false, this);
		} else if (view != null) {
			view.setItems(null);
		}
	}

	@Override
	public S getDataSource() {
		return dataSource;
	}

	@Override
	public void setView(final V view) {
		unregisterFromViewCallbacks(this.view);
		this.view = view;
		registerForViewCallbacks(this.view);

		if (dataSource != null) {
			dataSource.loadData(false, this);
		}
	}

	@Override
	public V getView() {
		return view;
	}

	@Override
	public void onDataLoaded(final BaseDataSource<List<D>> source, final List<D> data) {
		if (view != null) {
			view.setItems(data);
		}
	}

	@Override
	public void onLoadDataFailed(final BaseDataSource source) {
		if (view != null) {
			view.setItems(null);
		}
	}

	@Override
	public void onDataReplaced(final BaseDataSource<List<D>> source, final List<D> oldData,
			final List<D> newData) {
		if (view != null) {
			view.setItems(newData);
		}
	}

	@Override
	public void onDataModified(final BaseDataSource<List<D>> source, final List<D> data) {
		if (view != null) {
			view.notifyItemsChanged();
		}
	}

	@Override
	public void onLongOperationStarted(final BaseDataSource<List<D>> source) {
		if (view != null) {
			view.showLoadingIndicator(true);
		}
	}

	@Override
	public void onLongOperationFinished(final BaseDataSource<List<D>> source) {
		if (view != null) {
			view.showLoadingIndicator(false);
		}
	}

	@Override
	public void onDataAdded(final ListDataSource<D> source, final D added, final int index) {
		if (view != null) {
			view.notifyItemAdded(index);
		}
	}

	@Override
	public void onDataRemoved(final ListDataSource<D> source, final D removed, final int index) {
		if (view != null) {
			view.notifyItemRemoved(index);
		}
	}

	@Override
	public void onItemModified(final ListDataSource<D> source, final D modified, final int index) {
		if (view != null) {
			view.notifyItemModified(index);
		}
	}

	@Override
	public void onDataMoved(final ListDataSource<D> source, final D moved, final int initialIndex,
			final int finalIndex) {
		if (view != null) {
			view.notifyItemMoved(initialIndex, finalIndex);
		}
	}

	@Override
	public void onLibraryItemSelected(final BodyView bodyView, final LibraryItem item) {
		// Do nothing
	}

	@Override
	public void onContextualMenuItemSelected(final BodyView bodyView, final LibraryItem libraryItem,
			final MenuItem menuItem) {
		// Do nothing
	}

	/**
	 * Unsubscribes this presenter from all callbacks delivered by the supplied data source.
	 *
	 * @param dataSource
	 * 		the data source to unsubscribe from, may be null
	 */
	protected void unsubscribeFromDataSourceCallbacks(final S dataSource) {
		if (dataSource != null) {
			dataSource.unregisterDataReplacedListener(this);
			dataSource.unregisterDataModifiedListener(this);
			dataSource.unregisterLongOperationListener(this);
			dataSource.unregisterItemAddedListener(this);
			dataSource.unregisterItemRemovedListener(this);
			dataSource.unregisterItemMovedListener(this);
			dataSource.unregisterItemModifiedListener(this);
		}
	}

	/**
	 * Subscribes this presenter to all callbacks delivered by the supplied source callbacks.
	 *
	 * @param dataSource
	 * 		the data source to subscribe to, may be null
	 */
	protected void subscribeToDataSourceCallbacks(final S dataSource) {
		if (dataSource != null) {
			dataSource.registerDataReplacedListener(this);
			dataSource.registerDataModifiedListener(this);
			dataSource.registerLongOperationListener(this);
			dataSource.registerItemAddedListener(this);
			dataSource.registerItemRemovedListener(this);
			dataSource.registerItemModifiedListener(this);
			dataSource.registerItemMovedListener(this);
		}
	}

	/**
	 * Removes the presenter of the supplied view.
	 *
	 * @param view
	 * 		the view to modify, may be null
	 */
	protected void unregisterFromViewCallbacks(final V view) {
		if (view != null) {
			view.removeLibraryItemSelectedListener(this);
			view.removeContextualMenuItemSelectedListener(this);
		}
	}

	/**
	 * Sets this presenter as the supplied view's presenter.
	 *
	 * @param view
	 * 		the view to modify, may be null
	 */
	protected void registerForViewCallbacks(final V view) {
		if (view != null) {
			view.addLibraryItemSelectedListener(this);
			view.addContextualMenuItemSelectedListener(this);
		}
	}
}