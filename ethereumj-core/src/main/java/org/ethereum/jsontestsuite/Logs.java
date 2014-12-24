package org.ethereum.jsontestsuite;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.ethereum.vm.DataWord;
import org.ethereum.vm.LogInfo;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.spongycastle.util.encoders.Hex;

public class Logs {
	List<LogInfo> logs = new ArrayList<>();
	
	public Logs(JSONArray jLogs) {

        for (int i = 0; i < jLogs.size(); ++i){

            JSONObject jLog = (JSONObject)jLogs.get(i);
            byte[] address = Hex.decode((String)jLog.get("address"));
            byte[] data =    Hex.decode(((String)jLog.get("data")).substring(2));

            List<DataWord> topics = new ArrayList<>();

            JSONArray jTopics = (JSONArray)jLog.get("topics");
            for(Object t: jTopics.toArray()) {
                byte[] topic = Hex.decode(((String)t));
                topics.add(new DataWord(topic));
            }

            LogInfo li = new LogInfo(address, topics, data);
            logs.add(li);
        }
	}


    public Iterator<LogInfo> getIterator(){
        return logs.iterator();
    }


    public List<String> compareToReal(List<LogInfo> logs){

        List<String> results = new ArrayList<>();

        int i = 0;
        for (LogInfo postLog : this.logs){

            LogInfo realLog = logs.get(i);

            String postAddress = Hex.toHexString(postLog.getAddress());
            String realAddress = Hex.toHexString(realLog.getAddress());

            if (!postAddress.equals(realAddress)){

                String formatedString = String.format("Log: %s: has unexpected address, expected address: %s found address: %s",
                        i, postAddress, realAddress);
                results.add(formatedString);
            }

            String postData = Hex.toHexString(postLog.getData());
            String realData = Hex.toHexString(realLog.getData());

            if (!postData.equals(realData)){

                String formatedString = String.format("Log: %s: has unexpected data, expected data: %s found data: %s",
                        i, postData, realData);
                results.add(formatedString);
            }

            String postBloom = Hex.toHexString(postLog.getBloom().getData());
            String realBloom = Hex.toHexString(realLog.getBloom().getData());

            if (!postData.equals(realData)){

                String formatedString = String.format("Log: %s: has unexpected bloom, expected bloom: %s found bloom: %s",
                        i, postBloom, realBloom);
                results.add(formatedString);
            }

            List<DataWord> postTopics = postLog.getTopics();
            List<DataWord> realTopics = realLog.getTopics();

            int j = 0;
            for (DataWord postTopic : postTopics){

                DataWord realTopic = realTopics.get(j);

                if (!postTopic.equals(realTopic)){

                    String formatedString = String.format("Log: %s: has unexpected topic: %s, expected topic: %s found topic: %s",
                            i, j, postTopic, realTopic);
                    results.add(formatedString);
                }
                ++j;
            }

            ++i;
        }

        return results;
    }

}