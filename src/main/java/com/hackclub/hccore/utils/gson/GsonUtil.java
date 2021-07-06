package com.hackclub.hccore.utils.gson;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.Map;
import org.bukkit.Location;

public class GsonUtil {
    private static Gson gson;

    public static Gson getInstance() {
        if (gson == null) {
            // Register custom types
            Type locationMapType = new TypeToken<Map<String, Location>>() {}
            .getType();

            gson =
                new GsonBuilder()
                    .excludeFieldsWithoutExposeAnnotation()
                    .setPrettyPrinting()
                    .serializeNulls()
                    .registerTypeAdapter(
                        Location.class,
                        new LocationSerializer()
                    )
                    .registerTypeAdapter(
                        locationMapType,
                        new LocationMapDeserializer()
                    )
                    .registerTypeAdapter(
                        locationMapType,
                        new LocationMapSerializer()
                    )
                    .registerTypeAdapter(
                        Location.class,
                        new LocationDeserializer()
                    )
                    .create();
        }

        return gson;
    }
}
