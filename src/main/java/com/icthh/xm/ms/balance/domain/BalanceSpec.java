package com.icthh.xm.ms.balance.domain;

import java.util.List;
import java.util.Map;

import lombok.Data;

@Data
public class BalanceSpec {

    private List<BalanceTypeSpec> types;

    @Data
    public static class BalanceTypeSpec {
        private String key;
        private Map<String, String> name;
        private boolean isWithPockets;
        private boolean removeZeroPockets = false;
    }

}
