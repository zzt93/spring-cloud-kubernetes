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

package org.springframework.cloud.kubernetes.registry;

import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.kubernetes.client.KubernetesClientException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.cloud.client.serviceregistry.ServiceRegistry;
import org.springframework.cloud.kubernetes.discovery.KubernetesDiscoveryProperties;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class KubernetesServiceRegistry implements ServiceRegistry<KubernetesRegistration> {

	private static final Log log = LogFactory.getLog(KubernetesServiceRegistry.class);
	private final KubernetesClient client;
	private final KubernetesDiscoveryProperties properties;

	public KubernetesServiceRegistry(KubernetesClient client, KubernetesDiscoveryProperties properties) {
		this.client = client;
		this.properties = properties;
	}

	@Override
	public void register(KubernetesRegistration registration) {
		log.info("Registering : " + registration);

		if (!properties.getServiceLabels().isEmpty()) {
			try {
				client.pods().withName(InetAddress.getLocalHost().getHostName())
					.edit()
					.editMetadata().addToLabels(properties.getServiceLabels())
					.and().done();
			} catch (KubernetesClientException|UnknownHostException e) {
				log.warn("Unable to add labels to pod", e);
			}
		}
	}

	@Override
	public void deregister(KubernetesRegistration registration) {
		log.info("DeRegistering : " + registration);
	}

	@Override
	public void close() {

	}

	@Override
	public void setStatus(KubernetesRegistration registration,
						  String status) {
		log.info("Set Status for : " + registration + " Status: " + status);

	}

	@Override
	public <T> T getStatus(KubernetesRegistration registration) {
		log.info("Get Status for : " + registration );
		return null;
	}
}
