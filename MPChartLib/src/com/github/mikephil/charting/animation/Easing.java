
package com.github.mikephil.charting.animation;

/**
 * Easing options.
 * 
 * @author Daniel Cohen Gindi
 */
public class Easing {

    /**
     * Use EasingOption instead of EasingFunction to avoid crashes below Android
     * 3.0
     */
    public enum EasingOption {
        Linear,
        EaseInQuad,
        EaseOutQuad,
        EaseInOutQuad,
        EaseInCubic,
        EaseOutCubic,
        EaseInOutCubic,
        EaseInQuart,
        EaseOutQuart,
        EaseInOutQuart,
        EaseInSine,
        EaseOutSine,
        EaseInOutSine,
        EaseInExpo,
        EaseOutExpo,
        EaseInOutExpo,
        EaseInCirc,
        EaseOutCirc,
        EaseInOutCirc,
        EaseInElastic,
        EaseOutElastic,
        EaseInOutElastic,
        EaseInBack,
        EaseOutBack,
        EaseInOutBack,
        EaseInBounce,
        EaseOutBounce,
        EaseInOutBounce,
    }

    public static EasingFunction getEasingFunctionFromOption(EasingOption easing) {
        switch (easing) {
            default:
            case Linear:
                return EasingFunction.Linear;
            case EaseInQuad:
                return EasingFunction.EaseInQuad;
            case EaseOutQuad:
                return EasingFunction.EaseOutQuad;
            case EaseInOutQuad:
                return EasingFunction.EaseInOutQuad;
            case EaseInCubic:
                return EasingFunction.EaseInCubic;
            case EaseOutCubic:
                return EasingFunction.EaseOutCubic;
            case EaseInOutCubic:
                return EasingFunction.EaseInOutCubic;
            case EaseInQuart:
                return EasingFunction.EaseInQuart;
            case EaseOutQuart:
                return EasingFunction.EaseOutQuart;
            case EaseInOutQuart:
                return EasingFunction.EaseInOutQuart;
            case EaseInSine:
                return EasingFunction.EaseInSine;
            case EaseOutSine:
                return EasingFunction.EaseOutSine;
            case EaseInOutSine:
                return EasingFunction.EaseInOutSine;
            case EaseInExpo:
                return EasingFunction.EaseInExpo;
            case EaseOutExpo:
                return EasingFunction.EaseOutExpo;
            case EaseInOutExpo:
                return EasingFunction.EaseInOutExpo;
            case EaseInCirc:
                return EasingFunction.EaseInCirc;
            case EaseOutCirc:
                return EasingFunction.EaseOutCirc;
            case EaseInOutCirc:
                return EasingFunction.EaseInOutCirc;
            case EaseInElastic:
                return EasingFunction.EaseInElastic;
            case EaseOutElastic:
                return EasingFunction.EaseOutElastic;
            case EaseInOutElastic:
                return EasingFunction.EaseInOutElastic;
            case EaseInBack:
                return EasingFunction.EaseInBack;
            case EaseOutBack:
                return EasingFunction.EaseOutBack;
            case EaseInOutBack:
                return EasingFunction.EaseInOutBack;
            case EaseInBounce:
                return EasingFunction.EaseInBounce;
            case EaseOutBounce:
                return EasingFunction.EaseOutBounce;
            case EaseInOutBounce:
                return EasingFunction.EaseInOutBounce;
        }
    }
}
