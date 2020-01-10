package LCP.Transactions;
import LCP.LCPUtils;
import org.json.JSONObject;

import java.security.NoSuchAlgorithmException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Iterator;

public class Transactions {

    /**
     *
     * @param outputs [ {   "address":"54JMGD3QJH6RH6KZXZWQYILXPALYMB4G",
     *                      "amount":36176148}
     *                      ...
     *                ]
     *
     * @param inputs where are the coins coming from
     * @param authors who is signing for the coins
     * @param headerInfo get parent units, and other 'ball' information
     * @param verifier network or local,transaction integrity check can be null
     * @return raw transaction (unsigned)
     * @throws Exception
     */
    /*
    public static JSONObject makeRaw(JSONObject[] outputs,
                                     JSONObject[] inputs,
                                     JSONObject[] authors,
                                     JSONObject headerInfo,
                                     Verifier verifier) throws Exception {

        verifyTransaction(verifier, outputs, inputs, authors, headerInfo);

        return createUnit(outputs, inputs, authors, headerInfo);
    }
*/

    public static JSONObject makeRaw(JSONObject[] outputs,
                                     JSONObject[] inputs,
                                     JSONObject[] authors,
                                     JSONObject headerInfo)
            throws NoSuchAlgorithmException {

        return createUnit(outputs, inputs, authors,headerInfo);
    }


    public static JSONObject createAuthor(String address,
                                          String b64pubkey,
                                          String b64sig){
        JSONObject author = new JSONObject();
        JSONObject pubkey = new JSONObject();
        JSONObject authentifiers = new JSONObject();
        authentifiers.put("r",b64sig);
        pubkey.put("pubkey",b64pubkey);
        Object[] standardReleaseCondition  = {"sig",pubkey};
        author.put("address",address);
        author.put("authentifiers",authentifiers);
        //author.put("definition",standardReleaseCondition);

        return author;
    }
/*
    private static void verifyTransaction(Verifier verifier,
                                          JSONObject[] outputs,
                                          JSONObject[] inputs,
                                          JSONObject[] authors,
                                          JSONObject headerInfo
    ) throws Exception {

        verifier.checkOutputs(outputs);
        verifier.checkInputs(inputs);
        verifier.checkAuthors(authors);
        verifier.checkNetworkInfoIntegrity(headerInfo);
    }

*/
    private static JSONObject createUnit(JSONObject[] outputs,
                                         JSONObject[] inputs,
                                         JSONObject[] authors,
                                         JSONObject headerInfo)
            throws NoSuchAlgorithmException {

        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        JSONObject payload = createPayload(inputs,outputs);
        JSONObject msg = createMsg(payload,true);
        JSONObject[] msgArr = {msg};
        JSONObject header = createHeader(authors, headerInfo,true);

        JSONObject unit = new JSONObject(header, JSONObject.getNames(header));
        /*for(String key : JSONObject.getNames(header))
        {
            unit.put(key, header.get(key));
        }
        */
        int headerCommission = calcHeaderCommission(header);
        int payloadCommission = calcPayloadCommission(msgArr);
        JSONObject headerNoAuth = createHeader(authors,headerInfo,false);
        String unitHash =
                calcUnitHash(header, headerNoAuth, createMsg(payload,false));

        unit.put("unit",unitHash);
        unit.put("header_commission",headerCommission);
        unit.put("payload_commission",payloadCommission);
        unit.put("timestamp",timestamp.getTime());
        unit.put("messages",msgArr);

        return unit;
    }


    private static JSONObject createMsg(JSONObject payload,
                                        boolean withPayload)//or just metadata
            throws NoSuchAlgorithmException {

        JSONObject message = new JSONObject();
        String app = "payment";
        String payloadLocation = "inline";
        message.put("app",app);
        message.put("payload_location",payloadLocation);
        message.put("payload_hash",LCPUtils.shaHash(payload));
        if(withPayload){
            message.put("payload",payload);
            return message;
        }
        return message;
    }


