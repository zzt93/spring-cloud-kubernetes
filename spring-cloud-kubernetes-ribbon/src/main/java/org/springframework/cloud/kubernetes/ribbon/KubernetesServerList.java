/*
 * Copyright 2013-2018 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package org.springframework.cloud.kubernetes.ribbon;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.netflix.client.config.IClientConfig;
import com.netflix.loadbalancer.AbstractServerList;
import com.netflix.loadbalancer.Server;
import com.netflix.loadbalancer.ServerList;
import io.fabric8.kubernetes.api.model.*;
import io.fabric8.kubernetes.client.KubernetesClient;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class KubernetesServerList extends AbstractServerList<Server>
		implements ServerList<Server> {

	private static final int FIRST = 0;
	private static final Log LOG = LogFactory.getLog(KubernetesServerList.class);

	private final KubernetesClient client;

	private String serviceId;

	private String namespace;
	private String portName;

	public KubernetesServerList(KubernetesClient client) {
		this.client = client;
	}

	public void initWithNiwsConfig(IClientConfig clientConfig) {
		this.serviceId = clientConfig.getClientName();
		this.namespace = clientConfig.getPropertyAsString(KubernetesConfigKey.Namespace,
				client.getNamespace());
		this.portName = clientConfig.getPropertyAsString(KubernetesConfigKey.PortName,
				null);
	}

	public List<Server> getInitialListOfServers() {
		return Collections.emptyList();
	}

	public List<Server> getUpdatedListOfServers() {
		PodList pods = namespace != null
				? client.pods().inNamespace(namespace).withLabel("app", serviceId).list()
				: client.pods().withLabel("app", serviceId).list();

		List<Server> result = new ArrayList<>();
		if (pods != null) {
			if (LOG.isDebugEnabled()) {
				LOG.debug("Found [" + pods.getItems().size()
						+ "] pods in namespace [" + namespace + "] for name ["
						+ serviceId + "] and portName [" + portName + "]");
			}

			for (Pod pod : pods.getItems()) {
				ObjectMeta metadata = pod.getMetadata();
				result.add(new KubernetesServer(pod.getStatus().getPodIP(), pod.getSpec().getContainers().get(0).getPorts().get(0).getContainerPort()).setLabels(metadata.getLabels()));
			}
		}
		else {
			LOG.warn("Did not find any pods in ribbon in namespace [" + namespace
					+ "] for name [" + serviceId + "] and portName [" + portName + "]");
		}
		return result;
	}
}
