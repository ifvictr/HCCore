package com.hackclub.hccore.utils.gson;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.lang.reflect.Type;
import org.bukkit.Bukkit;
import org.bukkit.Location;

public class LocationDeserializer implements JsonDeserializer<Location> {

    @Override
    public Location deserialize(
        JsonElement source,
        Type type,
        JsonDeserializationContext context
    ) {
        JsonObject jsonObject = source.getAsJsonObject();
        String world = jsonObject.get("world").getAsString();
        double x = jsonObject.get("x").getAsDouble();
        double y = jsonObject.get("y").getAsDouble();
        double z = jsonObject.get("z").getAsDouble();

        return new Location(Bukkit.getServer().getWorld(world), x, y, z);
    }
}
