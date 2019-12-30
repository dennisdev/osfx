package dev.dennis.osfx.mixin;

import dev.dennis.osfx.api.Widget;
import dev.dennis.osfx.inject.mixin.Getter;
import dev.dennis.osfx.inject.mixin.Mixin;

@Mixin("Widget")
public abstract class WidgetMixin implements Widget {
    @Getter("width")
    @Override
    public abstract int getWidth();
}
