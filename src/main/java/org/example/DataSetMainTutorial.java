package org.example;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.sql.*;
import org.apache.spark.sql.expressions.WindowSpec;
import org.example.constant.Constants;
import org.example.entity.ProductSale;
import org.example.util.DateConversionUtil;

import java.sql.Timestamp;
import java.util.Properties;

import static org.apache.spark.sql.functions.row_number;

public class DataSetMainTutorial {

    public static void main(String[] args) {

        Logger.getLogger("org.apache").setLevel(Level.WARN);

        SparkConf conf = new SparkConf().setAppName("startingSpark").setMaster("local[*]");
        JavaSparkContext sc = new JavaSparkContext(conf);
        SQLContext sqlContext = new SQLContext(sc);

        SparkSession sparkSession = SparkSession.builder().appName("testsql").master("local[*]").config("spark.sql.warehouse.dir", "file:///~/tmp").getOrCreate();

        Dataset<Row> inputData = sparkSession.read().option("header", false).text("src/main/resources/inputfile.txt");

        // generate input data with mapping
        Dataset<ProductSale> salesDataset = inputData.map(row -> {
            String s = row.getAs(0);
            String product = s.substring(0, 1);
            String code = s.substring(8, 9);
            Integer unitPrice = Integer.parseInt(s.substring(1, 2));
            Integer count = Integer.parseInt(s.substring(2, 6));
            String timeSt = s.substring(9, 32);
            Timestamp saleTimeStamp = DateConversionUtil.convertTimestamp(timeSt, Constants.TIMESTAMP_YYYY_MM_DD_HH_MM_SS_SSS);
            return new ProductSale(product, code, count, unitPrice, unitPrice * count, saleTimeStamp);
        }, Encoders.bean(ProductSale.class));

        // filter sale count greater than 4000
        Dataset<ProductSale> higher_sales = salesDataset.filter("count>4000");

        // window function to see the row number
        WindowSpec w = org.apache.spark.sql.expressions.Window
                .partitionBy("productId")
                .orderBy("unitPrice", "total");
        higher_sales.repartition(new Column("productId"));
        Dataset<Row> partitionByProduct = higher_sales.withColumn("row_num", row_number().over(w));
        partitionByProduct.show(true);

        // filter data
        Dataset<ProductSale> lowerSalesCount = salesDataset.filter("count<=4000");

        // save data to the database
        saveDataToDB(higher_sales, SaveMode.Overwrite, "product_sale_higher");
        saveDataToDB(lowerSalesCount, SaveMode.Overwrite, "product_sale_lower");
    }


    private static void saveDataToDB(Dataset data, SaveMode saveMode, String table) {
        // saving
        Properties dbProps = new Properties();

        dbProps.setProperty("connectionURL", "jdbc:mysql://localhost:3306/sale");
        dbProps.setProperty("driver", "com.mysql.cj.jdbc.Driver");
        dbProps.setProperty("user", "root");
        dbProps.setProperty("password", "password");

        String connectionURL = dbProps.getProperty("connectionURL");

        data.write().mode(saveMode)
                .jdbc(connectionURL, table, dbProps);
    }


    private static Dataset<Row> readDataFromDbTable(SQLContext sqlContext, String table) {
        Dataset<Row> dataset = sqlContext
                .read()
                .format("jdbc")
                .option("url", "jdbc:mysql://localhost:3306/sale")
                .option("driver", "com.mysql.cj.jdbc.Driver")
                .option("dbtable", table + " as A")
                .option("user", "root")
                .option("password", "password")
                .load();
        return dataset;
    }
}