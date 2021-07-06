package com.hackclub.hccore.utils.gson;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import java.lang.reflect.Type;
import java.util.Map;
import org.bukkit.Location;

public class LocationMapSerializer
    implements JsonSerializer<Map<String, Location>> {

    @Override
    public JsonElement serialize(
        Map<String, Location> source,
        Type type,
        JsonSerializationContext context
    ) {
        JsonObject jsonLocationMap = new JsonObject();
        for (Map.Entry<String, Location> entry : source.entrySet()) {
            jsonLocationMap.add(
                entry.getKey(),
                context.serialize(entry.getValue())
            );
        }

        return jsonLocationMap;
    }
}
