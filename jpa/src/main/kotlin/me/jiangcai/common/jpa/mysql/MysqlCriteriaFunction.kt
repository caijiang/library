package me.jiangcai.common.jpa.mysql

import me.jiangcai.common.jpa.CriteriaFunction
import javax.persistence.criteria.CriteriaBuilder

/**
 * @author CJ
 */
class MysqlCriteriaFunction(builder: CriteriaBuilder, timezoneDiff: String?) :
    CriteriaFunction(builder, timezoneDiff) {

}