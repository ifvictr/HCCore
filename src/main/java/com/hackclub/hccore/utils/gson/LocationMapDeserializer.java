package com.hackclub.hccore.utils.gson;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.lang.reflect.Type;
import java.util.LinkedHashMap;
import java.util.Map;
import org.bukkit.Location;

public class LocationMapDeserializer
    implements JsonDeserializer<Map<String, Location>> {

    @Override
    public Map<String, Location> deserialize(
        JsonElement source,
        Type type,
        JsonDeserializationContext context
    ) {
        JsonObject jsonObject = source.getAsJsonObject();
        Map<String, Location> locationMap = new LinkedHashMap<>();
        for (Map.Entry<String, JsonElement> entry : jsonObject.entrySet()) {
            locationMap.put(
                entry.getKey(),
                context.deserialize(entry.getValue(), Location.class)
            );
        }

        return locationMap;
    }
}
