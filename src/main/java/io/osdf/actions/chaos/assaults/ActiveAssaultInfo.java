package io.osdf.actions.chaos.assaults;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ActiveAssaultInfo {
    private String assaultName;
    private String description;
    private Map<String, String> info;
}
