/*
 * Copyright (c) 2015, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

import org.apache.commons.lang3.ArrayUtils;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.ml.Pipeline;
import org.apache.spark.ml.PipelineModel;
import org.apache.spark.ml.PipelineStage;
import org.apache.spark.ml.classification.RandomForestClassifier;
import org.apache.spark.ml.clustering.KMeans;
import org.apache.spark.ml.evaluation.MulticlassClassificationEvaluator;
import org.apache.spark.ml.feature.IndexToString;
import org.apache.spark.ml.feature.StringIndexer;
import org.apache.spark.ml.feature.StringIndexerModel;
import org.apache.spark.ml.feature.VectorAssembler;
import org.apache.spark.mllib.evaluation.MulticlassMetrics;
import org.apache.spark.mllib.linalg.Matrix;
import org.apache.spark.sql.DataFrame;
import org.apache.spark.sql.SQLContext;

public class TestRandomForest {

    final static String RESPONSE_VARIABLE =  "class";
    final static String INDEXED_RESPONSE_VARIABLE =  "indexedClass";
    final static String FEATURES = "features";
    final static String PREDICTION = "prediction";
    final static String PREDICTION_LABEL = "predictionLabel";
    
    public static void main(String[] args) throws Exception {
        
        SparkConf sparkConf = new SparkConf();
        sparkConf.setAppName("test-client").setMaster("local[2]");
        sparkConf.set("spark.driver.allowMultipleContexts", "true");
        JavaSparkContext javaSparkContext = new JavaSparkContext(sparkConf);
        SQLContext sqlContext = new SQLContext(javaSparkContext);
        
        DataFrame dataFrame = sqlContext.read().format("com.databricks.spark.csv")
                                                .option("inferSchema", "true")
                                                .option("header", "true")
                                                .load("/home/supun/Supun/MachineLearning/data/Iris/train.csv");
        
        double [] dataSplitWeights = {0.7,0.3};
        DataFrame[] data = dataFrame.randomSplit(dataSplitWeights);
        
        // Create a vector from columns. Name the resulting vector as "features"
        String [] predictors = dataFrame.columns();
        predictors = ArrayUtils.removeElement(predictors, RESPONSE_VARIABLE);
        
        VectorAssembler vectorAssembler = new VectorAssembler();
        vectorAssembler.setInputCols(predictors).setOutputCol(FEATURES);
        
        MeanImputer meanImputer = new MeanImputer(predictors);
            
        // Index labels
        StringIndexerModel labelIndexer = new StringIndexer().setInputCol(RESPONSE_VARIABLE)
                                                             .setOutputCol(INDEXED_RESPONSE_VARIABLE)
                                                             .fit(data[0]);
        
        /*
        // Automatically identify categorical features, and index them.
        // Set maxCategories so features with > 4 distinct values are treated as continuous.
        VectorIndexerModel featureIndexer = new VectorIndexer().setInputCol("features")
                                                                .setOutputCol("indexedFeatures")
                                                                .setMaxCategories(20)
                                                                .fit(data[0]);
         */
        
        
        // Train a RandomForest model.
        RandomForestClassifier randowmForest = new RandomForestClassifier().setLabelCol(INDEXED_RESPONSE_VARIABLE)
                                                                           .setFeaturesCol(FEATURES)
                                                                           .setMaxBins(100)
                                                                           .setNumTrees(10)
                                                                           .setMaxDepth(10);

        KMeans kmeans = new KMeans().setK(10);
        
        // Convert indexed labels back to original labels.
        IndexToString labelConverter = new IndexToString().setInputCol(PREDICTION)
                                                          .setOutputCol(PREDICTION_LABEL)
                                                          .setLabels(labelIndexer.labels());

        Pipeline pipeline = new Pipeline().setStages(new PipelineStage[] {meanImputer, labelIndexer, vectorAssembler, kmeans});

        
        // Fit the pipeline to training documents.
        PipelineModel pipelineModel = pipeline.fit(data[0]);
        

        // ======================== Validate ========================
        DataFrame predictions = pipelineModel.transform(data[1]);
        predictions.show(200);
        
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
