package com.sidework.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import jakarta.persistence.EntityManager;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.PersistenceContext;

@Configuration
public class QuerydslConfig {

	@PersistenceContext
	private EntityManager em;

	@Bean
	public JPAQueryFactory jpaQueryFactory() {
		return new JPAQueryFactory(em);
	}
}
