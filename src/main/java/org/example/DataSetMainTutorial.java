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
import java.util.Scanner;

import static org.apache.spark.sql.functions.row_number;

public class DataSetMainTutorial {

    public static void main(String[] args) {

        Logger.getLogger("org.apache").setLevel(Level.WARN);

        SparkConf conf = new SparkConf().setAppName("startingSpark");//.setMaster("local[*]");
        JavaSparkContext sc = new JavaSparkContext(conf);
        SQLContext sqlContext = new SQLContext(sc);
        SparkSession sparkSession = SparkSession.builder().appName("testsql").master("local[*]").config("spark.sql.warehouse.dir", "file:///~/tmp").getOrCreate();

        Encoder<ProductSale> productSaleEncoder = Encoders.bean(ProductSale.class);

        Dataset<Row> inputData = sparkSession.read().option("header", false).text("./inputfile_2.txt");
//        inputData.show();



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
        }, Encoders.bean(ProductSale.class)).as("sales");


        // window function to see the row number
        WindowSpec w = org.apache.spark.sql.expressions.Window
                .partitionBy("productId")
                .orderBy("productId");
        Dataset<ProductSale> formattedData = salesDataset
                .repartition(new Column("productId"), new Column("productCode"))
                .withColumn("row_num", row_number().over(w)).as(productSaleEncoder);

        // read product for joining
        Dataset<Row> productDataset = readDataFromDbTable(sqlContext, "(select productId, productCode from product)").repartition(new Column("productId"));

        //filter data only in product table
        Dataset<ProductSale> filterDataWhichInProduct = formattedData
                .join(productDataset,
                        formattedData.col("productId").equalTo(productDataset.as("product").col("productId"))
                                .and( formattedData.col("productCode").equalTo(productDataset.as("product").col("productCode"))), "inner")
                .select(formattedData.col("count"),
                        formattedData.col("productCode"),
                        formattedData.col("productId"),
                        formattedData.col("saleTimeStamp"),
                        formattedData.col("total"),
                        formattedData.col("unitPrice"))
                .as(productSaleEncoder);


        System.out.println("Number of partitions: "+ formattedData.rdd().getNumPartitions());

        // filter sale count greater than 4000
        Dataset<ProductSale> higher_sales = filterDataWhichInProduct.filter("count>4000").as(productSaleEncoder);

        // filter data
        Dataset<ProductSale> lowerSalesCount = filterDataWhichInProduct.filter("count<=4000");



        // save data to the database
        higher_sales.explain();
        lowerSalesCount.explain();
//        saveDataToDB(higher_sales, SaveMode.Overwrite, "product_sale_higher");
//        saveDataToDB(lowerSalesCount, SaveMode.Overwrite, "product_sale_lower");
//        Scanner scanner = new Scanner(System.in);
//        scanner.nextLine();
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


    private static Dataset readDataFromDbTable(SQLContext sqlContext, String table) {
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