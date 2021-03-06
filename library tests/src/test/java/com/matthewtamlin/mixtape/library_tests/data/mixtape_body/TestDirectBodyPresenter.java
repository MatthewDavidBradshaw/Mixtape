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

package com.matthewtamlin.mixtape.library_tests.data.mixtape_body;

import com.matthewtamlin.mixtape.library.base_mvp.ListDataSource;
import com.matthewtamlin.mixtape.library.data.LibraryItem;
import com.matthewtamlin.mixtape.library.mixtape_body.BodyView;
import com.matthewtamlin.mixtape.library.mixtape_body.DirectBodyPresenter;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

/**
 * Unit tests for the {@link DirectBodyPresenter} class.
 */
@SuppressWarnings("unchecked") // Warning caused by mocks, but it isn't a problem
@RunWith(JUnit4.class)
public class TestDirectBodyPresenter {
	/**
	 * The presenter under test.
	 */
	private DirectBodyPresenter<LibraryItem, ListDataSource<LibraryItem>, BodyView> presenter;

	/**
	 * Initialises the testing objects and assigns them to member variables.
	 */
	@Before
	public void setup() {
		presenter = new DirectBodyPresenter<>();
	}

	/**
	 * Test to verify that the {@link DirectBodyPresenter#setDataSource(ListDataSource)} method
	 * functions correctly when the presenter does not have a view. The test will only pass if the
	 * presenter is registered and unregisters for callbacks, and if the correct calls are made to
	 * load data.
	 */
	@Test
	public void testSetDataSource_withoutView() {
		final ListDataSource<LibraryItem> dataSource1 = mock(ListDataSource.class);
		final ListDataSource<LibraryItem> dataSource2 = mock(ListDataSource.class);

		presenter.setDataSource(dataSource1);

		verify(dataSource1).registerDataReplacedListener(presenter);
		verify(dataSource1).registerDataModifiedListener(presenter);
		verify(dataSource1).registerLongOperationListener(presenter);
		verify(dataSource1).registerItemAddedListener(presenter);
		verify(dataSource1).registerItemRemovedListener(presenter);
		verify(dataSource1).registerItemModifiedListener(presenter);
		verify(dataSource1).registerItemMovedListener(presenter);

		verify(dataSource1).loadData(anyBoolean(), eq(presenter));

		presenter.setDataSource(dataSource2);

		verify(dataSource1).unregisterDataReplacedListener(presenter);
		verify(dataSource1).unregisterDataModifiedListener(presenter);
		verify(dataSource1).unregisterLongOperationListener(presenter);
		verify(dataSource1).unregisterItemAddedListener(presenter);
		verify(dataSource1).unregisterItemRemovedListener(presenter);
		verify(dataSource1).unregisterItemModifiedListener(presenter);
		verify(dataSource1).unregisterItemMovedListener(presenter);

		verify(dataSource2).registerDataReplacedListener(presenter);
		verify(dataSource2).registerDataModifiedListener(presenter);
		verify(dataSource2).registerLongOperationListener(presenter);
		verify(dataSource2).registerItemAddedListener(presenter);
		verify(dataSource2).registerItemRemovedListener(presenter);
		verify(dataSource2).registerItemModifiedListener(presenter);
		verify(dataSource2).registerItemMovedListener(presenter);

		verify(dataSource1).loadData(anyBoolean(), eq(presenter));

		presenter.setDataSource(null);

		verify(dataSource2).unregisterDataReplacedListener(presenter);
		verify(dataSource2).unregisterDataModifiedListener(presenter);
		verify(dataSource2).unregisterLongOperationListener(presenter);
		verify(dataSource2).unregisterItemAddedListener(presenter);
		verify(dataSource2).unregisterItemRemovedListener(presenter);
		verify(dataSource2).unregisterItemModifiedListener(presenter);
		verify(dataSource2).unregisterItemMovedListener(presenter);
	}

