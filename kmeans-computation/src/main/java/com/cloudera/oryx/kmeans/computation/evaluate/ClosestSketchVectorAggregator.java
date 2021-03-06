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

package com.cloudera.oryx.kmeans.computation.evaluate;

import com.cloudera.oryx.kmeans.computation.cluster.ClusterSettings;
import com.google.common.collect.ImmutableList;
import org.apache.crunch.fn.Aggregators;

public final class ClosestSketchVectorAggregator extends Aggregators.SimpleAggregator<ClosestSketchVectorData> {

  private ClosestSketchVectorData data;
  private final int numFolds;
  private final int numCenters;

  public ClosestSketchVectorAggregator(ClusterSettings settings) {
    this.numFolds = settings.getCrossFolds();
    this.numCenters = settings.getTotalPoints();
  }

  @Override
  public void reset() {
    data = new ClosestSketchVectorData(numFolds, numCenters);
  }

  @Override
  public void update(ClosestSketchVectorData value) {
    data.update(value);
  }

  @Override
  public Iterable<ClosestSketchVectorData> results() {
    return ImmutableList.of(data);
  }
}
