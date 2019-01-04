package lee.fund.util.config;

import lee.fund.util.convert.StringConverter;

import java.util.HashMap;
import java.util.Map;

public class SettingMap {
    private Map<String, String> settings;

    public SettingMap() {
        this.settings = new HashMap<>();
    }

    public SettingMap(int initialCapacity) {
        this.settings = new HashMap<>(initialCapacity);
    }

    public SettingMap(Map<String, String> settings) {
        this.settings = settings;
    }

    public static SettingMap newEmpty() {
        return new SettingMap(0);
    }
    
    public void put(String key, String value) {
        settings.put(key, value);
    }

    public String getValue(String key) {
        return settings.get(key);
    }

    public String getString(String key) {
        return settings.get(key);
    }

    public String getString(String key, String defaultValue) {
        return settings.getOrDefault(key, defaultValue);
    }

    public int getInt32(String key) {
        String value = settings.get(key);
        if (value == null || value.isEmpty()) return 0;

        return Integer.parseInt(value);
    }

    public int getInt32(String key, int defaultValue) {
        String value = settings.get(key);
        if (value == null || value.isEmpty()) return defaultValue;

        return StringConverter.toInt32(value, defaultValue);
    }

    public long getInt64(String key) {
        String value = settings.get(key);
        if (value == null || value.isEmpty()) return 0;

        return Long.parseLong(value);
    }

    public long getInt64(String key, long defaultValue) {
        String value = settings.get(key);
        if (value == null || value.isEmpty()) return defaultValue;

        return StringConverter.toInt64(value, defaultValue);
    }

    public boolean getBool(String key) {
        String value = settings.get(key);
        if (value == null || value.isEmpty()) return false;

        return Boolean.parseBoolean(value);
    }

    public boolean getBool(String key, boolean defaultValue) {
        String value = settings.get(key);
        if (value == null || value.isEmpty()) return defaultValue;

        return StringConverter.toBool(value, defaultValue);
    }

    public float getFloat32(String key) {
        String value = settings.get(key);
        if (value == null || value.isEmpty()) return 0;

        return Float.parseFloat(value);
    }

    public float getFloat32(String key, float defaultValue) {
        String value = settings.get(key);
        if (value == null || value.isEmpty()) return defaultValue;

        return StringConverter.toFloat32(value, defaultValue);
    }

    public double getFloat64(String key) {
        String value = settings.get(key);
        if (value == null || value.isEmpty()) return 0;

        return Double.parseDouble(value);
    }

    public double getFloat64(String key, double defaultValue) {
        String value = settings.get(key);
        if (value == null || value.isEmpty()) return defaultValue;

        return StringConverter.toFloat64(value, defaultValue);
    }

    public int size() {
        return this.settings.size();
    }
}
