package io.osdf.core.connection.context;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ClusterContextSettings {
    private ClusterType type;
}
