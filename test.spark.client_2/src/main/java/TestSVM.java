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

public class TestSVM {

    final static String RESPONSE_VARIABLE =  "class";
    final static String INDEXED_RESPONSE_VARIABLE =  "indexedClass";
    final static String FEATURES = "features";
    final static String PREDICTION = "prediction";
    final static String PREDICTION_LABEL = "predictionLabel";
    
    public static void main(String[] args) {

        SparkConf sparkConf = new SparkConf();
        sparkConf.setAppName("test-client").setMaster("local[2]");
        sparkConf.set("spark.driver.allowMultipleContexts", "true");
        JavaSparkContext javaSparkContext = new JavaSparkContext(sparkConf);
        SQLContext sqlContext = new SQLContext(javaSparkContext);
        
        // ======================== Import data ====================================
        DataFrame dataFrame = sqlContext.read().format("com.databricks.spark.csv")
                                                .option("inferSchema", "true")
                                                .option("header", "true")
                                                .load("/home/supun/Supun/MachineLearning/data/Iris/train-2.csv");
        
        // Split in to train/test data
        double [] dataSplitWeights = {0.7,0.3};
        DataFrame[] data = dataFrame.randomSplit(dataSplitWeights);
        
        // Test custom Transformer
        CustomTransformer cs = new CustomTransformer();
        cs.transform(data[0]).show();
        
        // Get predictor variable names
        String [] predictors = dataFrame.columns();
        predictors = ArrayUtils.removeElement(predictors, RESPONSE_VARIABLE);
        
        
        
        // ======================== Preprocess ===========================
        // Drawback: when encoding features, need to  fit the transformers to data, prior to pipeline.
        
        // Impute missing values
        MeanImputer meanImputer = new MeanImputer(predictors);
        
        // Create a vector from columns. Name the resulting vector as "features"
        VectorAssembler vectorAssembler = new VectorAssembler();
        vectorAssembler.setInputCols(predictors).setOutputCol(FEATURES);
        
        // Encode labels
        StringIndexerModel labelIndexer = new StringIndexer().setInputCol(RESPONSE_VARIABLE)
                                                             .setOutputCol(INDEXED_RESPONSE_VARIABLE)
                                                             .fit(data[0]);
        
        
        // Convert indexed labels back to original labels (decode labels).
        IndexToString labelConverter = new IndexToString().setInputCol(PREDICTION)
                                                          .setOutputCol(PREDICTION_LABEL)
                                                          .setLabels(labelIndexer.labels());
        
        
        // ======================== Train ========================
        // Define a SVM
        // Currently only supports binary clasification
        
        SVMClassifier svmClassifier = new SVMClassifier().setLabelCol(INDEXED_RESPONSE_VARIABLE).setFeaturesCol(FEATURES);
        
        // instantiate the One Vs Rest Classifier
        //OneVsRest oneVsRestClassifier = new OneVsRest().setClassifier(svmWrapper).setLabelCol(INDEXED_RESPONSE_VARIABLE);
        
        // Fit the pipeline for training.
        Pipeline pipeline = new Pipeline().setStages(new PipelineStage[] {meanImputer, labelIndexer, vectorAssembler, svmClassifier, labelConverter});
        PipelineModel pipelineModel = pipeline.fit(data[0]);
        
 
        
        
        // ======================== Validate ========================
        DataFrame predictions = pipelineModel.transform(data[1]);

        // Confusion Matrix
        MulticlassMetrics metrics = new MulticlassMetrics(predictions.select(PREDICTION, INDEXED_RESPONSE_VARIABLE));
        Matrix confusionMatrix = metrics.confusionMatrix();
        
        // Accuracy Measures
        MulticlassClassificationEvaluator evaluator = new MulticlassClassificationEvaluator().setLabelCol(INDEXED_RESPONSE_VARIABLE)
                                                                                             .setPredictionCol(PREDICTION)
                                                                                             .setMetricName("precision");
        double accuracy = evaluator.evaluate(predictions);
        
        System.out.println("===== Confusion Matrix ===== \n" + confusionMatrix + "\n============================");
        System.out.println("Accuracy = " + accuracy);
    }
}
