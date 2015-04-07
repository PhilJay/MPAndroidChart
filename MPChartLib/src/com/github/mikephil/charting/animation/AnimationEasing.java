package com.github.mikephil.charting.animation;

/**
 * Created by dcg on 7/4/15.
 */
public class AnimationEasing
{

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
        EaseInQuint,
        EaseOutQuint,
        EaseInOutQuint,
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

    public static interface EasingFunction {
        public float ease(long elapsed, long duration);
    }

    public static EasingFunction getEasingFunctionFromOption(EasingOption easing) {
        switch (easing) {
            default:
            case Linear:
                return EasingFunctions.Linear;
            case EaseInQuad:
                return EasingFunctions.EaseInQuad;
            case EaseOutQuad:
                return EasingFunctions.EaseOutQuad;
            case EaseInOutQuad:
                return EasingFunctions.EaseInOutQuad;
            case EaseInCubic:
                return EasingFunctions.EaseInCubic;
            case EaseOutCubic:
                return EasingFunctions.EaseOutCubic;
            case EaseInOutCubic:
                return EasingFunctions.EaseInOutCubic;
            case EaseInQuart:
                return EasingFunctions.EaseInQuart;
            case EaseOutQuart:
                return EasingFunctions.EaseOutQuart;
            case EaseInOutQuart:
                return EasingFunctions.EaseInOutQuart;
            case EaseInQuint:
                return EasingFunctions.EaseInQuint;
            case EaseOutQuint:
                return EasingFunctions.EaseOutQuint;
            case EaseInOutQuint:
                return EasingFunctions.EaseInOutQuint;
            case EaseInSine:
                return EasingFunctions.EaseInSine;
            case EaseOutSine:
                return EasingFunctions.EaseOutSine;
            case EaseInOutSine:
                return EasingFunctions.EaseInOutSine;
            case EaseInExpo:
                return EasingFunctions.EaseInExpo;
            case EaseOutExpo:
                return EasingFunctions.EaseOutExpo;
            case EaseInOutExpo:
                return EasingFunctions.EaseInOutExpo;
            case EaseInCirc:
                return EasingFunctions.EaseInCirc;
            case EaseOutCirc:
                return EasingFunctions.EaseOutCirc;
            case EaseInOutCirc:
                return EasingFunctions.EaseInOutCirc;
            case EaseInElastic:
                return EasingFunctions.EaseInElastic;
            case EaseOutElastic:
                return EasingFunctions.EaseOutElastic;
            case EaseInOutElastic:
                return EasingFunctions.EaseInOutElastic;
            case EaseInBack:
                return EasingFunctions.EaseInBack;
            case EaseOutBack:
                return EasingFunctions.EaseOutBack;
            case EaseInOutBack:
                return EasingFunctions.EaseInOutBack;
            case EaseInBounce:
                return EasingFunctions.EaseInBounce;
            case EaseOutBounce:
                return EasingFunctions.EaseOutBounce;
            case EaseInOutBounce:
                return EasingFunctions.EaseInOutBounce;
        }
    }

    public static class EasingFunctions {

        public static final EasingFunction Linear = new EasingFunction() {
            @Override
            public float ease(long elapsed, long duration) {
                return elapsed / (float) duration;
            }
        };

        public static final EasingFunction EaseInQuad = new EasingFunction() {
            @Override
            public float ease(long elapsed, long duration) {
                float position = elapsed / (float) duration;
                return position * position;
            }
        };

        public static final EasingFunction EaseOutQuad = new EasingFunction() {
            @Override
            public float ease(long elapsed, long duration) {
                float position = elapsed / (float) duration;
                return -position * (position - 2.f);
            }
        };

        public static final EasingFunction EaseInOutQuad = new EasingFunction() {
            @Override
            public float ease(long elapsed, long duration) {
                float position = elapsed / (duration / 2.f);
                if (position < 1.f)
                {
                    return 0.5f * position * position;
                }
                return -0.5f * ((--position) * (position - 2.f) - 1.f);
            }
        };

        public static final EasingFunction EaseInCubic = new EasingFunction() {
            @Override
            public float ease(long elapsed, long duration) {
                float position = elapsed / (float) duration;
                return position * position * position;
            }
        };

        public static final EasingFunction EaseOutCubic = new EasingFunction() {
            @Override
            public float ease(long elapsed, long duration) {
                float position = elapsed / (float) duration;
                position--;
                return (position * position * position + 1.f);
            }
        };

