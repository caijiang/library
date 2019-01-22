package me.jiangcai.crud.env.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

/**
 * @author CJ
 */
@Entity
@Setter
@Getter
public class Foo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    private Bar bar;
}
