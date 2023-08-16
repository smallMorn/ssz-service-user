package com.ssz.user.binlog.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

@Data
@Component
@ConfigurationProperties(prefix = "binlog")
public class BinLogDbPropertys {

    private List<BinLogDbProperty> datasourceList;

}
