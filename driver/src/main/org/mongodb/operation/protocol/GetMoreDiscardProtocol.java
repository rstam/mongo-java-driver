/*
 * Copyright (c) 2008 - 2013 10gen, Inc. <http://10gen.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.mongodb.operation.protocol;

import org.mongodb.connection.Channel;
import org.mongodb.connection.ChannelReceiveArgs;
import org.mongodb.connection.ResponseBuffers;

public class GetMoreDiscardProtocol implements Protocol<Void> {
    private final long cursorId;
    private final int responseTo;
    private final Channel channel;

    public GetMoreDiscardProtocol(final long cursorId, final int responseTo, final Channel channel) {
        this.cursorId = cursorId;
        this.responseTo = responseTo;
        this.channel = channel;
    }

    public Void execute() {
        long curCursorId = cursorId;
        int curResponseTo = responseTo;
        while (curCursorId != 0) {
            final ResponseBuffers responseBuffers = channel.receiveMessage(new ChannelReceiveArgs(curResponseTo));
            try {
                curCursorId = responseBuffers.getReplyHeader().getCursorId();
                curResponseTo = responseBuffers.getReplyHeader().getRequestId();
            } finally {
                responseBuffers.close();
            }
        }
        return null;
    }
}
