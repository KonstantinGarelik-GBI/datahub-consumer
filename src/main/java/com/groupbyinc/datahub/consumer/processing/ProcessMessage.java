package com.groupbyinc.datahub.consumer.processing;

import com.google.gson.Gson;
import com.groupbyinc.datahub.consumer.model.DatahubMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.beam.sdk.transforms.DoFn;
import org.bson.Document;

@Slf4j
@RequiredArgsConstructor
public class ProcessMessage extends DoFn<String, Document> {


    @ProcessElement
    public void processElement(ProcessContext context) {
        var element = context.element();
        try {
            Gson gson = new Gson();
            var datahubMessage = gson.fromJson(element, DatahubMessage.class);
            log.info("Processing datahub message: {}", datahubMessage);
            context.output(new Document(datahubMessage.getCollection(), datahubMessage));
        } catch (Exception e) {
            log.error("Failed to parse row: {}, {}", element, e.getMessage());
        }
    }
}
