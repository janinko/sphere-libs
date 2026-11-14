package eu.janinko.uo.saves;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 *
 * @author Honza Brázdil <janinko.g@gmail.com>
 */
@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class Character extends Entry {

    private long deaths;
    private String account;
    private String skillclass;
    private long create;

    public Character(String def) {
        super(def);
    }

}
