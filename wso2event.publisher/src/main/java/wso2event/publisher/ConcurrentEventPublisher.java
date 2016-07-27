/*
 * Copyright (c) 2016, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package wso2event.publisher;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.zip.GZIPOutputStream;

import javax.xml.bind.DatatypeConverter;

import org.apache.log4j.PropertyConfigurator;
import org.wso2.carbon.analytics.spark.core.util.AnalyticsConstants;
import org.wso2.carbon.analytics.spark.core.util.PublishingPayload;
import org.wso2.carbon.databridge.agent.AgentHolder;
import org.wso2.carbon.databridge.agent.DataPublisher;
import org.wso2.carbon.databridge.agent.exception.DataEndpointException;
import org.wso2.carbon.databridge.commons.Event;
import org.wso2.carbon.databridge.commons.utils.DataBridgeCommonsUtils;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Output;

/**
 * arguments:
 * <host> <port> <thredCount> <mediatorCount> <payloadsEnabled> <multitenantEnabled> <confDirectory>
 *
 */
public class ConcurrentEventPublisher implements Runnable{
    private static int receiverPort = 7612;
    private DataPublisher dataPublisher;
    private String url;
    private int threadNumber;
    private static String host = "localhost";
    private static int threadCount = 2;
    private static boolean withPayloads = true;
    private static String confDirectory = "src/main/resources";
    private static int mediatorCount = 30;
    private static int tenantCount = 1;
    private int tenantId = -1234;
//    private static String host = "192.168.1.20";
    
    public ConcurrentEventPublisher(int threadNumber, String type, String url, String authURL, String username, String password, int tenantId) throws Exception {
        this.dataPublisher = new DataPublisher(type, url, null, username, password);
        this.url = url;
        this.threadNumber = threadNumber;
        this.tenantId = tenantId;
    }