    private static JSONObject createPayload(JSONObject[] inputs, JSONObject[] outputs){
        JSONObject payload = new JSONObject();
        payload.put("outputs",outputs);
        payload.put("inputs",inputs);
        return payload;
    }


    private static JSONObject createHeader(JSONObject[] authors,
                                           JSONObject headerInfo,
                                           Boolean withAuthentication){

        JSONObject header = new JSONObject();
        String version = "1.0";
        String alt = "1";
        header.put("last_ball",headerInfo.get("last_stable_mc_ball"));
        header.put("last_ball_unit",headerInfo.get("last_stable_mc_ball_unit"));
        header.put("version",version);
        header.put("alt",alt);
        header.put("authors",authors);
        header.put("witness_list_unit",headerInfo.get("witness_list_unit"));
        header.put("parent_units",headerInfo.get("parent_units"));

        if(!withAuthentication){
            JSONObject newHeader = new JSONObject(header, JSONObject.getNames(header));
            JSONObject[] oldAuthorsArr = (JSONObject[]) header.get("authors");
            JSONObject[] newAuthorsArr = new JSONObject[oldAuthorsArr.length];
            JSONObject newAuthor = new JSONObject();
            for(int i =0 ; i < oldAuthorsArr.length;i++){
                newAuthorsArr[i] = newAuthor.put("address",oldAuthorsArr[i].get("address"));
            }
            newHeader.put("authors",newAuthorsArr);
            return newHeader;
        }
        else {
            return header;
        }
    }


    private static int calcHeaderCommission(JSONObject header){
        final int PARENT_UNITS_SIZE = 88;
        JSONObject newHeader = new JSONObject(header, JSONObject.getNames(header));
        newHeader.remove("parent_units");
        System.out.print("header size is ");
        System.out.println(calcSize(newHeader) + PARENT_UNITS_SIZE);
        return calcSize(newHeader) + PARENT_UNITS_SIZE;
    }


    private static int calcPayloadCommission(JSONObject[] payload){

        System.out.print("this the payload");
        //System.out.println(payload);
        return calcSize(payload);
    }


    private static int calcSize(Object element){
        System.out.print("the element is ");
        System.out.println(element);
        if(element == null){
            return 0;
        }
        else if(element instanceof String){
            return  ((String) element).length();
        }
        else if(element instanceof Integer){
            return 8;
        }
        else if(element instanceof JSONObject){
            int length = 0;
            JSONObject jsonObj = (JSONObject) element;
            Iterator<String> keys = jsonObj.keys();
            while(keys.hasNext()){
                String key = keys.next();
                System.out.print("key is ");
                System.out.println(key);
                System.out.print("attribute is ");
                System.out.println(jsonObj.get(key));
                length += calcSize(jsonObj.get(key));
                System.out.print("length is  ");
                System.out.println(length);
            }
            return length;
        }
        else if(element instanceof Object[]){
            int length = 0;
            Object[] headerElementArray = (Object[]) element;
            for(Object arrMember : headerElementArray){
                System.out.print("arr member is ");
                System.out.println(arrMember);
                length += calcSize(arrMember);
                System.out.print("length is ");
                System.out.println(length);
            }
            return length;
        }
        else if(element instanceof Boolean){
            return 1;
        }
        return 0;
    }



    private static String calcUnitHash(JSONObject header, JSONObject headerNoAuth, JSONObject msgNoPayload)
            throws NoSuchAlgorithmException {

        JSONObject partialUnit = new JSONObject(header, JSONObject.getNames(header));
        JSONObject[] messages = {msgNoPayload};
        partialUnit.put("messages",messages);
        String contentHash = LCPUtils.shaHash(partialUnit);
        headerNoAuth.put("content_hash",contentHash);

        return LCPUtils.shaHash(headerNoAuth);
    }

}