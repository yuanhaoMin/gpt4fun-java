package com.rua.constant;

import lombok.Getter;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

@Getter
public enum ChamberUserAccessLevelEnum {

    NORMAL(1), //
    GPT4(2), //
    AUDIO(4);

    private final int accessLevel;

    ChamberUserAccessLevelEnum(final int accessLevel) {
        this.accessLevel = accessLevel;
    }

    public boolean hasAccess(int accessBitmap) {
        return (accessBitmap & this.accessLevel) == this.accessLevel;
    }

    public static Set<ChamberUserAccessLevelEnum> getAccessLevelsFromBitmap(int accessBitmap) {
        return Arrays.stream(values()) //
                .filter(accessEnum -> accessEnum.hasAccess(accessBitmap)) //
                .collect(Collectors.toSet());
    }

}