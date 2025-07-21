# turns on the light with a new color, without the little blip of the old color. sleeping is the only way I guess :(
mosquitto_pub -h "192.168.1.25" -t "zigbee2mqtt/Kitchen/set" -m '{"color":{"hue":100,"saturation":100}, "brightness":0}'
sleep 0.5
mosquitto_pub -h "192.168.1.25" -t "zigbee2mqtt/Kitchen/set" -m '{"color":{"hue":100,"saturation":100}, "brightness":100}'

