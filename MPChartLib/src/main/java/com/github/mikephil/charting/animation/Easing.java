
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
                return Easing.EasingFunctions.Linear;
            case EaseInQuad:
                return Easing.EasingFunctions.EaseInQuad;
            case EaseOutQuad:
                return Easing.EasingFunctions.EaseOutQuad;
            case EaseInOutQuad:
                return Easing.EasingFunctions.EaseInOutQuad;
            case EaseInCubic:
                return Easing.EasingFunctions.EaseInCubic;
            case EaseOutCubic:
                return Easing.EasingFunctions.EaseOutCubic;
            case EaseInOutCubic:
                return Easing.EasingFunctions.EaseInOutCubic;
            case EaseInQuart:
                return Easing.EasingFunctions.EaseInQuart;
            case EaseOutQuart:
                return Easing.EasingFunctions.EaseOutQuart;
            case EaseInOutQuart:
                return Easing.EasingFunctions.EaseInOutQuart;
            case EaseInSine:
                return Easing.EasingFunctions.EaseInSine;
            case EaseOutSine:
                return Easing.EasingFunctions.EaseOutSine;
            case EaseInOutSine:
                return Easing.EasingFunctions.EaseInOutSine;
            case EaseInExpo:
                return Easing.EasingFunctions.EaseInExpo;
            case EaseOutExpo:
                return Easing.EasingFunctions.EaseOutExpo;
            case EaseInOutExpo:
                return Easing.EasingFunctions.EaseInOutExpo;
            case EaseInCirc:
                return Easing.EasingFunctions.EaseInCirc;
            case EaseOutCirc:
                return Easing.EasingFunctions.EaseOutCirc;
            case EaseInOutCirc:
                return Easing.EasingFunctions.EaseInOutCirc;
            case EaseInElastic:
                return Easing.EasingFunctions.EaseInElastic;
            case EaseOutElastic:
                return Easing.EasingFunctions.EaseOutElastic;
            case EaseInOutElastic:
                return Easing.EasingFunctions.EaseInOutElastic;
            case EaseInBack:
                return Easing.EasingFunctions.EaseInBack;
            case EaseOutBack:
                return Easing.EasingFunctions.EaseOutBack;
            case EaseInOutBack:
                return Easing.EasingFunctions.EaseInOutBack;
            case EaseInBounce:
                return Easing.EasingFunctions.EaseInBounce;
            case EaseOutBounce:
                return Easing.EasingFunctions.EaseOutBounce;
            case EaseInOutBounce:
                return Easing.EasingFunctions.EaseInOutBounce;
        }
    }
    
    private static class EasingFunctions {
        
        /**
         * ########## ########## ########## ########## ########## ##########
         * PREDEFINED EASING FUNCTIONS BELOW THIS
         */

        public static final EasingFunction Linear = new EasingFunction() {
            // @Override
            // public float ease(long elapsed, long duration) {
            // return elapsed / (float) duration;
            // }

            @Override
            public float getInterpolation(float input) {
                return input;
            }
        };

        public static final EasingFunction EaseInQuad = new EasingFunction() {
            // @Override
            // public float ease(long elapsed, long duration) {
            // float position = elapsed / (float) duration;
            // return position * position;
            // }

            @Override
            public float getInterpolation(float input) {
                return input * input;
            }
        };

        public static final EasingFunction EaseOutQuad = new EasingFunction() {
            // @Override
            // public float ease(long elapsed, long duration) {
            // float position = elapsed / (float) duration;
            // return -position * (position - 2.f);
            // }

            @Override
            public float getInterpolation(float input) {
                return -input * (input - 2f);
            }
        };

        public static final EasingFunction EaseInOutQuad = new EasingFunction() {
            // @Override
            // public float ease(long elapsed, long duration) {
            // float position = elapsed / (duration / 2.f);
            // if (position < 1.f)
            // {
            // return 0.5f * position * position;
            // }
            // return -0.5f * ((--position) * (position - 2.f) - 1.f);
            // }

            @Override
            public float getInterpolation(float input) {

                float position = input / 0.5f;

                if (position < 1.f) {
                    return 0.5f * position * position;
                }

                return -0.5f * ((--position) * (position - 2.f) - 1.f);
            }
        };

        public static final EasingFunction EaseInCubic = new EasingFunction() {
            // @Override
            // public float ease(long elapsed, long duration) {
            // float position = elapsed / (float) duration;
            // return position * position * position;
            // }

            @Override
            public float getInterpolation(float input) {
                return input * input * input;
            }
        };

        public static final EasingFunction EaseOutCubic = new
                EasingFunction() {
                    // @Override
                    // public float ease(long elapsed, long duration) {
                    // float position = elapsed / (float) duration;
                    // position--;
                    // return (position * position * position + 1.f);
                    // }

                    @Override
                    public float getInterpolation(float input) {
                        input--;
                        return (input * input * input + 1.f);
                    }
                };

        public static final EasingFunction EaseInOutCubic = new
                EasingFunction() {
                    // @Override
                    // public float ease(long elapsed, long duration) {
                    // float position = elapsed / (duration / 2.f);
                    // if (position < 1.f)
                    // {
                    // return 0.5f * position * position * position;
                    // }
                    // position -= 2.f;
                    // return 0.5f * (position * position * position + 2.f);
                    // }

                    @Override
                    public float getInterpolation(float input) {

                        float position = input / 0.5f;
                        if (position < 1.f) {
                            return 0.5f * position * position * position;
                        }
                        position -= 2.f;
                        return 0.5f * (position * position * position + 2.f);
                    }
                };

        public static final EasingFunction EaseInQuart = new EasingFunction() {

            public float getInterpolation(float input) {
                return input * input * input * input;
            }
        };

        public static final EasingFunction EaseOutQuart = new EasingFunction() {

            public float getInterpolation(float input) {
                input--;
                return -(input * input * input * input - 1f);
            }
        };

        public static final EasingFunction EaseInOutQuart = new
                EasingFunction() {
                    @Override
                    public float getInterpolation(float input) {
                        float position = input / 0.5f;
                        if (position < 1.f) {
                            return 0.5f * position * position * position * position;
                        }
                        position -= 2.f;
                        return -0.5f * (position * position * position * position - 2.f);
                    }
                };

        public static final EasingFunction EaseInSine = new EasingFunction() {
            // @Override
            // public float ease(long elapsed, long duration) {
            // float position = elapsed / (float) duration;
            // return -(float) Math.cos(position * (Math.PI / 2.f)) + 1.f;
            // }
            @Override
            public float getInterpolation(float input) {
                return -(float) Math.cos(input * (Math.PI / 2.f)) + 1.f;
            }
        };

        public static final EasingFunction EaseOutSine = new EasingFunction() {
            // @Override
            // public float ease(long elapsed, long duration) {
            // float position = elapsed / (float) duration;
            // return (float) Math.sin(position * (Math.PI / 2.f));
            // }
            @Override
            public float getInterpolation(float input) {
                return (float) Math.sin(input * (Math.PI / 2.f));
            }
        };

        public static final EasingFunction EaseInOutSine = new EasingFunction() {
            // @Override
            // public float ease(long elapsed, long duration) {
            // float position = elapsed / (float) duration;
            // return -0.5f * ((float) Math.cos(Math.PI * position) - 1.f);
            // }

            @Override
            public float getInterpolation(float input) {
                return -0.5f * ((float) Math.cos(Math.PI * input) - 1.f);
            }
        };

        public static final EasingFunction EaseInExpo = new EasingFunction() {
            // @Override
            // public float ease(long elapsed, long duration) {
            // return (elapsed == 0) ? 0.f : (float) Math.pow(2.f, 10.f * (elapsed
            // / (float) duration - 1.f));
            // }
            @Override
            public float getInterpolation(float input) {
                return (input == 0) ? 0.f : (float) Math.pow(2.f, 10.f * (input - 1.f));
            }
        };

        public static final EasingFunction EaseOutExpo = new EasingFunction() {
            // @Override
            // public float ease(long elapsed, long duration) {
            // return (elapsed == duration) ? 1.f : (-(float) Math.pow(2.f, -10.f *
            // elapsed
            // / (float) duration) + 1.f);
            // }

            @Override
            public float getInterpolation(float input) {
                return (input == 1f) ? 1.f : (-(float) Math.pow(2.f, -10.f * (input + 1.f)));
            }
        };

        public static final EasingFunction EaseInOutExpo = new
                EasingFunction() {
                    // @Override
                    // public float ease(long elapsed, long duration) {
                    // if (elapsed == 0)
                    // {
                    // return 0.f;
                    // }
                    // if (elapsed == duration)
                    // {
                    // return 1.f;
                    // }
                    //
                    // float position = elapsed / (duration / 2.f);
                    // if (position < 1.f)
                    // {
                    // return 0.5f * (float) Math.pow(2.f, 10.f * (position - 1.f));
                    // }
                    // return 0.5f * (-(float) Math.pow(2.f, -10.f * --position) +
                    // 2.f);
                    // }

                    @Override
                    public float getInterpolation(float input) {
                        if (input == 0)
                        {
                            return 0.f;
                        }
                        if (input == 1f)
                        {
                            return 1.f;
                        }

                        float position = input / 0.5f;
                        if (position < 1.f)
                        {
                            return 0.5f * (float) Math.pow(2.f, 10.f * (position - 1.f));
                        }
                        return 0.5f * (-(float) Math.pow(2.f, -10.f * --position) + 2.f);
                    }
                };

        public static final EasingFunction EaseInCirc = new EasingFunction() {
            // @Override
            // public float ease(long elapsed, long duration) {
            // float position = elapsed / (float) duration;
            // return -((float) Math.sqrt(1.f - position * position) - 1.f);
            // }

            @Override
            public float getInterpolation(float input) {
                return -((float) Math.sqrt(1.f - input * input) - 1.f);
            }
        };

        public static final EasingFunction EaseOutCirc = new EasingFunction() {
            // @Override
            // public float ease(long elapsed, long duration) {
            // float position = elapsed / (float) duration;
            // position--;
            // return (float) Math.sqrt(1.f - position * position);
            // }
            @Override
            public float getInterpolation(float input) {
                input--;
                return (float) Math.sqrt(1.f - input * input);
            }
        };

        public static final EasingFunction EaseInOutCirc = new
                EasingFunction() {
                    // @Override
                    // public float ease(long elapsed, long duration) {
                    // float position = elapsed / (duration / 2.f);
                    // if (position < 1.f)
                    // {
                    // return -0.5f * ((float) Math.sqrt(1.f - position * position)
                    // - 1.f);
                    // }
                    // return 0.5f * ((float) Math.sqrt(1.f - (position -= 2.f) *
                    // position)
                    // + 1.f);
                    // }

                    @Override
                    public float getInterpolation(float input) {
                        float position = input / 0.5f;
                        if (position < 1.f)
                        {
                            return -0.5f * ((float) Math.sqrt(1.f - position * position) - 1.f);
                        }
                        return 0.5f * ((float) Math.sqrt(1.f - (position -= 2.f) * position)
                        + 1.f);
                    }
                };

        public static final EasingFunction EaseInElastic = new
                EasingFunction() {
                    // @Override
                    // public float ease(long elapsed, long duration) {
                    // if (elapsed == 0)
                    // {
                    // return 0.f;
                    // }
                    //
                    // float position = elapsed / (float) duration;
                    // if (position == 1)
                    // {
                    // return 1.f;
                    // }
                    //
                    // float p = duration * .3f;
                    // float s = p / (2.f * (float) Math.PI) * (float)
                    // Math.asin(1.f);
                    // return -((float) Math.pow(2.f, 10.f * (position -= 1.f)) *
                    // (float)
                    // Math
                    // .sin((position * duration - s) * (2.f * Math.PI) / p));
                    // }

                    @Override
                    public float getInterpolation(float input) {
                        if (input == 0)
                        {
                            return 0.f;
                        }

                        float position = input;
                        if (position == 1)
                        {
                            return 1.f;
                        }

                        float p = .3f;
                        float s = p / (2.f * (float) Math.PI) * (float) Math.asin(1.f);
                        return -((float) Math.pow(2.f, 10.f * (position -= 1.f)) * (float)
                        Math
                                .sin((position - s) * (2.f * Math.PI) / p));
                    }
                };

        public static final EasingFunction EaseOutElastic = new
                EasingFunction() {
                    // @Override
                    // public float ease(long elapsed, long duration) {
                    // if (elapsed == 0)
                    // {
                    // return 0.f;
                    // }
                    //
                    // float position = elapsed / (float) duration;
                    // if (position == 1)
                    // {
                    // return 1.f;
                    // }
                    //
                    // float p = duration * .3f;
                    // float s = p / (2 * (float) Math.PI) * (float) Math.asin(1.f);
                    // return (float) Math.pow(2, -10 * position)
                    // * (float) Math.sin((position * duration - s) * (2.f *
                    // Math.PI) / p) +
                    // 1.f;
                    // }

                    @Override
                    public float getInterpolation(float input) {
                        if (input == 0)
                        {
                            return 0.f;
                        }

                        float position = input;
                        if (position == 1)
                        {
                            return 1.f;
                        }

                        float p = .3f;
                        float s = p / (2 * (float) Math.PI) * (float) Math.asin(1.f);
                        return (float) Math.pow(2, -10 * position)
                                * (float) Math.sin((position - s) * (2.f * Math.PI) / p) +
                                1.f;
                    }
                };

        public static final EasingFunction EaseInOutElastic = new
                EasingFunction() {
                    // @Override
                    // public float ease(long elapsed, long duration) {
                    // if (elapsed == 0)
                    // {
                    // return 0.f;
                    // }
                    //
                    // float position = elapsed / (duration / 2.f);
                    // if (position == 2)
                    // {
                    // return 1.f;
                    // }
                    //
                    // float p = duration * (.3f * 1.5f);
                    // float s = p / (2.f * (float) Math.PI) * (float)
                    // Math.asin(1.f);
                    // if (position < 1.f)
                    // {
                    // return -.5f
                    // * ((float) Math.pow(2.f, 10.f * (position -= 1.f)) * (float)
                    // Math
                    // .sin((position * duration - s) * (2.f * Math.PI) / p));
                    // }
                    // return (float) Math.pow(2.f, -10.f * (position -= 1.f))
                    // * (float) Math.sin((position * duration - s) * (2.f *
                    // Math.PI) / p) *
                    // .5f
                    // + 1.f;
                    // }

                    @Override
                    public float getInterpolation(float input) {
                        if (input == 0)
                        {
                            return 0.f;
                        }

                        float position = input / 0.5f;
                        if (position == 2)
                        {
                            return 1.f;
                        }

                        float p = (.3f * 1.5f);
                        float s = p / (2.f * (float) Math.PI) * (float) Math.asin(1.f);
                        if (position < 1.f)
                        {
                            return -.5f
                                    * ((float) Math.pow(2.f, 10.f * (position -= 1.f)) * (float) Math
                                            .sin((position * 1f - s) * (2.f * Math.PI) / p));
                        }
                        return (float) Math.pow(2.f, -10.f * (position -= 1.f))
                                * (float) Math.sin((position * 1f - s) * (2.f * Math.PI) / p) *
                                .5f
                                + 1.f;
                    }
                };

        public static final EasingFunction EaseInBack = new EasingFunction()
        {
            // @Override
            // public float ease(long elapsed, long duration) {
            // final float s = 1.70158f;
            // float position = elapsed / (float) duration;
            // return position * position * ((s + 1.f) * position - s);
            // }

            @Override
            public float getInterpolation(float input) {
                final float s = 1.70158f;
                float position = input;
                return position * position * ((s + 1.f) * position - s);
            }
        };

        public static final EasingFunction EaseOutBack = new EasingFunction()
        {
            // @Override
            // public float ease(long elapsed, long duration) {
            // final float s = 1.70158f;
            // float position = elapsed / (float) duration;
            // position--;
            // return (position * position * ((s + 1.f) * position + s) + 1.f);
            // }

            @Override
            public float getInterpolation(float input) {
                final float s = 1.70158f;
                float position = input;
                position--;
                return (position * position * ((s + 1.f) * position + s) + 1.f);
            }
        };

        public static final EasingFunction EaseInOutBack = new
                EasingFunction() {
                    // @Override
                    // public float ease(long elapsed, long duration) {
                    // float s = 1.70158f;
                    // float position = elapsed / (duration / 2.f);
                    // if (position < 1.f)
                    // {
                    // return 0.5f * (position * position * (((s *= (1.525f)) + 1.f)
                    // *
                    // position - s));
                    // }
                    // return 0.5f * ((position -= 2.f) * position
                    // * (((s *= (1.525f)) + 1.f) * position + s) + 2.f);
                    // }

                    @Override
                    public float getInterpolation(float input) {
                        float s = 1.70158f;
                        float position = input / 0.5f;
                        if (position < 1.f)
                        {
                            return 0.5f * (position * position * (((s *= (1.525f)) + 1.f) *
                                    position - s));
                        }
                        return 0.5f * ((position -= 2.f) * position
                                * (((s *= (1.525f)) + 1.f) * position + s) + 2.f);
                    }
                };

        public static final EasingFunction EaseInBounce = new
                EasingFunction() {
                    // @Override
                    // public float ease(long elapsed, long duration) {
                    // return 1.f - EaseOutBounce.ease(duration - elapsed,
                    // duration);
                    // }

                    @Override
                    public float getInterpolation(float input) {
                        return 1.f - EaseOutBounce.getInterpolation(1f - input);
                    }
                };

        public static final EasingFunction EaseOutBounce = new
                EasingFunction() {
                    // @Override
                    // public float ease(long elapsed, long duration) {
                    // float position = elapsed / (float) duration;
                    // if (position < (1.f / 2.75f))
                    // {
                    // return (7.5625f * position * position);
                    // }
                    // else if (position < (2.f / 2.75f))
                    // {
                    // return (7.5625f * (position -= (1.5f / 2.75f)) * position +
                    // .75f);
                    // }
                    // else if (position < (2.5f / 2.75f))
                    // {
                    // return (7.5625f * (position -= (2.25f / 2.75f)) * position +
                    // .9375f);
                    // }
                    // else
                    // {
                    // return (7.5625f * (position -= (2.625f / 2.75f)) * position +
                    // .984375f);
                    // }
                    // }

                    @Override
                    public float getInterpolation(float input) {
                        float position = input;
                        if (position < (1.f / 2.75f))
                        {
                            return (7.5625f * position * position);
                        }
                        else if (position < (2.f / 2.75f))
                        {
                            return (7.5625f * (position -= (1.5f / 2.75f)) * position + .75f);
                        }
                        else if (position < (2.5f / 2.75f))
                        {
                            return (7.5625f * (position -= (2.25f / 2.75f)) * position + .9375f);
                        }
                        else
                        {
                            return (7.5625f * (position -= (2.625f / 2.75f)) * position +
                            .984375f);
                        }
                    }
                };

        public static final EasingFunction EaseInOutBounce = new
                EasingFunction() {
                    // @Override
                    // public float ease(long elapsed, long duration) {
                    // if (elapsed < duration / 2.f)
                    // {
                    // return EaseInBounce.ease(elapsed * 2, duration) * .5f;
                    // }
                    // return EaseOutBounce.ease(elapsed * 2 - duration, duration) *
                    // .5f +
                    // .5f;
                    // }

                    @Override
                    public float getInterpolation(float input) {
                        if (input < 0.5f)
                        {
                            return EaseInBounce.getInterpolation(input * 2) * .5f;
                        }
                        return EaseOutBounce.getInterpolation(input * 2 - 1f) * .5f +
                        .5f;
                    }
                };

    }
}
