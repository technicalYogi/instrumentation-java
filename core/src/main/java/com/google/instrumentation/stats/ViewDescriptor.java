/*
 * Copyright 2016, Google Inc.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *     http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.instrumentation.stats;

import com.google.instrumentation.common.Function;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * A ViewDescriptor specifies an aggregation and a set of tag keys. The aggregation will be broken
 * down by the unique set of matching tag values for each measurement.
 */
public abstract class ViewDescriptor {
  /**
   * Name of view. Must be unique.
   */
  public final String getName() {
    return name;
  }

  /**
   * More detailed description, for documentation purposes.
   */
  public final String getDescription() {
    return description;
  }

  /**
   * Measurement type of this view.
   */
  public final MeasurementDescriptor getMeasurementDescriptor() {
    return measurementDescriptor;
  }

  /**
   * Tag keys to match with the associated {@link MeasurementDescriptor}. If no keys are specified,
   * then all stats are recorded. Keys must be unique.
   *
   * <p>Note: The returned list is unmodifiable, attempts to update it will throw an
   * UnsupportedOperationException.
   */
  public final List<TagKey> getTagKeys() {
    return tagKeys;
  }

 /**
   * Applies the given match function to the underlying data type.
   */
  public abstract <T> T match(
      Function<DistributionViewDescriptor, T> p0,
      Function<IntervalViewDescriptor, T> p1);


  private final String name;
  private final String description;
  private final MeasurementDescriptor measurementDescriptor;
  private final List<TagKey> tagKeys;

  private ViewDescriptor(
      String name,
      String description,
      MeasurementDescriptor measurementDescriptor,
      List<TagKey> tagKeys) {
    this.name = name;
    this.description = description;
    this.measurementDescriptor = measurementDescriptor;
    this.tagKeys = Collections.unmodifiableList(new ArrayList<TagKey>(tagKeys));
  }

  /**
   * A {@link ViewDescriptor} for distribution-base aggregations.
   */
  public static class DistributionViewDescriptor extends ViewDescriptor {
    /**
     * Constructs a new {@link DistributionViewDescriptor}.
     */
    public static DistributionViewDescriptor create(
        String name,
        String description,
        MeasurementDescriptor measurementDescriptor,
        DistributionAggregationDescriptor distributionAggregationDescriptor,
        List<TagKey> tagKeys) {
      return new DistributionViewDescriptor(
          name, description, measurementDescriptor, distributionAggregationDescriptor, tagKeys);
    }

    /**
     * The {@link DistributionAggregationDescriptor} associated with this
     * {@link DistributionViewDescriptor}.
     */
    public DistributionAggregationDescriptor getDistributionAggregationDescriptor() {
      return distributionAggregationDescriptor;
    }

    @Override
    public <T> T match(
        Function<DistributionViewDescriptor, T> p0,
        Function<IntervalViewDescriptor, T> p1) {
      return p0.apply(this);
    }

    private final DistributionAggregationDescriptor distributionAggregationDescriptor;

    private DistributionViewDescriptor(
        String name,
        String description,
        MeasurementDescriptor measurementDescriptor,
        DistributionAggregationDescriptor distributionAggregationDescriptor,
        List<TagKey> tagKeys) {
      super(name, description, measurementDescriptor, tagKeys);
      this.distributionAggregationDescriptor = distributionAggregationDescriptor;
    }
  }

  /**
   * A {@link ViewDescriptor} for interval-based aggregations.
   */
  public static class IntervalViewDescriptor extends ViewDescriptor {
    /**
     * Constructs a new {@link IntervalViewDescriptor}.
     */
    public static IntervalViewDescriptor create(
        String name,
        String description,
        MeasurementDescriptor measurementDescriptor,
        IntervalAggregationDescriptor intervalAggregationDescriptor,
        List<TagKey> tagKeys) {
      return new IntervalViewDescriptor(
          name, description, measurementDescriptor, intervalAggregationDescriptor, tagKeys);
    }

    /**
     * The {@link IntervalAggregationDescriptor} associated with this
     * {@link IntervalViewDescriptor}.
     */
    public IntervalAggregationDescriptor getIntervalAggregationDescriptor() {
      return intervalAggregationDescriptor;
    }

    @Override
    public <T> T match(
        Function<DistributionViewDescriptor, T> p0,
        Function<IntervalViewDescriptor, T> p1) {
      return p1.apply(this);
    }

    private final IntervalAggregationDescriptor intervalAggregationDescriptor;

    private IntervalViewDescriptor(
        String name,
        String description,
        MeasurementDescriptor measurementDescriptor,
        IntervalAggregationDescriptor intervalAggregationDescriptor,
        List<TagKey> tagKeys) {
      super(name, description, measurementDescriptor, tagKeys);
      this.intervalAggregationDescriptor = intervalAggregationDescriptor;
    }
  }
}
