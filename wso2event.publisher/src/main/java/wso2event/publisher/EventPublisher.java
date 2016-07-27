package wso2event.publisher;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.HashMap;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.PropertyConfigurator;
import org.wso2.carbon.analytics.datasource.commons.Record;
import org.wso2.carbon.analytics.datasource.core.util.GenericUtils;
import org.wso2.carbon.databridge.agent.AgentHolder;
import org.wso2.carbon.databridge.agent.DataPublisher;
import org.wso2.carbon.databridge.commons.Event;
import org.wso2.carbon.databridge.commons.utils.DataBridgeCommonsUtils;

public class EventPublisher {

    private static final int defaultThriftPort = 7611;
    static int sleep = 0;
    static String host = "localhost";
    
    public static void main(String[] args) throws Exception {
        if (args.length > 0) {
            host = args[0];
            if (args.length > 1) {
                sleep = Integer.parseInt(args[1]);
            }
        }
        String log4jConfPath = "./src/main/resources/log4j.properties";
        PropertyConfigurator.configure(log4jConfPath);
        String currentDir = System.getProperty("user.dir");
        System.setProperty("javax.net.ssl.trustStore", currentDir + "/src/main/resources/client-truststore.jks");
        System.setProperty("javax.net.ssl.trustStorePassword", "wso2carbon");
        AgentHolder.setConfigPath(getDataAgentConfigPath());
        
        System.out.println("Data Agent path: " + getDataAgentConfigPath());
        System.out.println(System.getProperty("javax.net.ssl.trustStorePassword"));

        String type = "Thrift";
        int receiverPort = defaultThriftPort;
        int securePort = receiverPort + 100;
        String url = "tcp://" + host + ":" + receiverPort;
        String authURL = "ssl://" + host + ":" + securePort;
        String username = "admin";
        String password = "admin";
        
        DataPublisher dataPublisher = new DataPublisher(type, url, authURL, username, password);
        String streamId = DataBridgeCommonsUtils.generateStreamId("STREAMING_DATA", "1.0.0");
        publishEvents(dataPublisher, streamId);
    }

    public static String getDataAgentConfigPath() {
        File filePath = new File("src/main/resources");
        return filePath.getAbsolutePath() + File.separator + "data-agent-conf.xml";
    }

        
    private static void publishEvents(DataPublisher dataPublisher, String streamId) throws Exception {
        String[] lines = IOUtils.toString(new FileInputStream("/home/supun/Desktop/STREAMING_DATA.csv")).split("\n");
        for (String line : lines) {
            String[] fields = line.split(",");
            Object[] metaData = new Object[]{fields[0]};
            Object[] payloadData = new Object[7];
            
            payloadData[0] = Integer.parseInt(fields[1]);
            payloadData[1] = Long.parseLong(fields[2]);
            payloadData[2] = Float.parseFloat(fields[3]);
            payloadData[3] = fields[4];
            payloadData[4] = Integer.parseInt(fields[5]);
            payloadData[5] = Integer.parseInt(fields[6]);
            payloadData[6] = Integer.parseInt(fields[7]);
            
            Event event = new Event(streamId, System.currentTimeMillis(), metaData, null, payloadData);
            dataPublisher.publish(event);
        }
        dataPublisher.shutdown();
    }
}