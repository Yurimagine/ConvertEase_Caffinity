package com.example.unitconversion.utils;


import java.util.HashMap;
import java.util.Map;
public class ConversionUtils {
    // Your original length units (for reference, but keep MainActivity unchanged)
    public static final String[] lengthUnits = {"cm", "m", "km", "inch", "ft"};
    // Unit maps for weight and volume
    public static final Map<String, Double> weightUnits = new HashMap<>();
    static {
        weightUnits.put("mg", 1.0);
        weightUnits.put("g", 1000.0);
        weightUnits.put("kg", 1000000.0);
        weightUnits.put("oz", 28349.5);
        weightUnits.put("lb", 453592.0);
    }
    public static final Map<String, Double> volumeUnits = new HashMap<>();
    static {
        volumeUnits.put("mL", 1.0);
        volumeUnits.put("L", 1000.0);
        volumeUnits.put("cup", 236.588);
        volumeUnits.put("pt", 473.176);
        volumeUnits.put("qt", 946.353);
        volumeUnits.put("gal", 3785.41);
    }
    // Temperature units
    public static final String[] temperatureUnits = {"°C", "°F", "K"};
    // Temperature functions
    public static double celsiusToFahrenheit(double c) { return (c * 9 / 5) + 32; }
    public static double celsiusToKelvin(double c) { return c + 273.15; }
    public static double fahrenheitToCelsius(double f) { return (f - 32) * 5 / 9; }
    public static double fahrenheitToKelvin(double f) { return (f - 32) * 5 / 9 + 273.15; }
    public static double kelvinToCelsius(double k) { return k - 273.15; }
    public static double kelvinToFahrenheit(double k) { return (k - 273.15) * 9 / 5 + 32; }
    // Unified convert (for weight, volume, temperature)
    // Unified convert (for weight, volume, temperature)
    public static double convert(double value, String fromUnit, String toUnit, String category) {
        switch (category.toLowerCase()) {
            case "weight":
                double baseValueWeight = value * weightUnits.get(fromUnit);
                return baseValueWeight / weightUnits.get(toUnit);
            case "volume":
                double baseValueVolume = value * volumeUnits.get(fromUnit);
                return baseValueVolume / volumeUnits.get(toUnit);
            case "temperature":
                double celsius = 0;
                switch (fromUnit) {
                    case "°C": celsius = value; break;
                    case "°F": celsius = fahrenheitToCelsius(value); break;
                    case "K": celsius = kelvinToCelsius(value); break;
                }
                switch (toUnit) {
                    case "°C": return celsius;
                    case "°F": return celsiusToFahrenheit(celsius);
                    case "K": return celsiusToKelvin(celsius);
                }
            default:
                return value;
        }
    }
    // Your original convertLength (for reference)
    public static double convertLength(double value, String fromUnit, String toUnit) {
        double inMeters = 0;
        switch (fromUnit) {
            case "cm": inMeters = value / 100; break;
            case "km": inMeters = value * 1000; break;
            case "inch": inMeters = value * 0.0254; break;
            case "ft": inMeters = value * 0.3048; break;
            default: inMeters = value;
        }
        switch (toUnit) {
            case "cm": return inMeters * 100;
            case "km": return inMeters / 1000;
            case "inch": return inMeters / 0.0254;
            case "ft": return inMeters / 0.3048;
            default: return inMeters;
        }
    }
}