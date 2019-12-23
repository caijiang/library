package me.jiangcai.common.jpa

import org.springframework.test.context.ContextConfiguration

@ContextConfiguration(classes = [HibernateH2::class])
class HibernateH2Test : CriteriaFunctionBuilderTest()

@ContextConfiguration(classes = [EclipseLinkH2::class])
class EclipseLinkH2Test : CriteriaFunctionBuilderTest()

@ContextConfiguration(classes = [HibernateMysql::class])
class HibernateMysqlTest : CriteriaFunctionBuilderTest()

@ContextConfiguration(classes = [EclipseLinkMysql::class])
class EclipseLinkMysqlTest : CriteriaFunctionBuilderTest()