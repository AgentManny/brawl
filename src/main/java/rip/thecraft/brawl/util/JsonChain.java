package rip.thecraft.brawl.util;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class JsonChain {

    private JsonObject json = new JsonObject();

    public JsonChain(JsonObject json) {
        this.json = json;
    }

    public JsonChain(String property, String value) {
        this.json.addProperty(property, value);
    }

    public JsonChain(String property, Number value) {
        this.json.addProperty(property, value);
    }

    public JsonChain(String property, Boolean value) {
        this.json.addProperty(property, value);
    }

    public JsonChain(String property, Character value) {
        this.json.addProperty(property, value);
    }

    public JsonChain(String property, JsonElement value) {
        this.json.add(property, value);
    }

    public JsonChain append(String property, String value) {
        this.json.addProperty(property, value);
        return this;
    }

    public JsonChain append(String property, Number value) {
        this.json.addProperty(property, value);
        return this;
    }

    public JsonChain append(String property, Boolean value) {
        this.json.addProperty(property, value);
        return this;
    }

    public JsonChain append(String property, Character value) {
        this.json.addProperty(property, value);
        return this;
    }

    public JsonChain append(String property, JsonElement element) {
        this.json.add(property, element);
        return this;
    }

    public JsonObject build() {
        return this.json;
    }

}