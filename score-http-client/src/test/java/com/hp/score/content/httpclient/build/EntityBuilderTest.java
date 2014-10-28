/*
 * Licensed to Hewlett-Packard Development Company, L.P. under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
*/
package com.hp.score.content.httpclient.build;

import org.apache.http.HttpEntity;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.FileEntity;
import org.apache.http.entity.StringEntity;
import org.junit.Before;
import org.junit.Test;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNull;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.junit.Assert.assertThat;

/**
 * User: Adina Tusa
 * Date: 8/19/14
 */
public class EntityBuilderTest {
    private static final String CONTENT_TYPE = "text/plain";
    private EntityBuilder entityBuilder;

    @Before
    public void setUp() {
        entityBuilder = new EntityBuilder();
    }

    @Test
    public void buildEntity() {
        HttpEntity httpEntity = entityBuilder
                .setBody("testBody")
                .buildEntity();
        assertThat(httpEntity, instanceOf(StringEntity.class));
        StringEntity stringEntity = (StringEntity) httpEntity;
        assertNull(stringEntity.getContentType());
    }

    @Test
    public void buildEntityWithContentType() {
        ContentType parsedContentType = ContentType.parse(CONTENT_TYPE);
        HttpEntity httpEntity = entityBuilder
                .setBody("testBody")
                .setContentType(parsedContentType)
                .buildEntity();
        assertThat(httpEntity, instanceOf(StringEntity.class));
        StringEntity stringEntity = (StringEntity) httpEntity;
        assertEquals(CONTENT_TYPE, stringEntity.getContentType().getValue());
    }

    @Test
    public void buildEntityWithFile() {
        ContentType parsedContentType = ContentType.parse(CONTENT_TYPE);
        HttpEntity httpEntity = entityBuilder
                .setFilePath(getClass().getResource("testfile.txt").getPath() )
                .setContentType(parsedContentType)
                .buildEntity();
        assertThat(httpEntity, instanceOf(FileEntity.class));
        FileEntity fileEntity = (FileEntity) httpEntity;
        assertEquals(CONTENT_TYPE, fileEntity.getContentType().getValue());
    }

    @Test
    public void buildEmptyEntity() {
        ContentType parsedContentType = ContentType.parse(CONTENT_TYPE);
        HttpEntity httpEntity = entityBuilder
                .setContentType(parsedContentType)
                .buildEntity();
        assertNull(httpEntity);
    }
}
