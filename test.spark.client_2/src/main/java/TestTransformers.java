import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.ml.Pipeline;
import org.apache.spark.ml.PipelineModel;
import org.apache.spark.ml.PipelineStage;
import org.apache.spark.ml.evaluation.MulticlassClassificationEvaluator;
import org.apache.spark.ml.feature.IndexToString;
import org.apache.spark.ml.feature.StringIndexer;
import org.apache.spark.ml.feature.StringIndexerModel;
import org.apache.spark.ml.feature.VectorAssembler;
import org.apache.spark.mllib.evaluation.MulticlassMetrics;
import org.apache.spark.mllib.linalg.Matrix;
import org.apache.spark.sql.DataFrame;
import org.apache.spark.sql.SQLContext;

public class TestTransformers {

    final static String RESPONSE_VARIABLE =  "class";
    final static String INDEXED_RESPONSE_VARIABLE =  "indexedClass";
    final static String FEATURES = "features";
    final static String PREDICTION = "prediction";
    final static String PREDICTION_LABEL = "predictionLabel";
    
    public static void main(String[] args) throws InterruptedException {

        SparkConf sparkConf = new SparkConf();
        sparkConf.setAppName("test-client").setMaster("local[2]");
        sparkConf.set("spark.driver.allowMultipleContexts", "true");
        JavaSparkContext javaSparkContext = new JavaSparkContext(sparkConf);
        SQLContext sqlContext = new SQLContext(javaSparkContext);
        
        // ======================== Import data ====================================
        DataFrame dataFrame = sqlContext.read().format("com.databricks.spark.csv")
                                                .option("inferSchema", "true")
                                                .option("header", "true")
                                                .load("/home/supun/Supun/data/train.csv");
        
        Map<String, String> regexMap = new LinkedHashMap<String, String>();
        regexMap.put(".*GRAND THEFT.*", "topic_1");
        regexMap.put(".*AUTOMOBILE.*", "topic_1");
        regexMap.put(".*TRAFFIC VIOLATION.*", "topic_1");
        regexMap.put(".*CHILD ABUSE.*", "topic_2");
        regexMap.put(".*BURGLARY.*", "topic_3");
        regexMap.put(".*DRIVING.*", "topic_1");
        regexMap.put(".*MALICIOUS .*", "topic_3");
        regexMap.put(".*STOLEN .*", "topic_1");
        for (int i = 0 ; i < 10000 ; i++) { 
            regexMap.put(i + ".*GRAND THEFT.*", "topic_1");
            regexMap.put(i + ".*AUTOMOBILE.*", "topic_1");
            regexMap.put(i + ".*TRAFFIC VIOLATION.*", "topic_1");
            regexMap.put(i + ".*CHILD ABUSE.*", "topic_2");
            regexMap.put(i + ".*BURGLARY.*", "topic_3");
            regexMap.put(i + ".*DRIVING.*", "topic_1");
            regexMap.put(i + ".*MALICIOUS .*", "topic_3");
            regexMap.put(i + ".*STOLEN .*", "topic_1");
        }
        
        long startTime = System.currentTimeMillis();
        
        RegexTransformer rt = new RegexTransformer("Description", regexMap);
        DataFrame transformedDF =  rt.transform(dataFrame);
        
        long endTime = System.currentTimeMillis();
        
        transformedDF.show(200);
        
/*        Thread.sleep(5000);
        dataFrame.rdd();
        Thread.sleep(5000);*/
        
        

        System.out.println("Elapsed milliseconds: " + (endTime - startTime)/1000.0);
    }
}
