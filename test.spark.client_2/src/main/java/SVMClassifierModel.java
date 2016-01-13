import org.apache.spark.ml.classification.ProbabilisticClassificationModel;
import org.apache.spark.ml.param.ParamMap;
import org.apache.spark.mllib.classification.SVMModel;
import org.apache.spark.mllib.linalg.Vector;
import org.apache.spark.mllib.linalg.Vectors;


public class SVMClassifierModel extends ProbabilisticClassificationModel<Object, SVMClassifierModel> {

    private static final long serialVersionUID = 1L;
    private SVMModel svmModel;
    
    SVMClassifierModel (SVMModel svmModel) {
        this.svmModel = svmModel;
    }

    @Override
    public String uid() {
        return "SVMClassifierModel";
    }

    @Override
    public int numClasses() {
        return 0;
    }

    @Override
    public Vector predictRaw(Object featuresVector) {
        return Vectors.dense(svmModel.predict((Vector) featuresVector));
    }

    @Override
    public SVMClassifierModel copy(ParamMap arg0) {
        return null;
    }

    @Override
    public Vector raw2probabilityInPlace(Vector arg0) {
        // TODO Auto-generated method stub
        return null;
    }
    
    public double raw2prediction(Vector rawPrediction) {
        return rawPrediction.toArray()[0];
    }

}
