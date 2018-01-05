package de.claudioaltamura.jmx.simple;

import static de.claudioaltamura.jmx.simple.SystemConfigManagement.HOST;
import static de.claudioaltamura.jmx.simple.SystemConfigManagement.PORT;

import java.io.IOException;

import javax.management.MBeanServerConnection;
import javax.management.MBeanServerInvocationHandler;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;

public class SystemConfigClient {

	public static void main(String[] args) throws IOException, MalformedObjectNameException {
		JMXServiceURL url = new JMXServiceURL("service:jmx:rmi:///jndi/rmi://" + HOST + ":" + PORT + "/jmxrmi");

		try (JMXConnector jmxConnector = JMXConnectorFactory.connect(url)) {
			MBeanServerConnection mbeanServerConnection = jmxConnector.getMBeanServerConnection();
			// ObjectName should be same as your MBean name
			ObjectName mbeanName = new ObjectName(HOST, "de.claudioaltamura.jmx.simple", "SystemConfig");

			// Get MBean proxy instance that will be used to make calls to
			// registered MBean
			SystemConfigMBean mbeanProxy = MBeanServerInvocationHandler
					.newProxyInstance(mbeanServerConnection, mbeanName, SystemConfigMBean.class, true);

			// let's make some calls to mbean through proxy and see the results.
			System.out.println("Current SystemConfig::" + mbeanProxy.doConfig());

			mbeanProxy.setSchemaName("NewSchema");
			mbeanProxy.setThreadCount(5);

			System.out.println("New SystemConfig::" + mbeanProxy.doConfig());

			// let's terminate the mbean by making thread count as 0
			mbeanProxy.setThreadCount(0);
		}
	}

}