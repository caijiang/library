package me.jiangcai.common.jpa

import org.springframework.context.annotation.Configuration


@Configuration
@EnableJpa(provider = JpaProvider.Hibernate, useH2TempDataSource = true)
open class HibernateH2

@Configuration
@EnableJpa(provider = JpaProvider.EclipseLink, useH2TempDataSource = true)
open class EclipseLinkH2


@Configuration
@EnableJpa(provider = JpaProvider.Hibernate, useMysqlDatabase = "library")
open class HibernateMysql

@Configuration
@EnableJpa(provider = JpaProvider.EclipseLink, useMysqlDatabase = "library")
open class EclipseLinkMysql