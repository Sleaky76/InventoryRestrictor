package com.sleaky.invRestrictor.api;

public class InvRestrictorProvider {

    private static InvRestrictorApi api;

    public InvRestrictorProvider() {
        throw new RuntimeException("Cannot instantiate this class");
    }

    public static InvRestrictorApi getApi() {
        if (api == null) {
            throw new IllegalStateException("Api is not available. Is the plugin loaded ?");
        }
        return api;
    }

    public static void setApi(InvRestrictorApi impl) {
        api = impl;
    }

    public static boolean isAvailable() {
        return api != null;
    }
}
