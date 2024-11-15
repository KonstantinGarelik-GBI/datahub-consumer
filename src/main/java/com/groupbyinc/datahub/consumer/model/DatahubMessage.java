package com.groupbyinc.datahub.consumer.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DatahubMessage {

    private String collection;
    private String customer;
    private String id;
    private String after;
    private Boolean active;

}
