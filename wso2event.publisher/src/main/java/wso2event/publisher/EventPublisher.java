package wso2event.publisher;

import java.io.File;

import org.wso2.carbon.databridge.agent.AgentHolder;
import org.wso2.carbon.databridge.agent.DataPublisher;
import org.wso2.carbon.databridge.commons.Event;
import org.wso2.carbon.databridge.commons.utils.DataBridgeCommonsUtils;

public class EventPublisher {

    private static final int defaultThriftPort = 7611;
    static int sleep = 0;
    static String host = "localhost";
    //static String host = "192.168.57.151";
    
    public static void main(String[] args) throws Exception {
        if (args.length > 0) {
            host = args[0];
            if (args.length > 1) {
                sleep = Integer.parseInt(args[1]);
            }
        }
        System.out.println("Starting Data Publisher");
        String currentDir = System.getProperty("user.dir");
        System.setProperty("javax.net.ssl.trustStore", currentDir + "/src/main/resources/client-truststore.jks");
        System.setProperty("javax.net.ssl.trustStorePassword", "wso2carbon");

        AgentHolder.setConfigPath(getDataAgentConfigPath());
        
        System.out.println("Data Agent path: " + getDataAgentConfigPath());
        System.out.println("Client truststore: " + System.getProperty("javax.net.ssl.trustStore"));

        String type = "Thrift";
        int receiverPort = defaultThriftPort;
        int securePort = receiverPort + 100;

        String url = "tcp://" + host + ":" + receiverPort;
        String authURL = "ssl://" + host + ":" + securePort;
        String username = "admin";
        String password = "admin";
        DataPublisher dataPublisher = new DataPublisher(type, url, authURL, username, password);
        String streamId = DataBridgeCommonsUtils.generateStreamId("esb-flow-entry-stream", "1.0.0");
        publishEvents(dataPublisher, streamId);
    }

