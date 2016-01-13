
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.ml.classification.ProbabilisticClassifier;
import org.apache.spark.ml.param.ParamMap;
import org.apache.spark.mllib.classification.SVMWithSGD;
import org.apache.spark.mllib.linalg.Vector;
import org.apache.spark.mllib.regression.LabeledPoint;
import org.apache.spark.sql.DataFrame;
import org.apache.spark.sql.Row;

public class SVMClassifier extends ProbabilisticClassifier<Object, SVMClassifier, SVMClassifierModel> {

    private static final long serialVersionUID = 1L;
    int numIterations;

    @Override
    public String uid() {
        return "SVMClassifier";
    }

    @Override
    public SVMClassifier copy(ParamMap arg0) {
        return null;
    }

    @Override
    public SVMClassifierModel train(DataFrame df) {
        
        int responseIndex = df.head().fieldIndex(getLabelCol());
        int featuresIndex = df.head().fieldIndex(getFeaturesCol());
        
        // convert dataframe to labeled points
        JavaRDD<Row> features = df.toJavaRDD();
        JavaRDD<LabeledPoint> labeledPoints = features.map(row -> new LabeledPoint(row.getDouble(responseIndex), (Vector)row.get(featuresIndex)));
        
        // train MLlib SVM
        SVMWithSGD svmWithSGD = new SVMWithSGD();
        svmWithSGD.optimizer().setNumIterations(100).setStepSize(0.1).setMiniBatchFraction(0.1);
        
        // create a wrapper around svm
        SVMClassifierModel svmModelWrapper = new SVMClassifierModel(svmWithSGD.run(labeledPoints.rdd()));
        return svmModelWrapper;
    }

}
