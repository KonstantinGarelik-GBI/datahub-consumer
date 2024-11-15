package com.groupbyinc.datahub.consumer.config;

import org.apache.beam.sdk.options.Description;
import org.apache.beam.sdk.options.PipelineOptions;

public interface ConsumerOptions extends PipelineOptions {

    @Description("GCP projectID. If not specified, the project where the dataflow works is used")
    String getProjectId();

    void setProjectId(String value);
}
