package org.example;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.sql.*;
import org.example.entity.ProductSale;

import java.util.Properties;

public class Main {
    public static void main(String[] args) {

        Logger.getLogger("org.apache").setLevel(Level.WARN);

        SparkConf conf = new SparkConf().setAppName("startingSpark").setMaster("local[*]");
        JavaSparkContext sc = new JavaSparkContext(conf);


        SparkSession sparkSession = SparkSession.builder().appName("testsql").master("local[*]").config("spark.sql.warehouse.dir", "file:///~/tmp").getOrCreate();


        Dataset<Row> inputData = sparkSession.read().option("header", false).text("src/main/resources/inputfile.txt");


        Dataset<ProductSale> salesDataset = inputData.map(row -> {

            String s = row.getAs(0);
            String product = s.substring(0, 1);
            Integer unitPrice = Integer.parseInt(s.substring(1, 2));
            Integer count = Integer.parseInt(s.substring(2, 6));
            return new ProductSale(product, count, unitPrice, unitPrice * count);
        }, Encoders.bean(ProductSale.class));


        Dataset<ProductSale> higher_sales = salesDataset.filter("count>4000");
        Dataset<ProductSale> lowerSalesCount = salesDataset.filter("count<=4000");
        lowerSalesCount.show();
        higher_sales.show();


        SQLContext sqlContext = new SQLContext(sc);

        Dataset<Row> df = sqlContext
                .read()
                .format("jdbc")
                .option("url", "jdbc:mysql://localhost:3306/sale")
                .option("driver", "com.mysql.cj.jdbc.Driver")
                .option("dbtable", "product_sale_lower")
                .option("user", "root")
                .option("password", "password")
                .load();

        df.show();


        Properties dbProps = new Properties();

        dbProps.setProperty("connectionURL", "jdbc:mysql://localhost:3306/sale");
        dbProps.setProperty("driver", "com.mysql.cj.jdbc.Driver");
        dbProps.setProperty("user", "root");
        dbProps.setProperty("password", "password");

        String connectionURL = dbProps.getProperty("connectionURL");

        higher_sales.write().mode(SaveMode.Overwrite)
                .jdbc(connectionURL, "product_sale_higher", dbProps);

        lowerSalesCount.write().mode(SaveMode.Overwrite)
                .jdbc(connectionURL, "product_sale_lower", dbProps);

    }
}