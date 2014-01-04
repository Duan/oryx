/*
 * Copyright (c) 2013, Cloudera, Inc. All Rights Reserved.
 *
 * Cloudera, Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"). You may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * This software is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
 * CONDITIONS OF ANY KIND, either express or implied. See the License for
 * the specific language governing permissions and limitations under the
 * License.
 */

package com.cloudera.oryx.als.common.candidate;

import java.util.Collection;
import java.util.Iterator;

import com.cloudera.oryx.common.collection.LongObjectMap;

/**
 * <p>Implementations of this interface speed up the recommendation process by pre-selecting a set of items
 * that are the only possible candidates for recommendation, or, are the most likely to be top recommendations.</p>
 * 
 * <p>For example, an implementation might know that most items in the model, while useful as data, 
 * are not recommendable since they are old -- out of date, or at least, undesirable enough as recommendations 
 * to be excluded. It could pre-compute which items are eligible and </p>
 * 
 * <p>This is a form of filtering, but, differs from the filtering provided by 
 * {@link com.cloudera.oryx.als.common.rescorer.RescorerProvider}. That is a run-time, per-request filter;
 * this class represents a more global, precomputed filtering that is not parameterized by the request.</p>
 *
 * <p><em>This is quite a low-level API and should be considered "advanced" usage; use a
 * {@link com.cloudera.oryx.als.common.rescorer.RescorerProvider} unless it's clear that it is not fast
 * enough.</em></p>
 *
 * <p>Implementations should define a constructor that accepts a parameter of type {@link LongObjectMap}.
 * This is a reference to the "Y" matrix in the model -- item-feature matrix.
 * Access to Y is protected by a lock, but, the implementation can assume that it is locked for
 * reading (not writing) during the constructor call, and is locked for reading (not writing) during
 * a call to {@link #getCandidateIterator(float[][])} and while the result of that method is used.
 * So, implementations may save and use a reference to Y if it is only used in the context of these
 * methods and only for reading.</p>
 * 
 * @author Sean Owen
 * @see com.cloudera.oryx.als.common.rescorer.RescorerProvider
 */
public interface CandidateFilter {
  
  // Note that your implementation will need a constructor matching the following, which is how it
  // gets a reference to the set of items:
  
  // public YourCandidateFilter(FastByIDMap<float[]> Y) {
  //   ...
  // }

  /**
   * @param userVectors user feature vector(s) for which recommendations are being made. This may or may not
   *  influence which items are returned. Use {@link com.cloudera.oryx.als.common.StringLongMapping#toLong(String)}
   *  to find the numeric internal ID for a given string ID.
   * @return a set of items most likely to be a good recommendation for the given users. These are returned
   *  as item ID / vector pairs ({@link LongObjectMap}'s {@code MapEntry}). They are returned as an {@link Iterator},
   *  and not just one, but potentially many. If several are returned, then the caller can process the
   *  {@link Iterator}s in parallel for speed.
   */
  Collection<Iterator<LongObjectMap.MapEntry<float[]>>> getCandidateIterator(float[][] userVectors);

  /**
   * Note a new item has appeared at run-time.
   *
   * @param itemID ID of new item
   */
  void addItem(String itemID);

}
