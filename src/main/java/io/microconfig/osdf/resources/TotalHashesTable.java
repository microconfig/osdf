package io.microconfig.osdf.resources;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
public class TotalHashesTable {
    private Map<String, String> hashes;

    public String get(String key) {
        if (hashes == null) return null;
        return hashes.get(key);
    }

    public void put(String key, String value) {
        if (hashes == null) {
            hashes = new HashMap<>();
        }
        hashes.put(key, value);
    }
}
