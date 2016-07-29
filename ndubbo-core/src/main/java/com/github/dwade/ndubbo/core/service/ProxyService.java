package com.github.dwade.ndubbo.core.service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.Watcher.Event.EventType;
import org.apache.zookeeper.ZooDefs.Ids;
import org.apache.zookeeper.ZooKeeper;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import com.github.dwade.ndubbo.core.zk.ZookeeperServer;

public class ProxyService<T> implements FactoryBean<T>, InitializingBean, ApplicationContextAware {
	
	private static ApplicationContext applicationContext;
	
	private String interfaceName;
	
	private String ref;
	
	private Class<?> interfaceClass;
	
	private T target;
	
	private ZookeeperServer zkServer;
	
	List<String> addressList = new ArrayList<String>();

	@Override
	public T getObject() throws Exception {
		return (T) target;
	}

	@Override
	public Class<?> getObjectType() {
		return interfaceClass;
	}

	@Override
	public boolean isSingleton() {
		return true;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void afterPropertiesSet() throws Exception {
		interfaceClass = Class.forName(interfaceName);
		target = (T) applicationContext.getBean(ref);
		zkServer = applicationContext.getBean(ZookeeperServer.class);
		ZooKeeper zk = new ZooKeeper(zkServer.getConnectString(), 2000, null);
		String path = "/" + interfaceName;
		if (zk.exists(path, new Watcher() {

			@Override
			public void process(WatchedEvent event) {
				if(EventType.NodeDataChanged.equals(event.getType())) {
					try {
						byte[] datas = zk.getData(path, false, null);
						ObjectInputStream in = new ObjectInputStream(new ByteArrayInputStream(datas));
						addressList = (List<String>) in.readObject();
						addressList.stream().forEach(str->System.out.println(str));
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		}) == null) {
			zk.create(path, null, Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
		} else {
			byte[] datas = zk.getData(path, false, null);
			ObjectInputStream in = new ObjectInputStream(new ByteArrayInputStream(datas));
			addressList = (List<String>) in.readObject();
		}
		addressList.add(InetAddress.getLocalHost().getHostAddress());
		ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
		ObjectOutputStream out = new ObjectOutputStream(byteStream);
		out.writeObject(addressList);
		zk.setData(path, byteStream.toByteArray(), -1);
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		ProxyService.applicationContext = applicationContext;
	}

	public String getInterfaceName() {
		return interfaceName;
	}

	public void setInterfaceName(String interfaceName) {
		this.interfaceName = interfaceName;
	}

	public String getRef() {
		return ref;
	}

	public void setRef(String ref) {
		this.ref = ref;
	}

}