    public void run() {
        System.out.println("Starting Data Publisher: " + this.threadNumber);
        System.out.println("Publishing to: " + url);
        String streamId = DataBridgeCommonsUtils.generateStreamId("esb-flow-entry-stream", "1.0.0");
        try {
            publishEvents(this.dataPublisher, streamId, threadNumber);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                this.dataPublisher.shutdown();
            } catch (DataEndpointException e) {
                e.printStackTrace();
            }
        }
    }
    
    public static void main(String[] args) throws Exception {
        String type = "Thrift";
        if (args.length > 0) {
            host = args[0];
            receiverPort = Integer.parseInt(args[1]);
            threadCount = Integer.parseInt(args[2]);
            mediatorCount = Integer.parseInt(args[3]);
            withPayloads = Boolean.parseBoolean(args[4]);
            tenantCount = Integer.parseInt(args[5]);
            if (args.length > 6) {
                confDirectory =  args[6];
            }
        }
        
        int securePort = receiverPort + 100;
        String url = "tcp://" + host + ":" + receiverPort;
        String authURL = "ssl://" + host + ":" + securePort;
        
        String log4jConfPath = confDirectory + "/log4j.properties";
        PropertyConfigurator.configure(log4jConfPath);
        String currentDir = System.getProperty("user.dir");
        System.setProperty("javax.net.ssl.trustStore", currentDir + "/" + confDirectory + "/client-truststore.jks");
        System.setProperty("javax.net.ssl.trustStorePassword", "wso2carbon");
        AgentHolder.setConfigPath(getDataAgentConfigPath());
        
        System.out.println("Data Agent path: " + getDataAgentConfigPath());
        System.out.println(System.getProperty("javax.net.ssl.trustStore"));
        int currentTenant = 0;
        for (int threadNumber = 0; threadNumber < threadCount; threadNumber++) {
            String username = "admin";
            String password = "admin";
            if (tenantCount > 1) {
                if (currentTenant > tenantCount) {
                    currentTenant =  1;
                } else {
                    currentTenant++;
                }
            } else {
                currentTenant = -1234;
            }
            Runnable eventPublisher = new ConcurrentEventPublisher(threadNumber, type, url, authURL, username, password, currentTenant);
            Thread thread = new Thread(eventPublisher);
            thread.start();
        }
    }

    public static String getDataAgentConfigPath() {
        File filePath = new File(confDirectory);
        return filePath.getAbsolutePath() + File.separator + "data-agent-conf.xml";
    }

        
    private void publishEvents(DataPublisher dataPublisher, String streamId, int threadNumber) throws Exception {
        System.out.println("Starting Data Publisher");
        long count = 0;
        Object[] metaData = { true, tenantId};
        long startTime;
        long a = System.currentTimeMillis();
        Event event;
        
        int total = 0;
        while (true) {
            String[] payloadData = new String[2];
            /******************** Creating Event Object ***************************************/
            Map <String, Object> flowMap = new HashMap <String, Object>();
            ArrayList<List<Object>> eventsList = new ArrayList<List<Object>>();
            ArrayList<PublishingPayload> payloadsList = new ArrayList<PublishingPayload>();
            Map<Integer, List<Integer>> eventList  = new HashMap<Integer, List<Integer>>();
            final String  messageFlowId = UUID.randomUUID().toString() + System.currentTimeMillis() + "_" + total;
            for (int i = 0 ; i < mediatorCount ; i++) {
                List<Object> singleEvent = new ArrayList<Object>();
                //messageID
                singleEvent.add("urn_uuid_" + messageFlowId);
                //component Type
                if (i == 0){
                    singleEvent.add("Proxy Service");
                } else {
                    singleEvent.add("Mediator");
                }
                //Component Name
                singleEvent.add("compName" + i);
                //component Index
                singleEvent.add(i);
                //component ID
                singleEvent.add("compId" + i);
                long start = System.currentTimeMillis();
                //startTime
                singleEvent.add(start);
                long end = System.currentTimeMillis() + 15;
                //endTime
                singleEvent.add(end );
                //duration
                singleEvent.add(end - start);
                //beforepayload
                singleEvent.add(null);
                //afterpayload
                singleEvent.add(null);
                
                //transport/context properties
               if (withPayloads) {
                   if (i%2 == 0) {
                       singleEvent.add("{mediation.flow.statistics.parent.index=4, tenant.info.domain=carbon.super, mediation.flow.statistics.statistic.id=urn_uuid_ac554691-c1ad-4bfe-ad34-8536ac7d1fdb, mediation.flow.statistics.index.object=org.apache.synapse.aspects.flow.statistics.util.UniqueIdentifierObject@7a26fe97, tenant.info.id=-1234, mediation.flow.trace.collected=true, CREDIT_CARD=bbbbb, TRANSPORT_IN_NAME=http, proxy.name=LicenseServiceProxy, mediation.flow.statistics.collected=true, VEHICLE_ID=aaaaa}");
                       singleEvent.add("{Transfer-Encoding=chunked, Host=localhost.localdomain:8282, MessageID=urn:uuid:ac554691-c1ad-4bfe-ad34-8536ac7d1fdb, To=/services/LicenseServiceProxy.LicenseServiceProxyHttpSoap12Endpoint, SOAPAction=urn:renewLicense, WSAction=urn:renewLicense, User-Agent=Axis2, Content-Type=application/soap+xml; charset=UTF-8; action=\"urn:renewLicense\"}");
                   } else {
                       singleEvent.add(null);
                       singleEvent.add(null);
                   }
               } else {
                   singleEvent.add(null);
                   singleEvent.add(null);
               }
                    
                //children
                singleEvent.add(Arrays.toString(new int[]{i+1}));
                //entrypoint
                singleEvent.add("LicenseServiceProxy");
                //entrypoint hashcode
                singleEvent.add("1241186573");
                //faultcount
                singleEvent.add(0);
                //hascode
                singleEvent.add("1241186573" + i);
                
                eventsList.add(singleEvent);
                List<Integer> attributeIndices = new ArrayList<Integer>();
                attributeIndices.add(8);
                attributeIndices.add(9);
                eventList.put(i , attributeIndices);
            }
            PublishingPayload pp = new PublishingPayload();
            pp.setEvents(eventList);
            pp.setPayload("<?xml version='1.0' encoding='utf-8'?><soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:sam=\"http://sample.esb.org\"><soapenv:Body><sam:renewLicense><sam:vehicleNumber>aaaaa</sam:vehicleNumber><sam:insurancePolicy>-2081631303</sam:insurancePolicy><sam:ecoCert>311989168</sam:ecoCert></sam:renewLicense></soapenv:Body></soapenv:Envelope>");
            
            if (withPayloads) {
                payloadsList.add(pp);
            }
            
            flowMap.put("host", "localhost");
            flowMap.put(AnalyticsConstants.EVENTS_ATTRIBUTE, eventsList);
            flowMap.put(AnalyticsConstants.PAYLOADS_ATTRIBUTE, payloadsList);
            
            /***************** kryo serializing ******************/
            Kryo kryo = new Kryo();
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            Output output = new Output(out);
            kryo.register(HashMap.class, 111);
            kryo.register(ArrayList.class, 222);
            kryo.register(PublishingPayload.class, 333);
            kryo.writeObject(output, flowMap);
            output.flush();
            
            
            /****************** gzip ***********************/
            ByteArrayOutputStream gzipOut = new ByteArrayOutputStream();
            GZIPOutputStream gzip = new GZIPOutputStream(gzipOut);
            gzip.write(out.toByteArray());
            gzip.close();
            String str = DatatypeConverter.printBase64Binary(gzipOut.toByteArray());
            
            
            /************ Publishing ****************************/
            startTime = System.currentTimeMillis();
            count++;
            payloadData[0] = "" + messageFlowId;
            payloadData[1] = str;
            event = new Event(streamId, System.currentTimeMillis(), metaData, null, payloadData);
            long duration = startTime-a;
            if (duration != 0 && duration%1000 == 0) {
                a = System.currentTimeMillis();
                System.out.println("Thread: " + this.threadNumber + " TPS: " + count*1.0/duration*1000 + " | Total: " + total);
                count = 0;
            }
            dataPublisher.publish(event);
            total++;
//            Thread.sleep(3); 
            if (total == 50000) {
                break;
            }
        }
    }
}
