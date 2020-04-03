package com.haroldstudios.protectionapi.protection;

public abstract class UniversalProtection implements Protection {

    @Override
    public boolean isEnabled() {
        return false;
    }


}
