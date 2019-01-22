package me.jiangcai.crud;

import java.io.Serializable;

/**
 * @author CJ
 */
public interface CrudFriendly<ID extends Serializable> {

    /**
     * @return 获取主键
     */
    ID getId();

}