        public static final EasingFunction EaseInOutCubic = new EasingFunction() {
            @Override
            public float ease(long elapsed, long duration) {
                float position = elapsed / (duration / 2.f);
                if (position < 1.f)
                {
                    return 0.5f * position * position * position;
                }
                position -= 2.f;
                return 0.5f * (position * position * position + 2.f);
            }
        };

        public static final EasingFunction EaseInQuart = new EasingFunction() {
            @Override
            public float ease(long elapsed, long duration) {
                float position = elapsed / (float) duration;
                return position * position * position * position;
            }
        };

        public static final EasingFunction EaseOutQuart = new EasingFunction() {
            @Override
            public float ease(long elapsed, long duration) {
                float position = elapsed / (float) duration;
                position--;
                return -(position * position * position * position - 1.f);
            }
        };

        public static final EasingFunction EaseInOutQuart = new EasingFunction() {
            @Override
            public float ease(long elapsed, long duration) {
                float position = elapsed / (duration / 2.f);
                if (position < 1.f)
                {
                    return 0.5f * position * position * position * position;
                }
                position -= 2.f;
                return -0.5f * (position * position * position * position - 2.f);
            }
        };

        public static final EasingFunction EaseInQuint = new EasingFunction() {
            @Override
            public float ease(long elapsed, long duration) {
                float position = elapsed / (float) duration;
                return position * position * position * position * position;
            }
        };

        public static final EasingFunction EaseOutQuint = new EasingFunction() {
            @Override
            public float ease(long elapsed, long duration) {
                float position = elapsed / (float) duration;
                position--;
                return (position * position * position * position * position + 1.f);
            }
        };

        public static final EasingFunction EaseInOutQuint = new EasingFunction() {
            @Override
            public float ease(long elapsed, long duration) {
                float position = elapsed / (duration / 2.f);
                if (position < 1.f)
                {
                    return 0.5f * position * position * position * position * position;
                }
                return 0.5f * ((position -= 2.f) * position * position * position * position + 2.f);
            }
        };

        public static final EasingFunction EaseInSine = new EasingFunction() {
            @Override
            public float ease(long elapsed, long duration) {
                float position = elapsed / (float) duration;
                return -(float) Math.cos(position * (Math.PI / 2.f)) + 1.f;
            }
        };

        public static final EasingFunction EaseOutSine = new EasingFunction() {
            @Override
            public float ease(long elapsed, long duration) {
                float position = elapsed / (float) duration;
                return (float) Math.sin(position * (Math.PI / 2.f));
            }
        };

        public static final EasingFunction EaseInOutSine = new EasingFunction() {
            @Override
            public float ease(long elapsed, long duration) {
                float position = elapsed / (float) duration;
                return -0.5f * ((float) Math.cos(Math.PI * position) - 1.f);
            }
        };

        public static final EasingFunction EaseInExpo = new EasingFunction() {
            @Override
            public float ease(long elapsed, long duration) {
                return (elapsed == 0) ? 0.f : (float) Math.pow(2.f, 10.f * (elapsed / (float) duration - 1.f));
            }
        };

        public static final EasingFunction EaseOutExpo = new EasingFunction() {
            @Override
            public float ease(long elapsed, long duration) {
                return (elapsed == duration) ? 1.f : (-(float) Math.pow(2.f, -10.f * elapsed / (float) duration) + 1.f);
            }
        };

        public static final EasingFunction EaseInOutExpo = new EasingFunction() {
            @Override
            public float ease(long elapsed, long duration) {
                if (elapsed == 0)
                {
                    return 0.f;
                }
                if (elapsed == duration)
                {
                    return 1.f;
                }

                float position = elapsed / (duration / 2.f);
                if (position < 1.f)
                {
                    return 0.5f * (float) Math.pow(2.f, 10.f * (position - 1.f));
                }
                return 0.5f * (-(float) Math.pow(2.f, -10.f * --position) + 2.f);
            }
        };

        public static final EasingFunction EaseInCirc = new EasingFunction() {
            @Override
            public float ease(long elapsed, long duration) {
                float position = elapsed / (float) duration;
                return -((float) Math.sqrt(1.f - position * position) - 1.f);
            }
        };

        public static final EasingFunction EaseOutCirc = new EasingFunction() {
            @Override
            public float ease(long elapsed, long duration) {
                float position = elapsed / (float) duration;
                position--;
                return (float) Math.sqrt(1.f - position * position);
            }
        };

