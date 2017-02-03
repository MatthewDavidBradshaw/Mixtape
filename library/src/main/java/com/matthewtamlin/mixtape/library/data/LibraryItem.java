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

package com.matthewtamlin.mixtape.library.data;

import android.graphics.Bitmap;

/**
 * A piece of content in a media library which can be displayed by showing a title, subtitle and
 * artwork. An item can be a single unit of media (such as a song), or it can be a collection of
 * other media items (such as an album). A LibraryItem does not need to store its title, subtitle
 * and artwork in memory, it only needs to provide access to the values when queried. As such, it is
 * perfectly acceptable for the getters to start long running operations to retrieve data when
 * called.
 */
public interface LibraryItem {
	/**
	 * Accesses the title of this LibraryItem. This method may be long running.
	 *
	 * @return the title of this item, null if none exists
	 * @throws LibraryReadException
	 * 		if the title cannot be accessed
	 */
	CharSequence getTitle() throws LibraryReadException;

	/**
	 * Accesses the subtitle of this LibraryItem. This method may be long running.
	 *
	 * @return the subtitle of this item, null if none exists
	 * @throws LibraryReadException
	 * 		if the subtitle cannot be accessed
	 */
	CharSequence getSubtitle() throws LibraryReadException;

	/**
	 * Accesses the artwork of this item. This method may be long running. The width and height
	 * parameters are used to optimise the memory used by the artwork. If either value is less than
	 * or equal to zero, then the unoptimised artwork is returned.
	 *
	 * @param width
	 * 		the desired width of the artwork, measured in pixels, greater than zero
	 * @param height
	 * 		the desired height of the artwork, measured in pixels, greater than zero
	 * @return the artwork of this item, null if none exists
	 * @throws LibraryReadException
	 * 		if the artwork cannot be accessed
	 */
	Bitmap getArtwork(int width, int height) throws LibraryReadException;
}