import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.spark.ml.Transformer;
import org.apache.spark.ml.param.ParamMap;
import org.apache.spark.sql.DataFrame;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.types.StructType;


public class MeanImputer extends Transformer {

    private static final long serialVersionUID = 5545470640951989469L;
    private static final String MEAN = "mean";
    private String [] coulmns;
    
    MeanImputer(String [] coulmns) {
        this.coulmns = coulmns;
    }
    
    @Override
    public String uid() {
        return "MeanImputer" + serialVersionUID;
    }

    @Override
    public Transformer copy(ParamMap arg0) {
        return null;
    }

    @Override
    public DataFrame transform(DataFrame data) {
        Map<String,String> map = new LinkedHashMap<String,String>();
        for (int i = 0 ; i < coulmns.length ; i++) {
            map.put(coulmns[i], MEAN);
        }
        
        // Generate means. Rename columns to their original names
        DataFrame meansDF = data.agg(map).toDF(this.coulmns);

        // Create a map containing column name, and the vale to be replaced with, for each column
        Map<String, Object> imputeMap = new HashMap<String,Object>();
        Row values = meansDF.first();
        for (int i = 0 ; i < values.length() ; i++) {
            imputeMap.put(meansDF.columns()[i], values.getDouble(i));
        }
        return data.na().fill(imputeMap);
    }

    @Override
    public StructType transformSchema(StructType arg0) {
        return arg0;
    }
}
