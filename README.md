# datahub-consumer

## Overview

The **Datahub consumer** is a Java application designed to consume change events from Debezium and propagate/replicate them into corresponding Mongo Browse collections using Apache Beam.
This application supports Google Cloud's Dataflow for scalable and efficient processing of data files, with the capability to use various runners as per the project's needs.

## Prerequisites

- **Java 21** or higher
- **Gradle 8** or higher
- A Google Cloud project with the necessary IAM permissions and a service account key.

## Dependencies

This project uses the following key libraries:

- Apache Beam (Core and Google Cloud SDK)
- SLF4J for logging
- JSON Schema Validation
- Lombok for reducing boilerplate code

## Getting Started

### Clone the Repository

```bash
git clone https://github.com/KonstantinGarelik-GBI/datahub-consumer.git
cd datahub-consumer
```

### Build the Project
To build the project, run:
```bash
./gradlew build
```
This command compiles the code, runs tests, and packages the application into an Uber JAR (including all dependencies).

### Configuration
The application configuration can be set in a config.properties file, which should include:

```properties

```

### Running the Application

On your local you can just run main class DatahubConsumerApplication. Don't forget to include programming arguments.

To run application on google flex template you can use this documentation as a starting point: https://cloud.google.com/dataflow/docs/guides/templates/using-flex-templates

To create flex template project you need to execute this command in gcp:
```bash
 gcloud dataflow flex-template build gs://dataflow6-tmp/datahub-file-producer.json \
 --image-gcr-path "us-east4-docker.pkg.dev/techlead-development/datahubproducer/datahub-file-producer:latest" \
 --sdk-language "JAVA" \
 --flex-template-base-image JAVA21 \
 --jar "datahub-file-producer-1.0.jar" \
 --env FLEX_TEMPLATE_JAVA_MAIN_CLASS="com.groupbyinc.DatahubFileProducerApplication"
```

To run the above created template you can use this command:

```bash
gcloud dataflow flex-template run "datahub-file-producer-`date +%Y%m%d-%H%M%S`" \
 --template-file-gcs-location "gs://dataflow6-tmp/datahub-file-producer.json" \
 --parameters input="gs://dataflow6-tmp/inventories_new.json" \
 --region "us-east4"
```

Don't forget to change parameters on yours in the above commands 