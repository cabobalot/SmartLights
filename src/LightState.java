public class LightState {
    public enum Mode {
        CCT,
        COLOR,
        NIGHT
    }

    private Mode mode = Mode.CCT;
    private int brightness = 80;
    private int colorTemp = 325;
    private int hue = 1;
    private int saturation = 100;

    /**
     * get a string with brightness and HSV or CCT depending on mode
     */
    public String getFullString() {
        if (mode == Mode.CCT) {
            return String.format("{\"brightness\": %d, \"color_temp\": %d}", brightness, colorTemp);
        } else if (mode == Mode.COLOR) {
            return String.format("""
                    {"color":{"hue":%d,"saturation":%d}, "brightness":%d}""", hue, saturation, brightness);
//            {"color":{"hue":360,"saturation":100}, "brightness":100}
        } else if (mode == Mode.NIGHT) {
            // honor brightness, but set color to 100% saturated red
            return String.format("""
                    {"color":{"hue":%d,"saturation":%d}, "brightness":%d}""", 360, 100, brightness);
        } else { // fallback to CCT
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

    public void setBrightness(int brightness) {
        this.brightness = limit(brightness, 0, 100);
    }

    public int getColorTemp() {
        return colorTemp;
    }

    // 500 = warm, 150 = cool
    public void setColorTemp(int colorTemp) {
        this.colorTemp = limit(colorTemp, 150, 500);
    }

    public int getHue() {
        return hue;
    }

    public void setHue(int hue) {
        this.hue = limit(hue, 0, 360);
    }

    public int getSaturation() {
        return saturation;
    }

    public void setSaturation(int saturation) {
        this.saturation = limit(saturation, 0, 100);
    }

    public Mode getMode() {
        return mode;
    }

    /**
     * only allows changing mode according to state machine logic
     * @param mode
     */
    public void setMode(Mode mode) {
        if (this.mode != Mode.NIGHT) {
            this.mode = mode;
        }
    }

    /**
     * use to exit night mode
     * @param mode
     */
    public void forceSetMode(Mode mode) {
        this.mode = mode;
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
