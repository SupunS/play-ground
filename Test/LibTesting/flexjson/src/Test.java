import flexjson.JSONDeserializer;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * Created by isurur on 11/18/16.
 */
public class Test {
    public static void main(String[] args) {

        Food stdClass = new Food();
        //        JSONSerializer serializer = new JSONSerializer().prettyPrint(true);
        //        //deep serialiing
        //        String jsonStr = serializer.deepSerialize(stdClass);
        //
        //        //serializing
        //        String jsonStr1 = serializer.serialize(stdClass);

        //deserializing
        System.out.println("Read JSON from file, convert JSON string back to object");
        long start = 0;
        long end = 0;
        try {

            byte[] encoded = Files.readAllBytes(Paths.get("/home/isurur/Documents/training_ESB/gw-repos/LibTesting/flexjson/src/100k-sample-json"));
            String json = new String(encoded, "UTF-8");
            for (int i = 0; i < 10000; i++) {
                if (i == 100) {

                    start = System.nanoTime();
                }
                StringReader reader = new StringReader(json);


                Food obj = new JSONDeserializer<Food>().deserialize(reader, Food.class);

            }

            end = System.nanoTime();

            long diff = end - start;
            System.out.println("Time taken " + (diff / 9000));

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
