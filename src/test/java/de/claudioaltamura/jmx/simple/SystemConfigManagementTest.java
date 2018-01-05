package de.claudioaltamura.jmx.simple;

import static de.claudioaltamura.jmx.simple.SystemConfigManagement.HOST;
import static de.claudioaltamura.jmx.simple.SystemConfigManagement.PORT;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.IOException;
import java.lang.management.ManagementFactory;

import javax.management.InstanceAlreadyExistsException;
import javax.management.InstanceNotFoundException;
import javax.management.MBeanRegistrationException;
import javax.management.MBeanServer;
import javax.management.MBeanServerConnection;
import javax.management.MBeanServerInvocationHandler;
import javax.management.MalformedObjectNameException;
import javax.management.NotCompliantMBeanException;
import javax.management.ObjectName;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class SystemConfigManagementTest {

	private static final int DEFAULT_NO_THREADS = 10;
	private static final String DEFAULT_SCHEMA = "default";

	private MBeanServer mbs;
	private ObjectName objectName;

	@Before
	public void setUp() throws MalformedObjectNameException, InstanceAlreadyExistsException, MBeanRegistrationException,
			NotCompliantMBeanException {
		// Get the MBean server
		mbs = ManagementFactory.getPlatformMBeanServer();
		// register the MBean
		SystemConfig mBean = new SystemConfig(DEFAULT_NO_THREADS, DEFAULT_SCHEMA);
		objectName = new ObjectName("localhost", "de.claudioaltamura.jmx.simple", "SystemConfig");
		mbs.registerMBean(mBean, objectName);
	}

	@After
	public void tearDown() throws MBeanRegistrationException, InstanceNotFoundException {
		if (mbs != null && mbs.isRegistered(objectName)) {
			mbs.unregisterMBean(objectName);
		}
	}

	@Test
	public void test() throws IOException, MalformedObjectNameException {

		JMXServiceURL url = new JMXServiceURL("service:jmx:rmi:///jndi/rmi://" + HOST + ":" + PORT + "/jmxrmi");

		try (JMXConnector jmxConnector = JMXConnectorFactory.connect(url)) {
			MBeanServerConnection mbeanServerConnection = jmxConnector.getMBeanServerConnection();
			// ObjectName should be same as your MBean name
			ObjectName mbeanName = new ObjectName(HOST, "de.claudioaltamura.jmx.simple", "SystemConfig");

			// Get MBean proxy instance that will be used to make calls to
			// registered MBean
			SystemConfigMBean mbeanProxy = (SystemConfigMBean) MBeanServerInvocationHandler
					.newProxyInstance(mbeanServerConnection, mbeanName, SystemConfigMBean.class, true);

			// let's make some calls to mbean through proxy and see the results.
			System.out.println("Current SystemConfig::" + mbeanProxy.doConfig());

			assertNotNull(mbeanProxy);
			assertEquals(10, mbeanProxy.getThreadCount());
		}
	}

}