	/**
	 * Test to verify that the {@link DirectBodyPresenter#setDataSource(ListDataSource)} method
	 * functions correctly when the presenter has a view. The test will only pass if the view is
	 * updated with the new data when the data source is changed.
	 */
	@Test
	public void testSetDataSource_withView() {
		final BodyView view = mock(BodyView.class);
		presenter.setView(view);

		final ArrayList<LibraryItem> data1 = new ArrayList<>();
		final ListDataSource<LibraryItem> dataSource1 = createNewDataSource(data1);

		final ArrayList<LibraryItem> data2 = new ArrayList<>();
		final ListDataSource<LibraryItem> dataSource2 = createNewDataSource(data2);

		presenter.setDataSource(dataSource1);
		verify(view).setItems(data1);

		presenter.setDataSource(dataSource2);
		verify(view, atLeastOnce()).setItems(data2);

		presenter.setDataSource(null);
		verify(view).setItems(null);
	}

	/**
	 * Test to verify that the {@link DirectBodyPresenter#setView(BodyView)} method functions
	 * correctly when the presenter does not have a data source. The test will only pass if the
	 * presenter registers/unregisters itself with the views.
	 */
	@Test
	public void testSetView_withoutDataSource() {
		final BodyView view1 = mock(BodyView.class);
		final BodyView view2 = mock(BodyView.class);

		presenter.setView(view1);

		verify(view1).addLibraryItemSelectedListener(presenter);
		verify(view1).addContextualMenuItemSelectedListener(presenter);

		presenter.setView(view2);

		verify(view1).removeLibraryItemSelectedListener(presenter);
		verify(view1).removeContextualMenuItemSelectedListener(presenter);
		verify(view2).addLibraryItemSelectedListener(presenter);
		verify(view2).addContextualMenuItemSelectedListener(presenter);

		presenter.setView(null);

		verify(view2).removeLibraryItemSelectedListener(presenter);
		verify(view2).removeContextualMenuItemSelectedListener(presenter);
	}

	/**
	 * Test to verify that the {@link DirectBodyPresenter#setView(BodyView)} method functions
	 * correctly when the presenter has a data source. The test will only pass if the presenter
	 * registers/unregisters itself with the views and loads data into the views.
	 */
	@Test
	public void testSetView_withDataSource() {
		final ArrayList<LibraryItem> data = new ArrayList<>();
		final ListDataSource<LibraryItem> dataSource = createNewDataSource(data);
		presenter.setDataSource(dataSource);

		final BodyView view1 = mock(BodyView.class);
		final BodyView view2 = mock(BodyView.class);

		presenter.setView(view1);

		verify(view1).addLibraryItemSelectedListener(presenter);
		verify(view1).addContextualMenuItemSelectedListener(presenter);
		verify(view1).setItems(data);

		presenter.setView(view2);

		verify(view1).removeLibraryItemSelectedListener(presenter);
		verify(view1).removeContextualMenuItemSelectedListener(presenter);
		verify(view1).setItems(null);
		verify(view2).addLibraryItemSelectedListener(presenter);
		verify(view2).addContextualMenuItemSelectedListener(presenter);
		verify(view2).setItems(data);

		presenter.setView(null);

		verify(view2).removeLibraryItemSelectedListener(presenter);
		verify(view2).removeContextualMenuItemSelectedListener(presenter);
		verify(view2).setItems(null);
	}

	/**
	 * Test to verify that the {@link DirectBodyPresenter} functions correctly when the data source
	 * delivers a data loaded callback and there is no view. The test will only pass if all methods
	 * exit normally.
	 */
	@Test
	public void testOnDataLoaded_withoutView() {
		final List<LibraryItem> data = new ArrayList<>();
		final ListDataSource<LibraryItem> dataSource = createNewDataSource(data);
		presenter.setDataSource(dataSource);

		presenter.onDataLoaded(dataSource, data);
	}

	/**
	 * Test to verify that the {@link DirectBodyPresenter} functions correctly when the data source
	 * delivers a data loaded callback and there is a view. The test will only pass if the data in
	 * the view is updated.
	 */
	@Test
	public void testOnDataLoaded_withView() {
		final List<LibraryItem> data = new ArrayList<>();
		final ListDataSource<LibraryItem> dataSource = createNewDataSource(data);
		presenter.setDataSource(dataSource);

		final BodyView view = mock(BodyView.class);
		presenter.setView(view);

		verify(view, times(1)).setItems(data);

		presenter.onDataLoaded(dataSource, data);

		verify(view, times(2)).setItems(data);
	}

