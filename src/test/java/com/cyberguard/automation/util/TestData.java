package com.cyberguard.automation.util;

public final class TestData {


    public static final String ADMIN_USERNAME       = "admin@cyberguard.com";
    public static final String ADMIN_PASSWORD       = "AdminSofka123456";
    public static final String ANALYST_USERNAME     = "soc@cyberguard.com";
    public static final String ANALYST_PASSWORD     = "SocSofka123456";


    public static final String VALID_SOURCE_IP      = "192.168.1.100";

    private static final String RUN_SUFFIX          = String.valueOf(System.currentTimeMillis());
    public static final String NEW_USER_EMAIL       = "auto.test." + RUN_SUFFIX + "@cyberguard.com";
    public static final String NEW_USER_FULL_NAME   = "Auto Test Usuario";
    public static final String NEW_USER_USERNAME    = "auto.test." + RUN_SUFFIX;
    public static final String NEW_USER_ROLE        = "soc_analyst";


    public static final String DUPLICATE_EMAIL      = "admin@cyberguard.com";


    public static final String ROLE_ADMIN           = "admin";
    public static final String ROLE_SOC_ANALYST     = "soc_analyst";
    public static final String ROLE_HANDLER         = "incident_handler";

    private TestData() {
    }
}
