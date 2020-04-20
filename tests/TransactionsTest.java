
/*
package LCP.tests;
import LCP.LCPUtils;
import LCP.Transactions.Transactions;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
public class TransactionsTest {
    public static void main(String[] args) throws Exception {
        //signatureTest();
        rawTransactionTest();
    }

    @Test
    public static void objectTest() throws Exception {
        MessageDigest md = MessageDigest.getInstance("SHA-256");

        JSONObject payload = new JSONObject();//
        JSONObject input = new JSONObject();
        JSONObject output1 = new JSONObject();

        input.put("unit","MpoEkdur5KjmYaG9UMQct98ZHyRFxbXih82M/O+HvFo=");
        input.put("message_index",0);
        input.put("output_index",0);
        JSONObject[] inputs = {input};
        payload.put("inputs",inputs);

        output1.put("address","54JMGD3QJH6RH6KZXZWQYILXPALYMB4G");
        output1.put("amount",36176148);
        JSONObject[] outputs = {output1};

        payload.put("outputs",outputs);
        //System.out.print("key set is");
        //System.out.println(payload.keySet());
        //payload_hash = LSuifuH5Evbx+pW+wZw+OeSfGpIzowHDmWsAwCZcAro=
        //{"inputs":[{"unit":"MpoEkdur5KjmYaG9UMQct98ZHyRFxbXih82M/O+HvFo=","message_index":0,"output_index":0}


        String payloadString = LCPUtils.toString(payload);
        String teststr3 = "inputs\u0000[\u0000message_index\u0000n\u00000\u0000output_index\u0000n\u00000\u0000unit\u0000s\u0000TN9FZCoZPoX3hm7LAz+FiQDciKeyhFn62UtSvPWxtd8=\u0000]\u0000outputs\u0000[\u0000address\u0000s\u000054JMGD3QJH6RH6KZXZWQYILXPALYMB4G\u0000amount\u0000n\u000036379554\u0000]";
        String testStr2 = LCPUtils.toString(payload);
        //System.out.println(payloadString);
        md.update(payloadString.getBytes());
        byte[] digest = md.digest();
        //System.out.println(payloadString);
        //SHA256Digest digest = new SHA256Digest();
        //digest.update(testString.getBytes(StandardCharsets.UTF_8),0,testString.getBytes().length);
        //byte[] o = new byte[digest.getDigestSize()];
        //digest.doFinal(o,0);
        String encodedString = Base64.getEncoder().encodeToString(digest);
            ///System.out.println(encodedString);
    }



    @Test
    public static void rawTransactionTest() throws NoSuchAlgorithmException {
        JSONObject headerInfo = new JSONObject();
        String[] parentUnits = {"0O9uvGcF/sbVnTdfZkbfJcJVuQvVE6aIWk2f7SDZZnA="};
        headerInfo.put("parent_units",parentUnits);
        headerInfo.put("last_stable_mc_ball","MHbriaJFqc2Wxj/UBWSOk6EpLNlnfe71aXWsOdVMz8U=");
        headerInfo.put("last_stable_mc_ball_unit","7yjsGAO3C1s5bmXePNy+JLD5XJvxX2BFmN3HpCYWp0w=");
        headerInfo.put("witness_list_unit","FxtHGkOHjv7aXajkg00/22Xp7VVXZEvT5cXfe8BSZEQ=");

        String authorAddress = "5PNCW2VHMOSGRQYJW7WQ7JVTX3HY5ZDB";
        String authorSig = "yPSkwrAQB0gZhN7JsYGb18+xaDcTWdnJQTkQTOl0A1U65wEKM+u172iBWJxQq+TDNhiNnzEQ6kea4QUEbt3TmA==";
        String authorPubKey = "AoLhtN0i6EpUqlkWHX5xX0YYl9YXOrS6A7euni23hR5Q";

        JSONObject author = Transactions.createAuthor(authorAddress,authorPubKey,authorSig);
        JSONObject[] authors = {author};

        JSONObject testInput = new JSONObject();
        testInput.put("unit","OnpvoZpzsBcOd+zfshQhsSWF+HC7A20vpRfdgXpqZDc=");
        testInput.put("message_index",0);
        testInput.put("output_index",0);
        JSONObject[] testInputs = {testInput};

        JSONObject testOutput = new JSONObject();
        testOutput.put("address","5PNCW2VHMOSGRQYJW7WQ7JVTX3HY5ZDB");
        testOutput.put("amount",24586996);
        JSONObject[] testOutputs = {testOutput};



        JSONObject header = new JSONObject();
        header.put("version","1.0");
        header.put("alt","1");
        header.put("witness_list_unit","FxtHGkOHjv7aXajkg00/22Xp7VVXZEvT5cXfe8BSZEQ=");
        header.put("last_ball_unit","7yjsGAO3C1s5bmXePNy+JLD5XJvxX2BFmN3HpCYWp0w=");
        header.put("last_ball","MHbriaJFqc2Wxj/UBWSOk6EpLNlnfe71aXWsOdVMz8U=");
        String[] parent_units = {"0O9uvGcF/sbVnTdfZkbfJcJVuQvVE6aIWk2f7SDZZnA="};
        header.put("parent_units",parent_units);
        JSONObject authorNoSig = new JSONObject();
        authorNoSig.put("address","5PNCW2VHMOSGRQYJW7WQ7JVTX3HY5ZDB");
        JSONObject[] authorsNoSig = {authorNoSig};
        header.put("authors",authorsNoSig);

        JSONObject unit = Transactions.makeRaw(testOutputs,testInputs,authors,headerInfo);
        System.out.println("this is the unit");
        System.out.println(unit);
        JSONObject msg = new JSONObject();
        msg.put("app","payment");
        msg.put("payload_hash","pXVXZVN5MRXhJZtHyGtadSu3DSnuKiUm8espRwBg7rs=");
        msg.put("payload_location","inline");

        JSONObject[] msgs = {msg};

        //System.out.print("this is the ")
    }

}
*/