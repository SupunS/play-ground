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
import org.apache.spark.ml.regression.GBTRegressor;
import org.apache.spark.ml.regression.LinearRegression;
import org.apache.spark.ml.regression.RandomForestRegressor;
import org.apache.spark.mllib.evaluation.MulticlassMetrics;
import org.apache.spark.mllib.linalg.Matrix;
import org.apache.spark.sql.DataFrame;
import org.apache.spark.sql.SQLContext;

public class TimeSeriesWithRegression {

    final static String RESPONSE_VARIABLE =  "co2";
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
        DataFrame trainDataFrame = sqlContext.read().format("com.databricks.spark.csv")
                                                .option("inferSchema", "true")
                                                .option("header", "true")
                                                .load("/home/supun/Supun/MachineLearning/TimeSeries/data/co2/df/co2.csv");
        
        DataFrame testDataFrame = sqlContext.read().format("com.databricks.spark.csv")
                .option("inferSchema", "true")
                .option("header", "true")
                .load("/home/supun/Supun/MachineLearning/TimeSeries/data/co2/df/co2-test.csv");
        
        
        // Get predictor variable names
        String [] predictors = trainDataFrame.columns();
        predictors = ArrayUtils.removeElement(predictors, RESPONSE_VARIABLE);
        
        
        
        // ======================== Preprocess ===========================
        // Drawback: when encoding features, need to  fit the transformers to data, prior to pipeline.
        
        // Impute missing values
        MeanImputer meanImputer = new MeanImputer(predictors);
        
        // Create a vector from columns. Name the resulting vector as "features"
        VectorAssembler vectorAssembler = new VectorAssembler();
        vectorAssembler.setInputCols(predictors).setOutputCol(FEATURES);

        // ======================== Train ========================
        // Define a SVM
        // Currently only supports binary clasification
        
        RandomForestRegressor rfr = new RandomForestRegressor().setLabelCol(RESPONSE_VARIABLE)
                                                               .setFeaturesCol(FEATURES)
                                                               .setNumTrees(40)
                                                               .setMaxBins(30)
                                                               .setMaxDepth(10)
                                                               .setMinInfoGain(0.5);
        LinearRegression lr = new LinearRegression().setMaxIter(1000)
                                                    .setRegParam(0.01)
                                                    .setLabelCol(RESPONSE_VARIABLE)
                                                    .setFeaturesCol(FEATURES);
        
        GBTRegressor gbt = new GBTRegressor().setLabelCol(RESPONSE_VARIABLE)
                                             .setFeaturesCol(FEATURES)
                                             .setMaxIter(50)
                                             .setMaxBins(50)
                                             .setMaxDepth(30)
                                             .setMinInfoGain(0.0001)
                                             .setStepSize(0.00001);
        
        // instantiate the One Vs Rest Classifier
        //OneVsRest oneVsRestClassifier = new OneVsRest().setClassifier(svmWrapper).setLabelCol(INDEXED_RESPONSE_VARIABLE);
        
        // Fit the pipeline for training.
        Pipeline pipeline = new Pipeline().setStages(new PipelineStage[] {meanImputer, vectorAssembler, gbt});
        PipelineModel pipelineModel = pipeline.fit(trainDataFrame);
        
 
        
        
        // ======================== Validate ========================
        DataFrame predictions = pipelineModel.transform(testDataFrame);

        predictions.select("prediction").show(300);
    }
}
