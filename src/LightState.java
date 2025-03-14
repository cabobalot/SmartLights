public class LightState {
    public enum Mode {
        CCT,
        COLOR
    }

    private final Mode mode;
    private final int brightness;
    private final int colorTemp;
    private final int hue;
    private final int saturation;

    /**
     * create in CCT mode
     * @param brightness
     * @param colorTemp
     */
    public LightState(int brightness, int colorTemp) {
        this.mode = Mode.CCT;
        this.brightness = limit(brightness, 0, 100);
        this.colorTemp = limit(colorTemp, 150, 500); // 500 = warm, 150 = cool

        this.hue = -1;
        this.saturation = -1;
    }

    /**
     * create in color mode
     * @param brightness
     * @param hue
     * @param saturation
     */
    public LightState(int brightness, int hue, int saturation) {
        this.mode = Mode.COLOR;
        this.brightness = limit(brightness, 0, 100);
        this.hue = limit(hue, 0, 360);
        this.saturation = limit(saturation, 0, 100);

        this.colorTemp = -1;
    }

    /**
     * get a string with brightness and HSV or CCT depending on mode
     */
    public String getFullString() {
        if (mode == Mode.CCT) {
            return String.format("{\"brightness\": %d, \"color_temp\": %d}", brightness, colorTemp);
        }
        else if (mode == Mode.COLOR) {
            return String.format("""
                    {"color":{"hue":%d,"saturation":%d}, "brightness":%d}""", hue, saturation, brightness);
//            {"color":{"hue":360,"saturation":100}, "brightness":100}
        }
        else { // fallback to CCT
            return String.format("{\"brightness\": %d, \"color_temp\": %d}", brightness, colorTemp);
        }
    }

    public String getHSVString() {
        return String.format("\"color\":{\"hsv\":\"%d,%d,%d\"}", hue, saturation, brightness);
        //{"color":{"hsv":"360,100,100"}}
    }

    public String getBrightnessString() {
        return String.format("\"brightness\": %d", brightness);
    }

    public String getColorTempString() {
        return String.format("\"color_temp\": %d", colorTemp);
    }

    public int getBrightness() {
        return brightness;
    }

    public int getColorTemp() {
        return colorTemp;
    }

    public int getHue() {
        return hue;
    }

    public int getSaturation() {
        return saturation;
    }

    public Mode getMode() {
        return mode;
    }


    private int limit(int in, int min, int max) {
        if (in < min) {
            return min;
        }
        if (in > max) {
            return max;
        }
        return in;
    }
}
