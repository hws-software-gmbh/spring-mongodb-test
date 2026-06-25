/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2021 Wholegrain Software, Jimi Steidl <www.wholegrain-software.com>
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.wholegrainsoftware.example.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Primary;
import org.springframework.data.mongodb.MongoDatabaseFactory;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.convert.MappingMongoConverter;
import org.springframework.data.mongodb.core.convert.NoOpDbRefResolver;
import org.springframework.data.mongodb.core.mapping.MongoMappingContext;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@Configuration
@EnableMongoRepositories(
        basePackages = {"com.wholegrainsoftware.example.person", "com.wholegrainsoftware.example.department"}
)
public class DefaultDatabaseConfiguration {
    @Bean
    @Primary
    public MongoTemplate mongoTemplate(MongoDatabaseFactory factory) {
        return new MongoTemplate(factory);
    }

    // Spring Boot 3's Mongo auto-config drags SslAutoConfiguration into MongoClient
    // creation; its ObjectProvider<SslBundleRegistrar>.orderedStream() force-instantiates
    // this bean while the MongoClient/mongoDatabaseFactory are still being built, forming
    // a cycle that Boot 3 (unlike Boot 2) rejects. The factory is only stored by the
    // constructor, so a @Lazy proxy defers it past bootstrap. The shared
    // MappingMongoConverter can't be used here because it depends on the factory (and is
    // dereferenced during construction), so build a connection-independent converter for
    // GridFs metadata instead.
    @Bean
    @Primary
    public GridFsTemplate gridFsTemplate(@Lazy MongoDatabaseFactory factory) {
        MappingMongoConverter converter =
                new MappingMongoConverter(NoOpDbRefResolver.INSTANCE, new MongoMappingContext());
        converter.afterPropertiesSet();
        return new GridFsTemplate(factory, converter);
    }
}
