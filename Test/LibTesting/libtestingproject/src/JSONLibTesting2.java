import net.sf.json.JSON;
import net.sf.json.JSONObject;
import net.sf.json.JSONSerializer;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * Created by isurur on 11/18/16.
 */
public class JSONLibTesting2 {
    public static void main(String[] args) {

        System.out.println("Read JSON from file, convert JSON string back to object");
        long start = 0;
        long end = 0;
        try {

            byte[] encoded = Files.readAllBytes(Paths.get(
                    "/home/isurur/Documents/training_ESB/gw-repos/LibTesting/libtestingproject/src/1MB-sample-json"));
            String json = new String(encoded, "UTF-8");
            for (int i = 0; i < 100; i++) {
                if (i == 10) {

                    start = System.nanoTime();
            }
            // StringReader reader = new StringReader(json);


                JSON json1 = JSONSerializer.toJSON(json);


           //  Student obj = (Student) JSONObject.toBean(JSONObject(reader), Student.class);
           //     System.out.print(obj.getAge());
            }

            end = System.nanoTime();

            long diff = end - start;
            System.out.println("Time taken " + (diff / 90));

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
