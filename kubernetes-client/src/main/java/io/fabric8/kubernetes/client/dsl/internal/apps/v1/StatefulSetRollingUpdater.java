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
package io.fabric8.kubernetes.client.dsl.internal.apps.v1;

import io.fabric8.kubernetes.api.model.Pod;
import io.fabric8.kubernetes.api.model.PodList;
import io.fabric8.kubernetes.api.model.apps.StatefulSet;
import io.fabric8.kubernetes.api.model.apps.StatefulSetBuilder;
import io.fabric8.kubernetes.api.model.apps.StatefulSetList;
import io.fabric8.kubernetes.client.Client;
import io.fabric8.kubernetes.client.dsl.Operation;
import io.fabric8.kubernetes.client.dsl.PodResource;
import io.fabric8.kubernetes.client.dsl.RollableScalableResource;
import io.fabric8.kubernetes.client.dsl.WatchListDeletable;

class StatefulSetRollingUpdater extends RollingUpdater<StatefulSet, StatefulSetList> {

  StatefulSetRollingUpdater(Client client, String namespace) {
    super(client, namespace);
  }

  StatefulSetRollingUpdater(Client client, String namespace, long rollingTimeoutMillis, long loggingIntervalMillis) {
    super(client, namespace, rollingTimeoutMillis, loggingIntervalMillis);
  }

  @Override
  protected StatefulSet createClone(StatefulSet obj, String newName, String newDeploymentHash) {
    return new StatefulSetBuilder(obj)
        .editMetadata()
        .withResourceVersion(null)
        .withName(newName)
        .endMetadata()
        .editSpec()
        .withReplicas(0)
        .editSelector().addToMatchLabels(DEPLOYMENT_KEY, newDeploymentHash).endSelector()
        .editTemplate().editMetadata().addToLabels(DEPLOYMENT_KEY, newDeploymentHash).endMetadata().endTemplate()
        .endSpec()
        .build();
  }

  @Override
  protected WatchListDeletable<Pod, PodList, PodResource> selectedPodLister(StatefulSet obj) {
    return selectedPodLister(obj.getSpec().getSelector());
  }

  @Override
  protected StatefulSet updateDeploymentKey(String name, String hash) {
    StatefulSet old = resources().inNamespace(namespace).withName(name).get();
    StatefulSet updated = new StatefulSetBuilder(old).editSpec()
        .editSelector().addToMatchLabels(DEPLOYMENT_KEY, hash).endSelector()
        .editTemplate().editMetadata().addToLabels(DEPLOYMENT_KEY, hash).endMetadata().endTemplate()
        .endSpec()
        .build();
    return resources().inNamespace(namespace).withName(name).replace(updated);
  }

  @Override
  protected StatefulSet removeDeploymentKey(String name) {
    StatefulSet old = resources().inNamespace(namespace).withName(name).get();
    StatefulSet updated = new StatefulSetBuilder(old)
        .editSpec()
        .editSelector().removeFromMatchLabels(DEPLOYMENT_KEY).endSelector()
        .editTemplate().editMetadata().removeFromLabels(DEPLOYMENT_KEY).endMetadata().endTemplate()
        .endSpec()
        .build();
    return resources().inNamespace(namespace).withName(name).replace(updated);
  }

  @Override
  protected int getReplicas(StatefulSet obj) {
    return obj.getSpec().getReplicas();
  }

  @Override
  protected StatefulSet setReplicas(StatefulSet obj, int replicas) {
    return new StatefulSetBuilder(obj).editSpec().withReplicas(replicas).endSpec().build();
  }

  @Override
  protected Operation<StatefulSet, StatefulSetList, RollableScalableResource<StatefulSet>> resources() {
    return new StatefulSetOperationsImpl(this.client);
  }
}
