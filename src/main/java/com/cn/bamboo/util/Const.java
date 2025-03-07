package com.cn.bamboo.util;

public class Const {
    /**
     * bamboo cloud相关接口地址
     */
    public static String BAMBOO_LOGIN_API = "https://api.bambulab.cn/v1/user-service/user/login";
    public static String BAMBOO_TFA_LOGIN_API= "https://bambulab.cn/api/sign-in/tfa";
    public static String BAMBOO_EMAIL_CODE_API = "https://api.bambulab.cn/v1/user-service/user/sendemail/code";
    public static String BAMBOO_SMS_CODE_API = "https://bambulab.cn/api/v1/user-service/user/sendsmscode";
    public static String BAMBOO_BIND_API = "https://api.bambulab.cn/v1/iot-service/api/user/bind";
    public static String BAMBOO_SLICER_SETTINGS_API = "https://api.bambulab.cn/v1/iot-service/api/slicer/setting?version=1.10.0.89";
    public static String BAMBOO_TASKS_API = "https://api.bambulab.cn/v1/user-service/my/tasks";
    public static String BAMBOO_PROJECTS_API = "https://api.bambulab.cn/v1/iot-service/api/user/project";

    /**
     * mqtt相关地址及端口
     */
    public static String BAMBOO_MQTT_URL = "ssl://cn.mqtt.bambulab.com";
    public static Integer BAMBOO_MQTT_PORT = 8883;
    public static String BAMBOO_MQTT_PUBLISH = "device/{0}/request";
    public static String BAMBOO_MQTT_SUBCRIBE = "device/{0}/report";

    public static class MqttCommand{
        public static String CHAMBER_LIGHT_ON = "{\"system\": {\"sequence_id\": \"0\", \"command\": \"ledctrl\", \"led_node\": \"chamber_light\", \"led_mode\": \"on\", \"led_on_time\": 500, \"led_off_time\": 500, \"loop_times\": 0, \"interval_time\": 0}}";
        public static String CHAMBER_LIGHT_OFF = "{\"system\": {\"sequence_id\": \"0\", \"command\": \"ledctrl\", \"led_node\": \"chamber_light\", \"led_mode\": \"off\", \"led_on_time\": 500, \"led_off_time\": 500, \"loop_times\": 0, \"interval_time\": 0}}";
        public static String SPEED_PROFILE_TEMPLATE = "{\"print\": {\"sequence_id\": \"0\", \"command\": \"print_speed\", \"param\": \"\"}}";
        public static String GET_VERSION = "{\"info\": {\"sequence_id\": \"0\", \"command\": \"get_version\"}}";
        public static String PAUSE = "{\"print\": {\"sequence_id\": \"0\", \"command\": \"pause\"}}";
        public static String RESUME = "{\"print\": {\"sequence_id\": \"0\", \"command\": \"resume\"}}";
        public static String STOP = "{\"print\": {\"sequence_id\": \"0\", \"command\": \"stop\"}}";
        public static String PUSH_ALL = "{\"pushing\": {\"sequence_id\": \"0\", \"command\": \"pushall\"}}";
        public static String START_PUSH = "{\"pushing\": {\"sequence_id\": \"0\", \"command\": \"start\"}}";
        public static String SEND_GCODE_TEMPLATE = "{\"print\": {\"sequence_id\": \"0\", \"command\": \"gcode_line\", \"param\": \"\"}}";
        public static String UPGRADE_CONFIRM_TEMPLATE = "{\"upgrade\": {\"command\": \"upgrade_confirm\", \"module\": \"ota\", \"reason\": \"\", \"result\": \"success\", \"sequence_id\": \"0\", \"src_id\": 2, \"upgrade_type\": 4, \"url\": \"https://public-cdn.bblmw.com/upgrade/device/{model}/{version}/product/{hash}/{stamp}.json.sig\", \"version\": \"{version}\"}}";
        public static String PRINT_PROJECT_FILE_TEMPLATE = "{\"print\": {\"sequence_id\": 0, \"command\": \"project_file\", \"param\": \"\", \"url\": \"\", \"bed_type\": \"auto\", \"timelapse\": false, \"bed_leveling\": true, \"flow_cali\": true, \"vibration_cali\": true, \"layer_inspect\": true, \"use_ams\": false, \"ams_mapping\": [0], \"subtask_name\": \"\", \"profile_id\": \"0\", \"project_id\": \"0\", \"subtask_id\": \"0\", \"task_id\": \"0\"}}";
        public static String SKIP_OBJECTS_TEMPLATE = "{\"print\": {\"sequence_id\": \"0\", \"command\": \"skip_objects\", \"obj_list\": []}}";
        public static String SWITCH_AMS_TEMPLATE = "{\"print\": {\"command\": \"ams_change_filament\", \"sequence_id\": \"0\", \"target\": 255, \"curr_temp\": 0, \"tar_temp\": 0}}";
        public static String MOVE_AXIS_GCODE = "M211 S\nM211 X1 Y1 Z1\nM1002 push_ref_mode\nG91 \nG1 {axis}{distance}.0 F{speed}\nM1002 pop_ref_mode\nM211 R\n";
        public static String HOME_GCODE = "G28\n";
        public static String EXTRUDER_GCODE = "M83 \nG0 E{distance}.0 F900\n";
        public static String GET_ACCESSORIES = "{\"system\": {\"sequence_id\": \"0\", \"command\": \"get_accessories\", \"accessory_type\": \"none\"}}";
        public static  String PROMPT_SOUND_ENABLE = "{\"print\": {\"sequence_id\": \"0\", \"command\": \"print_option\", \"sound_enable\": true}}";
        public static  String PROMPT_SOUND_DISABLE = "{\"print\": {\"sequence_id\": \"0\", \"command\": \"print_option\", \"sound_enable\": false}}";
    }
}
