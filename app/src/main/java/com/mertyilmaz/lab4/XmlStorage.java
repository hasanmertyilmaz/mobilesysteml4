package com.mertyilmaz.lab4;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;
import org.xmlpull.v1.XmlSerializer;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * XML persistence for the vehicle database.
 *
 * File format (same as the assignment example):
 *   <vehicles>
 *     <car><name>Honda Civic</name><fuelType>3</fuelType></car>
 *     <bicycle><name>Giant</name></bicycle>
 *     ...
 *   </vehicles>
 *
 * MANDATORY fuelType mapping rule, used 1:1 for reading AND writing:
 * the integer stored in XML IS the fuel bitmask of CombustionVehicle:
 *   DIESEL = 1, PETROL = 2, LPG = 4, CNG = 8,
 *   and sums are combinations (3 = DIESEL|PETROL, 5 = DIESEL|LPG, ...).
 * Non-combustion vehicles (bicycle, scooter) have no fuelType element.
 *
 * Extension (documented): on save we also write <fuelAmount> for combustion
 * vehicles so the fuel state survives a restart; when the element is missing
 * (e.g. in the initial database) the amount defaults to 0.
 */
public final class XmlStorage {

    private XmlStorage() {
    }

    /** Loads vehicles; every vehicle gets its unique ID in the Vehicle constructor. */
    public static List<Vehicle> load(InputStream in) throws Exception {
        List<Vehicle> result = new ArrayList<>();
        XmlPullParser parser = XmlPullParserFactory.newInstance().newPullParser();
        parser.setInput(in, "UTF-8");

        String vehicleTag = null;
        String field = null;
        String name = null;
        int fuelType = 0;
        double fuelAmount = 0;

        for (int event = parser.getEventType();
                event != XmlPullParser.END_DOCUMENT; event = parser.next()) {
            switch (event) {
                case XmlPullParser.START_TAG:
                    String tag = parser.getName();
                    if (isVehicleTag(tag)) {
                        vehicleTag = tag;
                        name = null;
                        fuelType = 0;
                        fuelAmount = 0;
                    } else {
                        field = tag;
                    }
                    break;

                case XmlPullParser.TEXT:
                    if (vehicleTag != null && field != null) {
                        String text = parser.getText().trim();
                        if (!text.isEmpty()) {
                            if ("name".equals(field)) {
                                name = text;
                            } else if ("fuelType".equals(field)) {
                                fuelType = Integer.parseInt(text);
                            } else if ("fuelAmount".equals(field)) {
                                fuelAmount = Double.parseDouble(text);
                            }
                        }
                    }
                    break;

                case XmlPullParser.END_TAG:
                    if (isVehicleTag(parser.getName()) && vehicleTag != null && name != null) {
                        result.add(create(vehicleTag, name, fuelType, fuelAmount));
                        vehicleTag = null;
                    }
                    field = null;
                    break;
            }
        }
        return result;
    }

    /** Saves all vehicles, preserving type, name and fuelType (+ fuelAmount). */
    public static void save(OutputStream out, List<Vehicle> vehicles) throws Exception {
        XmlSerializer xml = XmlPullParserFactory.newInstance().newSerializer();
        xml.setOutput(out, "UTF-8");
        xml.setFeature("http://xmlpull.org/v1/doc/features.html#indent-output", true);
        xml.startDocument("UTF-8", true);
        xml.startTag(null, "vehicles");
        for (Vehicle v : vehicles) {
            String tag = tagFor(v);
            xml.startTag(null, tag);
            xml.startTag(null, "name").text(v.getName()).endTag(null, "name");
            if (v instanceof CombustionVehicle) {
                CombustionVehicle c = (CombustionVehicle) v;
                xml.startTag(null, "fuelType")
                        .text(String.valueOf(c.getSupportedFuelMask()))
                        .endTag(null, "fuelType");
                xml.startTag(null, "fuelAmount")
                        .text(String.valueOf(c.getFuelAmount()))
                        .endTag(null, "fuelAmount");
            }
            xml.endTag(null, tag);
        }
        xml.endTag(null, "vehicles");
        xml.endDocument();
        out.flush();
    }

    private static boolean isVehicleTag(String tag) {
        return "car".equals(tag) || "motorboat".equals(tag)
                || "bicycle".equals(tag) || "scooter".equals(tag);
    }

    private static Vehicle create(String tag, String name, int fuelType, double fuelAmount) {
        switch (tag) {
            case "car": {
                Car car = new Car(name, fuelType);
                car.setFuelAmount(fuelAmount);
                return car;
            }
            case "motorboat": {
                Motorboat boat = new Motorboat(name, fuelType);
                boat.setFuelAmount(fuelAmount);
                return boat;
            }
            case "bicycle":
                return new Bicycle(name);
            default:
                return new Scooter(name);
        }
    }

    private static String tagFor(Vehicle v) {
        if (v instanceof Car) return "car";
        if (v instanceof Motorboat) return "motorboat";
        if (v instanceof Bicycle) return "bicycle";
        return "scooter";
    }
}
