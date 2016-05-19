/*
 * Copyright 2016 Red Hat, Inc, and individual contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package eventbusbridge;

import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.bridge.BridgeOptions;
import io.vertx.ext.bridge.PermittedOptions;
import io.vertx.ext.eventbus.bridge.tcp.TcpEventBusBridge;

public class Main {

    public static void main(String[] args) {
        Vertx vertx = Vertx.vertx();
        TcpEventBusBridge bridge = TcpEventBusBridge.create(
                vertx,
                new BridgeOptions()
                        .addOutboundPermitted(new PermittedOptions().setAddressRegex("test.*"))
                .addInboundPermitted(new PermittedOptions().setAddressRegex("test.*")));

        final int port = Integer.parseInt(args[0]);

        bridge.listen(port, res -> {
            vertx.setPeriodic(100, timer -> {
                //System.out.println("Sending the time...");
                vertx.eventBus().publish("test.time", new JsonObject().put("now", System.currentTimeMillis()));
            });

            vertx.eventBus().consumer("test.echo",  m -> m.reply(m.body()));
        });
    }
}