package me.jiangcai.crud.env.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

/**
 * 贵族血统！
 *
 * @author CJ
 */
@Entity
@Setter
@Getter
public class BlueBlood {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    private Bar bar;
}