	/**
	 * Test to verify that the {@link DirectBodyPresenter} functions correctly when the data source
	 * delivers a data load failed callback and there is no view. The test will only pass if all
	 * methods exit normally.
	 */
	@Test
	public void testOnLoadDataFailed_withoutView() {
		final List<LibraryItem> data = new ArrayList<>();
		final ListDataSource<LibraryItem> dataSource = createNewDataSource(data);
		presenter.setDataSource(dataSource);

		presenter.onLoadDataFailed(dataSource);
	}

	/**
	 * Test to verify that the {@link DirectBodyPresenter} functions correctly when the data source
	 * delivers a data load failed callback and there is a view. The test will only pass if the view
	 * is updated to display no data.
	 */
	@Test
	public void testOnLoadDataFailed_withView() {
		final List<LibraryItem> data = new ArrayList<>();
		final ListDataSource<LibraryItem> dataSource = createNewDataSource(data);
		presenter.setDataSource(dataSource);

		final BodyView view = mock(BodyView.class);
		presenter.setView(view);

		verify(view, never()).setItems(null);

		presenter.onLoadDataFailed(dataSource);

		verify(view, times(1)).setItems(null);
	}

	/**
	 * Test to verify that the {@link DirectBodyPresenter} functions correctly when the data source
	 * delivers a data replaced callback and there is no view. The test will only pass if all
	 * methods exit normally.
	 */
	@Test
	public void testOnDataReplaced_withoutView() {
		final List<LibraryItem> data = new ArrayList<>();
		final ListDataSource<LibraryItem> dataSource = createNewDataSource(data);
		presenter.setDataSource(dataSource);

		presenter.onDataReplaced(dataSource, mock(List.class), mock(List.class));
	}

	/**
	 * Test to verify that the {@link DirectBodyPresenter} functions correctly when the data source
	 * delivers a data replaced callback and there is a view. The test will only pass if the view is
	 * updated to display the new data.
	 */
	@Test
	public void testOnDataReplaced_withView() {
		final List<LibraryItem> originalData = new ArrayList<>();
		final SettableListDataSource dataSource = createNewDataSource(originalData);
		presenter.setDataSource(dataSource);

		final BodyView view = mock(BodyView.class);
		presenter.setView(view);

		final ArrayList<LibraryItem> newData = new ArrayList<>();
		dataSource.setData(newData);
		presenter.onDataReplaced(dataSource, originalData, newData);

		verify(view, atLeastOnce()).setItems(newData);
	}

	/**
	 * Test to verify that the {@link DirectBodyPresenter} functions correctly when the data source
	 * delivers a data modified callback and there is no view. The test will only pass if all
	 * methods exit normally.
	 */
	@Test
	public void testOnDataModified_withoutView() {
		final List<LibraryItem> data = new ArrayList<>();
		final ListDataSource<LibraryItem> dataSource = createNewDataSource(data);
		presenter.setDataSource(dataSource);

		presenter.onDataModified(dataSource, data);
	}

	/**
	 * Test to verify that the {@link DirectBodyPresenter} functions correctly when the data source
	 * delivers a data modified callback and there is a view. The test will only pass if the view is
	 * notified of the event.
	 */
	@Test
	public void testOnDataModified_withView() {
		final List<LibraryItem> data = new ArrayList<>();
		final ListDataSource<LibraryItem> dataSource = createNewDataSource(data);
		presenter.setDataSource(dataSource);

		final BodyView view = mock(BodyView.class);
		presenter.setView(view);

		verify(view, never()).notifyItemsChanged();

		presenter.onDataModified(dataSource, data);

		verify(view, times(1)).notifyItemsChanged();
	}

