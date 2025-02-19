/**
 * Copyright (C) 2015 Red Hat, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.fabric8.kubernetes.client.extension;

import io.fabric8.kubernetes.api.model.DeletionPropagation;
import io.fabric8.kubernetes.client.dsl.Resource;

import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

/**
 * Provides an interface that is usable by the {@link ExtensibleResourceAdapter} that returns
 * a non-specialized value
 */
public interface ExtensibleResource<T> extends Resource<T> {

  @Override
  ExtensibleResource<T> lockResourceVersion(String resourceVersion);

  @Override
  ExtensibleResource<T> cascading(boolean enabled);

  @Override
  ExtensibleResource<T> dryRun(boolean isDryRun);

  @Override
  ExtensibleResource<T> withResourceVersion(String resourceVersion);

  @Override
  ExtensibleResource<T> fromServer();

  @Override
  ExtensibleResource<T> withGracePeriod(long gracePeriodSeconds);

  @Override
  ExtensibleResource<T> withPropagationPolicy(DeletionPropagation propagationPolicy);

  @Override
  ExtensibleResource<T> withWaitRetryBackoff(long initialBackoff, TimeUnit backoffUnit, double backoffMultiplier);

  @Override
  ExtensibleResource<T> withIndexers(Map<String, Function<T, List<String>>> indexers);

  @Override
  ExtensibleResource<T> withLimit(Long limit);

}
