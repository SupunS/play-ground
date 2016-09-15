from pyspark import SparkContext, SparkConf, SQLContext
from pyspark.ml.feature import VectorAssembler
from pyspark.mllib.feature import LabeledPoint
from pyspark.sql.functions import col
from pyspark.ml import Pipeline
from pyspark.ml.classification import RandomForestClassifier
from pyspark.ml.feature import StringIndexer, VectorIndexer
from pyspark.ml.evaluation import MulticlassClassificationEvaluator




###################### Set the additional propeties. #####################
sparkConf = (SparkConf()
             .set(key="carbon.insert.batch.size", value="1000")
             .set(key="spark.driver.allowMultipleContexts",value="true")
             .set(key="spark.executor.extraJavaOptions", value="-Dwso2_custom_conf_dir=/home/supun/Downloads/wso2das-3.1.0/repository/conf"))

#Create a new SparkContext using the above SparkConf.
sparkCtx = SparkContext(conf=sparkConf)

# Check spark master.
print(sparkConf.get("spark.master"));


###################### Get data from DAS table #####################
sqlCtx = SQLContext(sparkCtx)
sqlCtx.sql('CREATE TEMPORARY TABLE table1 ' +
           'USING org.wso2.carbon.analytics.spark.core.sources.AnalyticsRelationProvider ' +
           'OPTIONS (tenantId "-1234", tableName "IRIS_DATA_STREAM")')

df = sqlCtx.sql("SELECT * FROM table1");



##################### Prepare the data #####################
assembler = VectorAssembler(inputCols=["sepal_length", "sepal_width", "petal_length", "petal_width"],
                            outputCol="features")

assembledDf = assembler.transform(df)

assembledDf.show()

transformedDf = assembledDf.select(col("class").alias("label"), col("features"))

transformedDf.show()
transformedDf.printSchema()



##################### Training Model ############################

labelIndexer = StringIndexer(inputCol="label", outputCol="indexedLabel").fit(transformedDf)
featureIndexer = VectorIndexer(inputCol="features", outputCol="indexedFeatures", maxCategories=4).fit(transformedDf)

# Split the data into training and test sets (30% held out for testing)
(trainingData, testData) = transformedDf.randomSplit([0.7, 0.3])

rf = RandomForestClassifier(labelCol="indexedLabel", featuresCol="indexedFeatures")

# Chain indexers and forest in a Pipeline
pipeline = Pipeline(stages=[labelIndexer, featureIndexer, rf])

# Train model.  This also runs the indexers.
model = pipeline.fit(trainingData)



#####################  Make predictions. ##################### 
predictions = model.transform(testData)

# Select sample rows to display.
predictions.select("prediction", "indexedLabel", "features").show(5)

# Select (prediction, true label) and compute test error
evaluator = MulticlassClassificationEvaluator(labelCol="indexedLabel", predictionCol="prediction", metricName="precision")
accuracy = evaluator.evaluate(predictions)

print("Accuracy =  " + str(round(accuracy*100, 2)) + "%")

rfModel = model.stages[2]
print(rfModel)