	/**
	 * Test to verify that the {@link DirectBodyPresenter} functions correctly when the data source
	 * delivers a long operation started callback and there is no view. The test will only pass if
	 * all methods exit normally.
	 */
	@Test
	public void testOnLongOperationStarted_withoutView() {
		final List<LibraryItem> data = new ArrayList<>();
		final ListDataSource<LibraryItem> dataSource = createNewDataSource(data);
		presenter.setDataSource(dataSource);

		presenter.onLongOperationStarted(dataSource);
	}

	/**
	 * Test to verify that the {@link DirectBodyPresenter} functions correctly when the data source
	 * delivers a long operation started callback and there is a view. The test will only pass if
	 * the loading indicator is shown.
	 */
	@Test
	public void testOnLongOperationStarted_withView() {
		final List<LibraryItem> data = new ArrayList<>();
		final ListDataSource<LibraryItem> dataSource = createNewDataSource(data);
		presenter.setDataSource(dataSource);

		final BodyView view = mock(BodyView.class);
		presenter.setView(view);

		verify(view, never()).showLoadingIndicator(true);

		presenter.onLongOperationStarted(dataSource);

		verify(view).showLoadingIndicator(true);
		verify(view, never()).showLoadingIndicator(false);
	}

	/**
	 * Test to verify that the {@link DirectBodyPresenter} functions correctly when the data source
	 * delivers a long operation finished callback and there is no view. The test will only pass if
	 * all methods exit normally.
	 */
	@Test
	public void testOnLongOperationFinished_withoutView() {
		final List<LibraryItem> data = new ArrayList<>();
		final ListDataSource<LibraryItem> dataSource = createNewDataSource(data);
		presenter.setDataSource(dataSource);

		presenter.onLongOperationFinished(dataSource);
	}

	/**
	 * Test to verify that the {@link DirectBodyPresenter} functions correctly when the data source
	 * delivers a long operation finished callback and there is a view. The test will only pass if
	 * the loading indicator is hidden.
	 */
	@Test
	public void testOnLongOperationFinished_withView() {
		final List<LibraryItem> data = new ArrayList<>();
		final ListDataSource<LibraryItem> dataSource = createNewDataSource(data);
		presenter.setDataSource(dataSource);

		final BodyView view = mock(BodyView.class);
		presenter.setView(view);

		verify(view, never()).showLoadingIndicator(false);

		presenter.onLongOperationFinished(dataSource);

		verify(view).showLoadingIndicator(false);
		verify(view, never()).showLoadingIndicator(true);
	}

	/**
	 * Test to verify that the {@link DirectBodyPresenter} functions correctly when the data source
	 * delivers a data added callback and there is no view. The test will only pass if all methods
	 * exit normally.
	 */
	@Test
	public void testOnDataAdded_withoutView() {
		final List<LibraryItem> data = new ArrayList<>();
		final ListDataSource<LibraryItem> dataSource = createNewDataSource(data);
		presenter.setDataSource(dataSource);

		presenter.onDataAdded(dataSource, mock(LibraryItem.class), 0);
	}

	/**
	 * Test to verify that the {@link DirectBodyPresenter} functions correctly when the data source
	 * delivers a data added callback and there is a view. The test will only pass if the view is
	 * notified of the event.
	 */
	@Test
	public void testOnDataAdded_withView() {
		final List<LibraryItem> data = new ArrayList<>();
		final ListDataSource<LibraryItem> dataSource = createNewDataSource(data);
		presenter.setDataSource(dataSource);

		final BodyView view = mock(BodyView.class);
		presenter.setView(view);

		verify(view, never()).notifyItemAdded(anyInt());

		presenter.onDataAdded(dataSource, mock(LibraryItem.class), 1);

		verify(view, times(1)).notifyItemAdded(1);
	}

	/**
	 * Test to verify that the {@link DirectBodyPresenter} functions correctly when the data source
	 * delivers a data removed callback and there is no view. The test will only pass if all methods
	 * exit normally.
	 */
	@Test
	public void testOnDataRemoved_withoutView() {
		final List<LibraryItem> data = new ArrayList<>();
		final ListDataSource<LibraryItem> dataSource = createNewDataSource(data);
		presenter.setDataSource(dataSource);

		presenter.onDataRemoved(dataSource, mock(LibraryItem.class), 0);
	}