        public static final EasingFunction EaseInOutCirc = new EasingFunction() {
            @Override
            public float ease(long elapsed, long duration) {
                float position = elapsed / (duration / 2.f);
                if (position < 1.f)
                {
                    return -0.5f * ((float) Math.sqrt(1.f - position * position) - 1.f);
                }
                return 0.5f * ((float) Math.sqrt(1.f - (position -= 2.f) * position) + 1.f);
            }
        };

        public static final EasingFunction EaseInElastic = new EasingFunction() {
            @Override
            public float ease(long elapsed, long duration) {
                if (elapsed == 0)
                {
                    return 0.f;
                }

                float position = elapsed / (float) duration;
                if (position == 1)
                {
                    return 1.f;
                }

                float p = duration * .3f;
                float s = p / (2.f * (float) Math.PI) * (float) Math.asin(1.f);
                return -((float) Math.pow(2.f, 10.f * (position -= 1.f)) * (float) Math.sin((position * duration - s) * (2.f * Math.PI) / p));
            }
        };

        public static final EasingFunction EaseOutElastic = new EasingFunction() {
            @Override
            public float ease(long elapsed, long duration) {
                if (elapsed == 0)
                {
                    return 0.f;
                }

                float position = elapsed / (float) duration;
                if (position == 1)
                {
                    return 1.f;
                }

                float p = duration * .3f;
                float s = p / (2 * (float) Math.PI) * (float) Math.asin(1.f);
                return (float) Math.pow(2, -10 * position) * (float) Math.sin((position * duration - s) * (2.f * Math.PI) / p) + 1.f;
            }
        };

        public static final EasingFunction EaseInOutElastic = new EasingFunction() {
            @Override
            public float ease(long elapsed, long duration) {
                if (elapsed == 0)
                {
                    return 0.f;
                }

                float position = elapsed / (duration / 2.f);
                if (position == 2)
                {
                    return 1.f;
                }

                float p = duration * (.3f * 1.5f);
                float s = p / (2.f * (float) Math.PI) * (float) Math.asin(1.f);
                if (position < 1.f)
                {
                    return -.5f * ((float) Math.pow(2.f, 10.f * (position -= 1.f)) * (float) Math.sin((position * duration - s) * (2.f * Math.PI) / p));
                }
                return (float) Math.pow(2.f, -10.f * (position -= 1.f)) * (float) Math.sin((position * duration - s) * (2.f * Math.PI) / p) * .5f + 1.f;
            }
        };

        public static final EasingFunction EaseInBack = new EasingFunction() {
            @Override
            public float ease(long elapsed, long duration) {
                final float s = 1.70158f;
                float position = elapsed / (float) duration;
                return position * position * ((s + 1.f) * position - s);
            }
        };

        public static final EasingFunction EaseOutBack = new EasingFunction() {
            @Override
            public float ease(long elapsed, long duration) {
                final float s = 1.70158f;
                float position = elapsed / (float) duration;
                position--;
                return (position * position * ((s + 1.f) * position + s) + 1.f);
            }
        };

        public static final EasingFunction EaseInOutBack = new EasingFunction() {
            @Override
            public float ease(long elapsed, long duration) {
                float s = 1.70158f;
                float position = elapsed / (duration / 2.f);
                if (position < 1.f)
                {
                    return 0.5f * (position * position * (((s *= (1.525f)) + 1.f) * position - s));
                }
                return 0.5f * ((position -= 2.f) * position * (((s *= (1.525f)) + 1.f) * position + s) + 2.f);
            }
        };

        public static final EasingFunction EaseInBounce = new EasingFunction() {
            @Override
            public float ease(long elapsed, long duration) {
                return 1.f - EaseOutBounce.ease(duration - elapsed, duration);
            }
        };

        public static final EasingFunction EaseOutBounce = new EasingFunction() {
            @Override
            public float ease(long elapsed, long duration) {
                float position = elapsed / (float) duration;
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
                    return (7.5625f * (position -= (2.625f / 2.75f)) * position + .984375f);
                }
            }
        };

        public static final EasingFunction EaseInOutBounce = new EasingFunction() {
            @Override
            public float ease(long elapsed, long duration) {
                if (elapsed < duration / 2.f)
                {
                    return EaseInBounce.ease(elapsed * 2, duration) * .5f;
                }
                return EaseOutBounce.ease(elapsed * 2 - duration, duration) * .5f + .5f;
            }
        };

    }
}
