package org.example;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.example.constant.Constants;
import org.example.entity.ProductSale;
import org.example.entity.SaleId;
import org.example.util.DateConversionUtil;
import scala.Tuple2;

import java.sql.Timestamp;

public class PairRddMain {

    public static void main(String[] args) {
        Logger.getLogger("org.apache").setLevel(Level.WARN);

        SparkConf conf = new SparkConf().setAppName("startingSpark").setMaster("local[*]");
        JavaSparkContext sc = new JavaSparkContext(conf);

        String pathToFile = "src/main/resources/inputfile.txt";

        JavaRDD<String> data = sc.textFile(pathToFile);

        JavaPairRDD<SaleId, ProductSale> salesPairRdd = data.mapToPair(row -> {
            String s = row;
            String product = s.substring(0, 1);
            String code = s.substring(8, 9);
            Integer unitPrice = Integer.parseInt(s.substring(1, 2));
            Integer count = Integer.parseInt(s.substring(2, 6));
            String timeSt = s.substring(9, 32);
            Timestamp saleTimeStamp = DateConversionUtil.convertTimestamp(timeSt, Constants.TIMESTAMP_YYYY_MM_DD_HH_MM_SS_SSS);
            SaleId saleId = new SaleId(product, code, saleTimeStamp);
            ProductSale productSale = new ProductSale(product, code, count, unitPrice, count * unitPrice);
            return new Tuple2<SaleId, ProductSale>(saleId, productSale);
        });


        JavaPairRDD<Tuple2<SaleId, ProductSale>, Long> ss = salesPairRdd.zipWithIndex();

        salesPairRdd.collect().forEach(e -> {
            SaleId s = e._1();
            System.out.println(s.getProductId() + "-" + s.getProductCode() + "-" + s.getSaleTimeSatamp());
        });
    }
}
