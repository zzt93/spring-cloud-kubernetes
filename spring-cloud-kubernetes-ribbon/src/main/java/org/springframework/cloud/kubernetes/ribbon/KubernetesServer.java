package org.springframework.cloud.kubernetes.ribbon;

import com.netflix.loadbalancer.Server;

import java.util.HashMap;
import java.util.Map;

/**
 * @author zzt
 */
public class KubernetesServer extends Server {

	private Map<String, String> labels = new HashMap<>();

	public KubernetesServer(String host, int port) {
		super(host, port);
	}

	public KubernetesServer(String scheme, String host, int port) {
		super(scheme, host, port);
	}

	public KubernetesServer(String id) {
		super(id);
	}

	KubernetesServer setLabels(Map<String, String> labels) {
		this.labels = labels;
		return this;
	}

	public Map<String, String> getLabels() {
		return labels;
	}
}
