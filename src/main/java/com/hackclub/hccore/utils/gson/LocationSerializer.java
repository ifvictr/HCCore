package com.hackclub.hccore.utils.gson;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import java.lang.reflect.Type;
import org.bukkit.Location;

public class LocationSerializer implements JsonSerializer<Location> {

    @Override
    public JsonElement serialize(
        Location source,
        Type type,
        JsonSerializationContext context
    ) {
        JsonObject jsonLocation = new JsonObject();
        jsonLocation.addProperty("world", source.getWorld().getName());
        jsonLocation.addProperty("x", source.getX());
        jsonLocation.addProperty("y", source.getY());
        jsonLocation.addProperty("z", source.getZ());

        return jsonLocation;
    }
}
