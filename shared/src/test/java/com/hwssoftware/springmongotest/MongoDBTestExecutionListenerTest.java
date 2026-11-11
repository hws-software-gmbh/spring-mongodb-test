/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2026 HWS Software GmbH, Jimi Steidl <www.hws-software.com>
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

package com.hwssoftware.springmongotest;

import org.junit.jupiter.api.Test;
import org.springframework.test.context.TestContext;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class MongoDBTestExecutionListenerTest {
    @SuppressWarnings("unchecked")
    private final AnnotationHandler<Doc> mongoDB = mock(AnnotationHandler.class);
    @SuppressWarnings("unchecked")
    private final AnnotationHandler<GridFsFile> gridFs = mock(AnnotationHandler.class);
    private final TestContext ctx = mock(TestContext.class);
    private final MongoDBTestExecutionListener listener = new MongoDBTestExecutionListener(mongoDB, gridFs);

    @Test
    public void beforeTestMethodRunsEachCleanupExactlyOnce() throws Exception {
        doReturn(Fixture.class).when(ctx).getTestClass();
        when(ctx.getTestMethod()).thenReturn(Fixture.class.getDeclaredMethod("annotated"));

        listener.beforeTestMethod(ctx);

        verify(mongoDB, times(1)).cleanup(ctx);
        verify(gridFs, times(1)).cleanup(ctx);
    }

    @Test
    public void beforeTestMethodSkipsCleanupWithoutMongoDBTestAnnotation() throws Exception {
        doReturn(Fixture.class).when(ctx).getTestClass();
        when(ctx.getTestMethod()).thenReturn(Fixture.class.getDeclaredMethod("notAnnotated"));

        listener.beforeTestMethod(ctx);

        verify(mongoDB, never()).cleanup(any());
        verify(gridFs, never()).cleanup(any());
    }

    static class Fixture {
        @MongoDBTest
        void annotated() {
        }

        void notAnnotated() {
        }
    }
}
