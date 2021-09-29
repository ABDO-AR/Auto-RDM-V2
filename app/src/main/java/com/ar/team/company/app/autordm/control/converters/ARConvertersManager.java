package com.ar.team.company.app.autordm.control.converters;

import com.google.gson.InstanceCreator;

import java.lang.reflect.Type;

@SuppressWarnings("unused")
public class ARConvertersManager {

    // Converter(CharSequence):
    public static class CharSequenceConverter implements InstanceCreator<CharSequence> {
        @Override // InstanceCreatorOfCharSequence:
        public CharSequence createInstance(Type type) {
            return "{aaa}";
        }
    }
}
