package com.visualizer.log.config;

import org.hibernate.dialect.MySQL57Dialect;
import org.hibernate.dialect.MySQL5Dialect;
import org.hibernate.dialect.MySQL5InnoDBDialect;
import org.hibernate.dialect.function.StandardSQLFunction;
import org.hibernate.type.LongType;
import org.hibernate.type.StringType;

public class CustomMySQLDialect extends MySQL5Dialect { //MySQL57Dialect

    public CustomMySQLDialect() {
        super();

        // native function 추가 - hibernate 버그인지 등록을 안 해주면 실행을 못 함
        this.registerFunction("group_concat", new StandardSQLFunction("group_concat", new StringType()));
        this.registerFunction("timestampdiff", new StandardSQLFunction("timestampdiff", new LongType()));

    }

}