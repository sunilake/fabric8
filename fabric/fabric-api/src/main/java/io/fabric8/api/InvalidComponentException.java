/**
 *  Copyright 2005-2016 Red Hat, Inc.
 *
 *  Red Hat licenses this file to you under the Apache License, version
 *  2.0 (the "License"); you may not use this file except in compliance
 *  with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 *  implied.  See the License for the specific language governing
 *  permissions and limitations under the License.
 */
package io.fabric8.api;

/**
 * A runtime exception thrown by invalid components.
 *
 * @since 13-Sep-2013
 */
public class InvalidComponentException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public InvalidComponentException() {
    }

    public InvalidComponentException(String message) {
        super(message);
    }

    public InvalidComponentException(String message, Throwable cause) {
        super(message, cause);
    }
}
