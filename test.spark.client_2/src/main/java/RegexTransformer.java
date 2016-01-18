import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import org.apache.spark.ml.Transformer;
import org.apache.spark.ml.param.ParamMap;
import org.apache.spark.sql.Column;
import org.apache.spark.sql.DataFrame;
import org.apache.spark.sql.functions;
import org.apache.spark.sql.types.DataTypes;
import org.apache.spark.sql.types.StructType;

public class RegexTransformer extends Transformer {

    private static final long serialVersionUID = 5545470640951989469L;
    private static final String REGEX_TRANSFORMER_UDF = "regexMapper";
    private static final String TRANSFORMED_COLUMN = "Topic";
    private Map<Pattern, String> regexMap = new HashMap<Pattern, String>();
    private String column;
    
    RegexTransformer(String column, Map<String, String> regexMap) {
        this.column = column;
        for (String regexString : regexMap.keySet()) {
            this.regexMap.put(Pattern.compile(regexString), regexMap.get(regexString));
        }
        
    }
    
    @Override
    public String uid() {
        return "CustomTransformer" + serialVersionUID;
    }

    @Override
    public Transformer copy(ParamMap arg0) {
        return null;
    }

    /**
     * Transform using a UDF
     * 
     * @param df    Dataframe that needed to be transformed using the regex.
     * @return      Dataframe with the transformed column.
     */
    @Override
    public DataFrame transform(DataFrame df) {
        //TODO: check null for regexMap
        df.sqlContext().udf().register(REGEX_TRANSFORMER_UDF, (String str) -> { return findTopic(str); }, DataTypes.StringType);
        Column col = df.col(column);
        col = functions.callUDF(REGEX_TRANSFORMER_UDF, col);
        return df.withColumn(TRANSFORMED_COLUMN, col);
    }

    @Override
    public StructType transformSchema(StructType arg0) {
        return arg0;
    }
    
    
    /**
     * Retrieve the topic matching to the regex. if not found, return the original string.
     *  
     * @param str   String to be matched.
     * @return      Matching topic. If not found, original string
     */
    private String findTopic(String str) {
        for (Pattern pattern : this.regexMap.keySet()) {
            if (pattern.matcher(str).matches()) {
                return this.regexMap.get(pattern);
            }
        }
        return str;
    }
    
    
    /* Using SQL regex-replace function */
   /* private DataFrame method_1(DataFrame df) {
        // LIMIT: only 200 pairs
        Column col = df.col(column);
        for (Pattern key : regexMap.keySet()) {
            col = functions.regexp_replace(col, key.toString(), regexMap.get(key));
        }
        return df.withColumn("Topic" , col);
    }*/
}