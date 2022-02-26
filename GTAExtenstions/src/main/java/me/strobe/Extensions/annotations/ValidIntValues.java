package me.strobe.Extensions.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This is ValidValues in Project: (Gta-Core) : But you already knew that
 *
 * @author G.P of Prentice Productions
 * @version 1.0
 * Created On    : 2/25/2022, 9:33 AM
 * Last Edit     : 2/25/2022, 9:33 AM (Update Me!)
 * Time to Write : (Rough Estimate)
 * <p>
 * (Class Description)
 */

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.PARAMETER})
public @interface ValidIntValues {
    int[] values() default {};
    int min() default 0;
    int max() default Integer.MAX_VALUE;

}
