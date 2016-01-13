import org.apache.spark.ml.Transformer;
import org.apache.spark.ml.param.ParamMap;
import org.apache.spark.sql.DataFrame;
import org.apache.spark.sql.functions;
import org.apache.spark.sql.types.StructType;


public class CustomTransformer extends Transformer {

    private static final long serialVersionUID = 5545470640951989469L;
    
    CustomTransformer() {
    }
    
    @Override
    public String uid() {
        return "CustomTransformer" + serialVersionUID;
    }

    @Override
    public Transformer copy(ParamMap arg0) {
        return null;
    }

    @Override
    public DataFrame transform(DataFrame data) {
        return data.withColumn("power", functions.pow(data.col("petal_length"), 3));
    }

    @Override
    public StructType transformSchema(StructType arg0) {
        return arg0;
    }
}
