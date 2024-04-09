package com.spike.vertx.mre;

import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.ext.web.client.HttpRequest;
import io.vertx.ext.web.client.WebClient;
import io.vertx.ext.web.client.impl.ClientPhase;
import io.vertx.ext.web.client.impl.HttpContext;
import io.vertx.ext.web.client.impl.WebClientInternal;

// TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class ForLoopSend {
    public static void main(String[] args) {

        Vertx vertx = Vertx.vertx();
        WebClientInternal client =
                (WebClientInternal) WebClient.wrap(vertx.httpClientBuilder().build());
        client.addInterceptor(
                new Handler<HttpContext<?>>() {
                    @Override
                    public void handle(HttpContext<?> httpContext) {
                        System.out.println(httpContext.phase());
                        if (httpContext.phase() == ClientPhase.SEND_REQUEST) { // changing to CREATE_REQUEST will allow all requests go through
                            httpContext.fail(new IllegalStateException("send failed"));
                        } else {
                            httpContext.next();
                        }
                    }
                });
        final HttpRequest<Buffer> bufferHttpRequest = client.get("http://localhost:9999");
        for (int i = 0; i < 20; i++) {
            try {
                bufferHttpRequest.send().toCompletionStage().toCompletableFuture().join();
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }
        System.out.println("Finish");
    }
}