    public static String getDataAgentConfigPath() {
        File filePath = new File("src/main/resources");
        return filePath.getAbsolutePath() + File.separator + "data-agent-conf.xml";
    }

        
    private static void publishEvents(DataPublisher dataPublisher, String streamId) throws Exception {
        int count = 0;
        Object[] metaData = { false };
        long startTime;
        long endTime;
        long messageId = 0;
        long a = System.currentTimeMillis();
        while (true) {
            startTime = System.currentTimeMillis();
            endTime = startTime + 100;
            messageId++;
            count++;
            String flowDataFull = "{\"messageFlowId\":\"" + messageId + "\",\"payloads\":[{\"payload\":\"<?xmlversion='1.0'encoding='utf-8'?><soapenv:Envelopexmlns:soapenv='http://schemas.xmlsoap.org/soap/envelope/'><soapenv:Body/></soapenv:Envelope>\",\"events\":[{\"eventIndex\":0,\"attribute\":\"afterPayload\"},{\"eventIndex\":1,\"attribute\":\"beforePayload\"},{\"eventIndex\":1,\"attribute\":\"afterPayload\"}]},{\"payload\":\"<?xmlversion='1.0'encoding='utf-8'?><soapenv:Envelopexmlns:soapenv='http://schemas.xmlsoap.org/soap/envelope/'><soapenv:Body/></soapenv:Envelope>\",\"events\":[{\"eventIndex\":5,\"attribute\":\"afterPayload\"},{\"eventIndex\":6,\"attribute\":\"beforePayload\"},{\"eventIndex\":6,\"attribute\":\"afterPayload\"},{\"eventIndex\":7,\"attribute\":\"beforePayload\"},{\"eventIndex\":7,\"attribute\":\"afterPayload\"}]},{\"payload\":\"<?xmlversion='1.0'encoding='utf-8'?><soapenv:Envelopexmlns:soapenv='http://schemas.xmlsoap.org/soap/envelope/'><soapenv:Body/></soapenv:Envelope>\",\"events\":[{\"eventIndex\":0,\"attribute\":\"beforePayload\"}]},{\"payload\":\"<?xmlversion='1.0'encoding='utf-8'?><soapenv:Envelopexmlns:soapenv='http://schemas.xmlsoap.org/soap/envelope/'><soapenv:Body/></soapenv:Envelope>\",\"events\":[{\"eventIndex\":2,\"attribute\":\"beforePayload\"}]},{\"payload\":\"<?xmlversion='1.0'encoding='utf-8'?><soapenv:Envelopexmlns:soapenv='http://schemas.xmlsoap.org/soap/envelope/'><soapenv:Body/></soapenv:Envelope>\",\"events\":[{\"eventIndex\":2,\"attribute\":\"afterPayload\"},{\"eventIndex\":3,\"attribute\":\"beforePayload\"},{\"eventIndex\":3,\"attribute\":\"afterPayload\"}]},{\"payload\":\"<?xmlversion='1.0'encoding='utf-8'?><soapenv:Envelopexmlns:soapenv='http://schemas.xmlsoap.org/soap/envelope/'><soapenv:Body/></soapenv:Envelope>\",\"events\":[{\"eventIndex\":4,\"attribute\":\"beforePayload\"}]},{\"payload\":\"<?xmlversion='1.0'encoding='utf-8'?><soapenv:Envelopexmlns:soapenv='http://schemas.xmlsoap.org/soap/envelope/'><soapenv:Body/></soapenv:Envelope>\",\"events\":[{\"eventIndex\":4,\"attribute\":\"afterPayload\"}]},{\"payload\":\"<?xmlversion='1.0'encoding='utf-8'?><soapenv:Envelopexmlns:soapenv='http://schemas.xmlsoap.org/soap/envelope/'><soapenv:Body/></soapenv:Envelope>\",\"events\":[{\"eventIndex\":5,\"attribute\":\"beforePayload\"}]}],\"host\":\"192.168.57.153:9763\",\"events\":[{\"componentType\":\"Proxy Service\",\"componentName\":\"TestProxy_2\",\"componentId\":\"TestProxy_2@0:TestProxy_2\",\"startTime\":"+startTime+",\"endTime\":"+endTime+",\"duration\":25,\"beforePayload\":null,\"afterPayload\":null,\"contextPropertyMap\":{\"mediation.flow.statistics.collected\":true,\"mediation.flow.trace.collected\":true,\"mediation.flow.statistics.statistic.id\":\"" + messageId + "\",\"mediation.flow.statistics.parent.index\":0,\"mediation.flow.statistics.index.object\":\"org.apache.synapse.aspects.flow.statistics.util.UniqueIdentifierObject@4749b823\"},\"transportPropertyMap\":{\"ContentType\":\"application/xml\",\"Connection\":\"keep-alive\",\"User-Agent\":\"Apache-HttpClient/4.2.6(java1.5)\",\"Host\":\"192.168.57.153:8280\",\"To\":\"/services/TestProxy_2\",\"MessageID\":"+messageId+"},\"children\":[1],\"entryPoint\":\"TestProxy_2\",\"entryPointHashcode\":-1123256409,\"faultCount\":0,\"hashCode\":-1123256409},{\"componentType\":\"Sequence\",\"componentName\":\"PROXY_INSEQ\",\"componentId\":\"TestProxy_2@1:PROXY_INSEQ\",\"startTime\":"+startTime+",\"endTime\":"+endTime+",\"duration\":11,\"beforePayload\":null,\"afterPayload\":null,\"contextPropertyMap\":null,\"transportPropertyMap\":null,\"children\":[2],\"entryPoint\":\"TestProxy_2\",\"entryPointHashcode\":-1123256409,\"faultCount\":0,\"hashCode\":-1123256409},{\"componentType\":\"Mediator\",\"componentName\":\"LogMediator\",\"componentId\":\"TestProxy_2@2:LogMediator\",\"startTime\":"+startTime+",\"endTime\":"+endTime+",\"duration\":6,\"beforePayload\":null,\"afterPayload\":null,\"contextPropertyMap\":{\"mediation.flow.statistics.collected\":true,\"mediation.flow.trace.collected\":true,\"mediation.flow.statistics.statistic.id\":\"" + messageId + "\",\"TRANSPORT_IN_NAME\":\"http\",\"proxy.name\":\"TestProxy_2\",\"mediation.flow.statistics.parent.index\":2,\"mediation.flow.statistics.index.object\":\"org.apache.synapse.aspects.flow.statistics.util.UniqueIdentifierObject@4749b823\",\"tenant.info.domain\":\"carbon.super\",\"tenant.info.id\":-1234},\"transportPropertyMap\":{\"ContentType\":\"application/xml\",\"Connection\":\"keep-alive\",\"User-Agent\":\"Apache-HttpClient/4.2.6(java1.5)\",\"Host\":\"192.168.57.153:8280\",\"To\":\"/services/TestProxy_2\",\"MessageID\":"+messageId+"},\"children\":[3],\"entryPoint\":\"TestProxy_2\",\"entryPointHashcode\":-1123256409,\"faultCount\":0,\"hashCode\":-1123256409},{\"componentType\":\"Mediator\",\"componentName\":\"LogMediator\",\"componentId\":\"TestProxy_2@3:LogMediator\",\"startTime\":"+startTime+",\"endTime\":"+endTime+",\"duration\":0,\"beforePayload\":null,\"afterPayload\":null,\"contextPropertyMap\":{\"mediation.flow.statistics.collected\":true,\"mediation.flow.trace.collected\":true,\"mediation.flow.statistics.statistic.id\":\"" + messageId + "\",\"TRANSPORT_IN_NAME\":\"http\",\"proxy.name\":\"TestProxy_2\",\"mediation.flow.statistics.parent.index\":3,\"mediation.flow.statistics.index.object\":\"org.apache.synapse.aspects.flow.statistics.util.UniqueIdentifierObject@4749b823\",\"tenant.info.domain\":\"carbon.super\",\"tenant.info.id\":-1234},\"transportPropertyMap\":{\"ContentType\":\"application/xml\",\"Connection\":\"keep-alive\",\"User-Agent\":\"Apache-HttpClient/4.2.6(java1.5)\",\"Host\":\"192.168.57.153:8280\",\"To\":\"/services/TestProxy_2\",\"MessageID\":"+messageId+"},\"children\":[4],\"entryPoint\":\"TestProxy_2\",\"entryPointHashcode\":-1123256409,\"faultCount\":0,\"hashCode\":-1123256409},{\"componentType\":\"Sequence\",\"componentName\":\"TestSequence_1\",\"componentId\":\"TestSequence_1@0:TestSequence_1\",\"startTime\":"+startTime+",\"endTime\":"+endTime+",\"duration\":1,\"beforePayload\":null,\"afterPayload\":null,\"contextPropertyMap\":{\"mediation.flow.statistics.collected\":true,\"mediation.flow.trace.collected\":true,\"mediation.flow.statistics.statistic.id\":\"" + messageId + "\",\"TRANSPORT_IN_NAME\":\"http\",\"proxy.name\":\"TestProxy_2\",\"mediation.flow.statistics.parent.index\":4,\"mediation.flow.statistics.index.object\":\"org.apache.synapse.aspects.flow.statistics.util.UniqueIdentifierObject@4749b823\",\"tenant.info.domain\":\"carbon.super\",\"tenant.info.id\":-1234},\"transportPropertyMap\":{\"ContentType\":\"application/xml\",\"Connection\":\"keep-alive\",\"User-Agent\":\"Apache-HttpClient/4.2.6(java1.5)\",\"Host\":\"192.168.57.153:8280\",\"To\":\"/services/TestProxy_2\",\"MessageID\":"+messageId+"},\"children\":[5],\"entryPoint\":\"TestProxy_2\",\"entryPointHashcode\":-1123256409,\"faultCount\":0,\"hashCode\":1326438244},{\"componentType\":\"Mediator\",\"componentName\":\"LogMediator\",\"componentId\":\"TestSequence_1@1:LogMediator\",\"startTime\":"+startTime+",\"endTime\":"+endTime+",\"duration\":1,\"beforePayload\":null,\"afterPayload\":null,\"contextPropertyMap\":{\"mediation.flow.statistics.collected\":true,\"mediation.flow.trace.collected\":true,\"mediation.flow.statistics.statistic.id\":\"" + messageId + "\",\"TRANSPORT_IN_NAME\":\"http\",\"proxy.name\":\"TestProxy_2\",\"mediation.flow.statistics.parent.index\":5,\"mediation.flow.statistics.index.object\":\"org.apache.synapse.aspects.flow.statistics.util.UniqueIdentifierObject@4749b823\",\"tenant.info.domain\":\"carbon.super\",\"tenant.info.id\":-1234},\"transportPropertyMap\":{\"ContentType\":\"application/xml\",\"Connection\":\"keep-alive\",\"User-Agent\":\"Apache-HttpClient/4.2.6(java1.5)\",\"Host\":\"192.168.57.153:8280\",\"To\":\"/services/TestProxy_2\",\"MessageID\":"+messageId+"},\"children\":[6],\"entryPoint\":\"TestProxy_2\",\"entryPointHashcode\":-1123256409,\"faultCount\":0,\"hashCode\":1326438244},{\"componentType\":\"Mediator\",\"componentName\":\"PropertyMediator:clone1\",\"componentId\":\"TestSequence_1@2:PropertyMediator:clone1\",\"startTime\":"+startTime+",\"endTime\":"+endTime+",\"duration\":0,\"beforePayload\":null,\"afterPayload\":null,\"contextPropertyMap\":{\"mediation.flow.statistics.collected\":true,\"mediation.flow.trace.collected\":true,\"mediation.flow.statistics.statistic.id\":\"" + messageId + "\",\"TRANSPORT_IN_NAME\":\"http\",\"proxy.name\":\"TestProxy_2\",\"mediation.flow.statistics.parent.index\":6,\"mediation.flow.statistics.index.object\":\"org.apache.synapse.aspects.flow.statistics.util.UniqueIdentifierObject@4749b823\",\"tenant.info.domain\":\"carbon.super\",\"tenant.info.id\":-1234},\"transportPropertyMap\":{\"ContentType\":\"application/xml\",\"Connection\":\"keep-alive\",\"User-Agent\":\"Apache-HttpClient/4.2.6(java1.5)\",\"Host\":\"192.168.57.153:8280\",\"To\":\"/services/TestProxy_2\",\"MessageID\":"+messageId+"},\"children\":[7],\"entryPoint\":\"TestProxy_2\",\"entryPointHashcode\":-1123256409,\"faultCount\":0,\"hashCode\":1326438244},{\"componentType\":\"Mediator\",\"componentName\":\"DropMediator\",\"componentId\":\"TestSequence_1@3:DropMediator\",\"startTime\":"+startTime+",\"endTime\":"+endTime+",\"duration\":0,\"beforePayload\":null,\"afterPayload\":null,\"contextPropertyMap\":{\"mediation.flow.statistics.collected\":true,\"mediation.flow.trace.collected\":true,\"clone1\":\"complete\",\"mediation.flow.statistics.statistic.id\":\"" + messageId + "\",\"TRANSPORT_IN_NAME\":\"http\",\"proxy.name\":\"TestProxy_2\",\"mediation.flow.statistics.parent.index\":7,\"mediation.flow.statistics.index.object\":\"org.apache.synapse.aspects.flow.statistics.util.UniqueIdentifierObject@4749b823\",\"tenant.info.domain\":\"carbon.super\",\"tenant.info.id\":-1234},\"transportPropertyMap\":{\"ContentType\":\"application/xml\",\"Connection\":\"keep-alive\",\"User-Agent\":\"Apache-HttpClient/4.2.6(java1.5)\",\"Host\":\"192.168.57.153:8280\",\"To\":\"/services/TestProxy_2\",\"MessageID\":"+messageId+"},\"children\":null,\"entryPoint\":\"TestProxy_2\",\"entryPointHashcode\":-1123256409,\"faultCount\":0,\"hashCode\":1326438244}]}";
            String[] payloadData = { "" + messageId, flowDataFull };
            Event event = new Event(streamId, System.currentTimeMillis(), metaData, null, payloadData);
            long duration = startTime-a;
            if (duration != 0 && duration%1000 == 0) {
                a = System.currentTimeMillis();
                System.out.println("TPS: " + count*1.0/duration*1000);
                count = 0;
            }
            //System.out.println("sending event..");
            dataPublisher.tryPublish(event);
            Thread.sleep(1);
        }
    }
}