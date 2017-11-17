package com.nike.wingtips.zipkin.elasticapm;

/**
 * Copyright 2015-2016 The OpenZipkin Authors
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */

import java.io.Closeable;
import java.io.IOException;

import com.nike.wingtips.zipkin.elasticapm.internal.Nullable;

import static com.nike.wingtips.zipkin.elasticapm.internal.Util.checkNotNull;


/**
 * Components are object graphs used to compose a zipkin service or client. For example, a storage
 * component might return a query api.
 *
 * <p>Components are lazy with regards to I/O. They can be injected directly to other components so
 * as to avoid crashing the application graph if a network service is unavailable.
 */
public interface Component extends Closeable {

    /**
     * Answers the question: Are operations on this component likely to succeed?
     *
     * <p>Implementations should initialize the component if necessary. It should test a remote
     * connection, or consult a trusted source to derive the result. They should use least resources
     * possible to establish a meaningful result, and be safe to call many times, even concurrently.
     *
     * @see CheckResult#OK
     */
    CheckResult check();

    /**
     * Closes any network resources created implicitly by the component.
     *
     * <p>For example, if this created a connection, it would close it. If it was provided one, this
     * would close any sessions, but leave the connection open.
     */
    @Override
    void close() throws IOException;

    final class CheckResult {
        public static final CheckResult OK = new CheckResult(true, null);

        public static final CheckResult failed(Exception exception) {
            return new CheckResult(false, checkNotNull(exception, "exception"));
        }

        public final boolean ok;

        /** Present when not ok */
        @Nullable
        public final Exception exception;

        CheckResult(boolean ok, Exception exception) {
            this.ok = ok;
            this.exception = exception;
        }
    }
}


