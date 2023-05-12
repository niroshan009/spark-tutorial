





# Use an appropriate base image with Java and Spark pre-installed
FROM apache/spark:latest

# Set the working directory
WORKDIR /app

# Copy the Spark application JAR to the container
COPY ./target/spark-tutorial-1.0-SNAPSHOT.jar /opt/spark/work-dir/app.jar
COPY ./target/inputfile_2.txt /opt/spark/work-dir/inputfile_2.txt


# Add any additional dependencies or files your application requires
#COPY additional_files /app/additional_files

# Set the entry point for running the Spark application
ENTRYPOINT ["spark-submit", "--class", "org.example.DataSetMainTutorial", "app.jar"]
