package com.groupbyinc.datahub.consumer;

import com.groupbyinc.datahub.consumer.pipeline.DatahubPipeline;
import org.apache.beam.sdk.Pipeline;

public class DatahubConsumerApplication {

	public static void main(String[] args) {
		DatahubPipeline datahubPipeline = new DatahubPipeline();
		Pipeline pipeline = datahubPipeline.getDatahubPipeline(args);

		// For a Dataflow Flex Template, do NOT call waitUntilFinish().
		pipeline.run();
	}

}
