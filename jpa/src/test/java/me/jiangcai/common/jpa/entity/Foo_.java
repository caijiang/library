package me.jiangcai.common.jpa.entity;

import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * @author CJ
 */
@Generated(value = "org.hibernate.jpamodelgen.JPAMetaModelEntityProcessor")
@StaticMetamodel(Foo.class)
public class Foo_ {

    public static volatile SingularAttribute<Foo, String> id;
    public static volatile SingularAttribute<Foo, BigDecimal> value;
    public static volatile SingularAttribute<Foo, LocalDateTime> created;
}
