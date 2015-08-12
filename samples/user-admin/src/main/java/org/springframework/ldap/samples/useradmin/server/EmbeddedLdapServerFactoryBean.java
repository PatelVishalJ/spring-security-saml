package org.springframework.ldap.samples.useradmin.server;

import org.springframework.beans.factory.config.AbstractFactoryBean;

public class EmbeddedLdapServerFactoryBean extends AbstractFactoryBean<EmbeddedLdapServer> {
	private int port;
	private String partitionName;
	private String partitionSuffix;

	public EmbeddedLdapServerFactoryBean() {}

	@Override
	public Class<?> getObjectType()
	{
	  return EmbeddedLdapServer.class;
	}

	public void setPartitionName(String partitionName) {
	  this.partitionName = partitionName;
	}

	public void setPartitionSuffix(String partitionSuffix) {
	  this.partitionSuffix = partitionSuffix;
	}

	public void setPort(int port) {
	  this.port = port;
	}

	@Override
	protected EmbeddedLdapServer createInstance() throws Exception
	{
	  return EmbeddedLdapServer.newEmbeddedServer(this.partitionName, this.partitionSuffix, this.port);
	}

	protected void destroyInstance(EmbeddedLdapServer instance) throws Exception
	{
	  instance.shutdown();
	}
}
