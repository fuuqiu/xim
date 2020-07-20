package io.zhudy.xim.server;

import static io.zhudy.xim.ConfigKeys.IM_SERVER_ADDR;
import static io.zhudy.xim.session.SessionManager.SESSION_LISTENER_NAME_FOR_IOC;

import com.google.common.net.HostAndPort;

import javax.inject.Inject;
import javax.inject.Named;

import io.zhudy.xim.session.SessionListener;
import lombok.extern.log4j.Log4j2;
import org.reactivestreams.Publisher;
import reactor.core.publisher.Mono;
import reactor.netty.DisposableServer;
import reactor.netty.http.server.HttpServer;
import reactor.netty.http.server.HttpServerRequest;
import reactor.netty.http.server.HttpServerResponse;
import reactor.netty.http.server.WebsocketServerSpec;

import java.util.Set;

@Log4j2
public class ImServer {

    public static final String VERSION_HTTP_PATH = "/version";
    public static final String IM_WS_PATH = "/im";

    private final ImSocketHandler imSocketHandler;
    private final HostAndPort hap;

    private DisposableServer disposableServer;

    private Set<SessionListener> sessionListeners;

    @Inject
    public ImServer(@Named(IM_SERVER_ADDR) HostAndPort hap, ImSocketHandler imSocketHandler, @Named(SESSION_LISTENER_NAME_FOR_IOC) Set<SessionListener> sessionListeners) {
        this.hap = hap;
        this.imSocketHandler = imSocketHandler;

        this.sessionListeners = sessionListeners;
    }


    /**
     * 启动 IM 服务.
     */
    public void start() {
        this.disposableServer =
                HttpServer.create()
                        .host(hap.getHost())
                        .port(hap.getPort())
                        .wiretap(true)
                        .route(
                                routes -> {
                                    routes.get("/info", this::info);
                                    // IM Socket 注册
                                    routes.ws(
                                            IM_WS_PATH,
                                            imSocketHandler,
                                            WebsocketServerSpec.builder().handlePing(true).build());
                                })
                        .bindNow();
        log.info("IM Server started at - {}", hap);
    }

    /**
     * 停止 IM 服务.
     */
    public void stop() {
        if (disposableServer != null) {
            disposableServer.disposeNow();
        }
        log.info("IM Server stopped");
    }

    // FIXME 后面完善
    private Publisher<Void> info(HttpServerRequest request, HttpServerResponse response) {
        var size = sessionListeners.size();
        return response
                .header("content-type", "application/json")
                .sendHeaders()
                .sendString(Mono.just("{\"xim_version\": \"1.0.0\"}"));
    }
}
