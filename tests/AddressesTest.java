package LCP.tests;
import LCP.Addresses;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;
import org.junit.*;

class AddressesTest {
    public static void main(String[] args) throws Exception {
        should_Return_A2WWHN7755YZVMXCBLMFWRSLKSZJN3FU();
    }

    @Test
    static void should_Return_A2WWHN7755YZVMXCBLMFWRSLKSZJN3FU() throws Exception {
        JSONObject publicKeyJson = new JSONObject(); //address definition
        publicKeyJson.put("pubkey","Ald9tkgiUZQQ1djpZgv2ez7xf1ZvYAsTLhudhvn0931w");
        Object[] releaseCondition = new Object[]{"sig",publicKeyJson};
        String walletAddress = Addresses.generateAddress(releaseCondition);

        System.out.print(walletAddress);
    }


}

