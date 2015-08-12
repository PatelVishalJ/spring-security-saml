package org.springframework.ldap.samples.useradmin.server;

import java.io.File;

import org.apache.commons.io.FileUtils;
import org.apache.directory.server.core.DefaultDirectoryService;
import org.apache.directory.server.core.DirectoryService;
import org.apache.directory.server.core.entry.ServerEntry;
import org.apache.directory.server.core.partition.impl.btree.jdbm.JdbmPartition;
import org.apache.directory.server.ldap.LdapServer;
import org.apache.directory.server.protocol.shared.transport.TcpTransport;
import org.apache.directory.shared.ldap.name.LdapDN;

public class EmbeddedLdapServer {
    private final DirectoryService directoryService;
    private final LdapServer ldapServer;
    private static File workingDirectory;

    private EmbeddedLdapServer(DirectoryService directoryService,
                               LdapServer ldapServer) {
        this.directoryService = directoryService;
        this.ldapServer = ldapServer;
    }

    public static EmbeddedLdapServer newEmbeddedServer(String defaultPartitionName, String defaultPartitionSuffix, int port)
            throws Exception{
        workingDirectory = new File(System.getProperty("java.io.tmpdir") + "/apacheds-test1");
        //FileUtils.deleteDirectory(workingDirectory);

        DefaultDirectoryService directoryService = new DefaultDirectoryService();
        directoryService.setShutdownHookEnabled(true);
        directoryService.setAllowAnonymousAccess(true);

        directoryService.setWorkingDirectory(workingDirectory);
        directoryService.getChangeLog().setEnabled( false );

        JdbmPartition partition = new JdbmPartition();
        partition.setId(defaultPartitionName);
        partition.setSuffix(defaultPartitionSuffix);
        directoryService.addPartition(partition);

        directoryService.startup();

        // Inject the apache root entry if it does not already exist
        if ( !directoryService.getAdminSession().exists( partition.getSuffixDn() ) )
        {
            ServerEntry entry = directoryService.newEntry(new LdapDN(defaultPartitionSuffix));
            entry.add("objectClass", "top", "domain", "extensibleObject");
            entry.add("dc", defaultPartitionName);
            directoryService.getAdminSession().add( entry );
        }

        LdapServer ldapServer = new LdapServer();
        ldapServer.setDirectoryService(directoryService);

        TcpTransport ldapTransport = new TcpTransport(port);
        ldapServer.setTransports( ldapTransport );
        ldapServer.start();

        return new EmbeddedLdapServer(directoryService, ldapServer);
    }

    public void shutdown() throws Exception {
        ldapServer.stop();
        directoryService.shutdown();
        //FileUtils.deleteDirectory(workingDirectory);
    }
}
