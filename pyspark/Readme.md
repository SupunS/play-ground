How to Setup
============


- Install jupyter

- Download and uncompress spark binary.

- Set following in ~/.bashrc

	export PYSPARK_DRIVER_PYTHON=ipython
	export PYSPARK_DRIVER_PYTHON_OPTS='notebook' pyspark
	export PYSPARK_PYTHON=/home/supun/Supun/Softwares/anaconda3/bin/python
	export SPARK_HOME="/home/supun/Supun/Softwares/spark-1.6.2-bin-hadoop2.6"
	export PYSPARK_SUBMIT_ARGS="--master local[2]"
	export PATH="/home/supun/Supun/Softwares/spark-1.6.2-bin-hadoop2.6/bin:$PATH"
	export SPARK_CLASSPATH=/home/supun/Downloads/wso2das-3.1.0/repository/components/plugins/abdera_1.0.0.wso2v3.jar:/home/supun/Downloads/wso2das-3.1.0/repository/components/plugins/ajaxtags_1.3.0.beta-rc7-wso2v1.jar.......

(Note: in the abve SPARK_CLASSPATH, we need to add all the jars in DAS's "plugins" as well as "libs" directories)

	export PYTHONPATH=$SPARK_HOME/python/lib/py4j-0.9-src.zip:$PYTHONPATH
	export PYTHONPATH=$SPARK_HOME/python:$PYTHONPATH
	export PYTHONPATH=$SPARK_HOME/python/lib:$PYTHONPATH

- Dowload pyrolite-4.13.jar and add that to SPARK_CLASSPATH as well. This jar is needed at runtime when converting spark-dataframe to pandas-dataframe

- Run source ~/.bashrc to make the changes take effect.

- Create an "python-workspace" directory and navigate to it.

- Start DAS in Cluster mode. Make sure to reduce the cores allocated to CarbonAnalytics spark APP. Unset SPARK_CLASSPATH to start DAS, if python and DAS are running in the same machine.
		Spark Master UI will be available at http://localhost:8081

- Run: pyspark --master spark://10.100.5.116:7077 --conf "spark.driver.extraJavaOptions=-Dwso2_custom_conf_dir=/home/supun/Downloads/wso2das-3.1.0/repository/conf"
	jupyter will be available in http://localhost:8888/
	Here, spark://10.100.5.116:7077 is the Spark Master URL of cluster created by DAS



Running .py script:
-------------------

 <SPARK_HOME>./bin/spark-submit --master spark://<spark-master-ip>:7077 --conf "spark.driver.extraJavaOptions=-Dwso2_custom_conf_dir=/home/supun/Downloads/wso2das-3.1.0/repository/conf" /home/supun/Supun/MachineLearning/python/PySpark-Sample.py
