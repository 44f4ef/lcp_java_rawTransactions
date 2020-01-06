//put package information here
package LCP;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import org.json.*;

public class LCPUtils {

    public static String shaHash (JSONObject payload) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        String payloadString = LCPUtils.toString(payload);
        //System.out.println(payloadString);
        md.update(payloadString.getBytes());
        byte[] digest = md.digest();
        //System.out.println(payloadString);

        return Base64.getEncoder().encodeToString(digest);

    }


    public static String toString (Object preImage) {
        String stringJoinChar = "\u0000";
        String[] componentsArray = makeComponentStringArray(preImage);
        return String.join(stringJoinChar,componentsArray);

    }


    private static String[] makeComponentStringArray(Object element){
        /**
         * makes an array that contains a prefix for the element type used
         * @param element
         * @throws IllegalArgumentException
         * @throws UnsupportedOperationException for unsupported data types
        */

        if (element instanceof String) {
            return new String[]{"s", (String) element};
        }

        else if (element instanceof Integer) {
            return new String[]{"n", element.toString()};
        }

        else if (element instanceof Boolean) {
            return new String[]{"b", element.toString()};
        }

        else if (element instanceof Object[]) {
            Object[] elementArray = (Object[]) element;
            if(elementArray.length == 0){
                throw new IllegalArgumentException("array can not be empty");
            }
            ArrayList<String> stringArrayList= new ArrayList<String>();
            stringArrayList.add("[");
            for(Object arrayElement : elementArray){
                String[] innerElementStringArray = makeComponentStringArray(arrayElement);
                stringArrayList.addAll(Arrays.asList(innerElementStringArray));
            }
            stringArrayList.add("]");
            return stringArrayList.toArray(new String[0]);
        }
        else if (element instanceof JSONObject){
            JSONObject elementObject  = (JSONObject) element;
            ArrayList<String> stringArrayList = new ArrayList<String>();
            List<String> keys;
            keys = new ArrayList<>(elementObject.keySet());;
            Collections.sort(keys);
            for (String key : keys) {
                String[] objectKeyComponentArray = new String[]{key};//makeComponentStringArray(key);
                stringArrayList.addAll(Arrays.asList(objectKeyComponentArray));
                //System.out.println(stringArrayList);
                String[] objectAttrComponentArray = makeComponentStringArray(elementObject.get(key));
                stringArrayList.addAll(Arrays.asList(objectAttrComponentArray));
            }

            return stringArrayList.toArray(new String[0]);
        }

        else {
            throw new UnsupportedOperationException("data type not supported");
        }
    }

}
