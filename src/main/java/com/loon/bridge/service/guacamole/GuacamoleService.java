package com.loon.bridge.service.guacamole;

import java.util.Arrays;

import javax.websocket.server.ServerEndpointConfig;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.loon.bridge.service.auditvideo.AuditVideoService;
import com.loon.bridge.service.file.FileService;
import com.loon.bridge.service.remotecpe.NatService;

/**
 * git https://github.com/fatedier/frp
 * 
 *
 * @author nbflow
 */
@Service
public class GuacamoleService implements InitializingBean {

    @Value("${guac.guac.hostname}")
    private String GUAC_HOSTNAME;

    @Value("${guac.guac.port}")
    private Integer GUAC_PORT;

    @Autowired
    private LoonServerEndpointExporter serverEndpointExporter;

    @Autowired
    private NatService natService;

    @Autowired
    private FileService fileService;

    @Autowired
    private AuditVideoService auditVideoService;

    @Override
    public void afterPropertiesSet() throws Exception {
        ServerEndpointConfig config = ServerEndpointConfig.Builder.create(LoonGuacamoleWebSocketTunnelEndpoint.class, "/websocket-tunnel").subprotocols(Arrays.asList(new String[]{"guacamole"})).build();
        serverEndpointExporter.getServerContainer().addEndpoint(config);
        LoonGuacamoleWebSocketTunnelEndpoint.GUAC_HOSTNAME = GUAC_HOSTNAME;
        LoonGuacamoleWebSocketTunnelEndpoint.GUAC_PORT = GUAC_PORT;
        LoonGuacamoleWebSocketTunnelEndpoint.natService = natService;
        LoonGuacamoleWebSocketTunnelEndpoint.fileService = fileService;
        LoonGuacamoleWebSocketTunnelEndpoint.auditVideoService = auditVideoService;
        LoonGuacamoleWebSocketTunnelEndpoint.init();
    }

}