	/**
	 * Test to verify that the {@link DirectBodyPresenter} functions correctly when the data source
	 * delivers a data removed callback and there is a view. The test will only pass if the view is
	 * notified of the event.
	 */
	@Test
	public void testOnDataRemoved_withView() {
		final List<LibraryItem> data = new ArrayList<>();
		final ListDataSource<LibraryItem> dataSource = createNewDataSource(data);
		presenter.setDataSource(dataSource);

		final BodyView view = mock(BodyView.class);
		presenter.setView(view);

		verify(view, never()).notifyItemRemoved(anyInt());

		presenter.onDataAdded(dataSource, mock(LibraryItem.class), 1);

		verify(view, never()).notifyItemRemoved(1);
	}

	/**
	 * Test to verify that the {@link DirectBodyPresenter} functions correctly when the data source
	 * delivers a list item modified callback and there is no view. The test will only pass if all
	 * methods exit normally.
	 */
	@Test
	public void testOnItemModified_withoutView() {
		final List<LibraryItem> data = new ArrayList<>();
		final ListDataSource<LibraryItem> dataSource = createNewDataSource(data);
		presenter.setDataSource(dataSource);

		presenter.onItemModified(dataSource, mock(LibraryItem.class), 1);
	}

	/**
	 * Test to verify that the {@link DirectBodyPresenter} functions correctly when the data source
	 * delivers a list item modified callback and there is a view. The test will only pass if the
	 * view is notified of the event.
	 */
	@Test
	public void testOnItemModified_withView() {
		final List<LibraryItem> data = new ArrayList<>();
		final ListDataSource<LibraryItem> dataSource = createNewDataSource(data);
		presenter.setDataSource(dataSource);

		final BodyView view = mock(BodyView.class);
		presenter.setView(view);

		verify(view, never()).notifyItemModified(anyInt());

		presenter.onItemModified(dataSource, mock(LibraryItem.class), 1);

		verify(view, times(1)).notifyItemModified(1);
	}

	/**
	 * Test to verify that the {@link DirectBodyPresenter} functions correctly when the data source
	 * delivers a data moved callback and there is no view. The test will only pass if all methods
	 * exit normally.
	 */
	@Test
	public void testOnDataMoved_withoutView() {
		final List<LibraryItem> data = new ArrayList<>();
		final ListDataSource<LibraryItem> dataSource = createNewDataSource(data);
		presenter.setDataSource(dataSource);

		presenter.onDataMoved(dataSource, mock(LibraryItem.class), 1, 2);
	}

	/**
	 * Test to verify that the {@link DirectBodyPresenter} functions correctly when the data source
	 * delivers a data moved callback and there is a view. The test will only pass if the view is
	 * notified of the event.
	 */
	@Test
	public void testOnDataMoved_withView() {
		final List<LibraryItem> data = new ArrayList<>();
		final ListDataSource<LibraryItem> dataSource = createNewDataSource(data);
		presenter.setDataSource(dataSource);

		final BodyView view = mock(BodyView.class);
		presenter.setView(view);

		verify(view, never()).notifyItemMoved(anyInt(), anyInt());

		presenter.onDataMoved(dataSource, mock(LibraryItem.class), 1, 2);

		verify(view, times(1)).notifyItemMoved(1, 2);
	}

	/**
	 * Test to verify that the {@link DirectBodyPresenter} function correctly when the view delivers
	 * a library item selected callback and there are no registered callback listeners. The test
	 * will only pass if all methods exit normally.
	 */
	@Test
	public void testOnLibraryItemSelected_noCallbacks() {
		presenter.onLibraryItemSelected(mock(BodyView.class), mock(LibraryItem.class));
	}

	/**
	 * Creates a new SettableListDataSource with the supplied items as the data.
	 *
	 * @param items
	 * 		the data returned by the source
	 * @return the new data source
	 */
	private SettableListDataSource createNewDataSource(final List<LibraryItem> items) {
		final SettableListDataSource dataSource = new SettableListDataSource();
		dataSource.setData(items);
		return dataSource;
	}
}