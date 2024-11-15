package com.groupbyinc.datahub.consumer.pipeline;

import com.google.cloud.ServiceOptions;
import com.google.pubsub.v1.ProjectSubscriptionName;
import com.groupbyinc.datahub.consumer.config.ApplicationConfig;
import com.groupbyinc.datahub.consumer.config.ConsumerOptions;
import com.groupbyinc.datahub.consumer.processing.ProcessMessage;
import com.groupbyinc.datahub.consumer.producer.WriteToMongo;
import lombok.extern.slf4j.Slf4j;
import org.apache.beam.sdk.Pipeline;
import org.apache.beam.sdk.io.gcp.pubsub.PubsubIO;
import org.apache.beam.sdk.options.PipelineOptionsFactory;
import org.apache.beam.sdk.transforms.ParDo;

import java.io.Serializable;

@Slf4j
public class DatahubPipeline implements Serializable {

    private static final long serialVersionUID = 1L;

    public Pipeline getDatahubPipeline(String[] args) {
        ConsumerOptions options = PipelineOptionsFactory.fromArgs(args)
                .withoutStrictParsing()
                .as(ConsumerOptions.class);

        log.info("Preparing datahub pipeline with next options: {}", options);
        String projectId = options.getProjectId();
        if (projectId == null || projectId.isEmpty()) {
            projectId = ServiceOptions.getDefaultProjectId();
        }
        log.info("ProjectId: {}", projectId);

        ApplicationConfig applicationConfig = ApplicationConfig.getInstance("config.properties",
            projectId);

        log.debug("Application config: {}", applicationConfig.toString());

        var subscription = ProjectSubscriptionName
            .of(projectId, applicationConfig.getProperty("DATAHUB_TOPIC_NAME")).toString();

        var mongoURI = applicationConfig.getProperty("CONSUMER_MONGODB_URI");
        var mongoDatabaseName = applicationConfig.getProperty("CONSUMER_MONGODB_DATABASE");

        Pipeline pipeline = Pipeline.create(options);
        pipeline
                .apply("Read from PubSub", PubsubIO.readStrings().fromSubscription(subscription))
                .apply("Processing", ParDo.of(new ProcessMessage()))
                .apply("Publish to Mongo", ParDo.of(new WriteToMongo(mongoURI, mongoDatabaseName)));
        return pipeline;
    }

}