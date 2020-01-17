package example.hello.service;

import io.rsocket.AbstractRSocket;
import io.rsocket.ConnectionSetupPayload;
import io.rsocket.Payload;
import io.rsocket.RSocket;
import io.rsocket.RSocketFactory;
import io.rsocket.SocketAcceptor;
import io.rsocket.frame.decoder.PayloadDecoder;
import io.rsocket.metadata.CompositeMetadata;
import io.rsocket.transport.netty.server.TcpServerTransport;
import io.rsocket.util.DefaultPayload;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/**
 * Service that returns hello messages requested by the hello-client.
 */
public class HelloService {
    private static final Logger LOG = LoggerFactory.getLogger(HelloService.class);

    private static final String MESSAGE_FORMAT = "Hello, %s! [clientId: %s]";

    public static void main(String... args) throws Exception {
        RSocketFactory.receive()
                .frameDecoder(PayloadDecoder.DEFAULT)
                .acceptor(new SocketAcceptor() {
                    @Override
                    public Mono<RSocket> accept(ConnectionSetupPayload setup, RSocket sendingSocket) {
                        // Parsing the connection setup metadata and setting the client id for the connection
                        // that was passed by the hello-client during connection setup.
                        final Map<String, Object> connMetadata = parseMetadata(setup);
                        final String clientId = (String) connMetadata.getOrDefault("messaging/x.clientId", "UNKNOWN");

                        return Mono.just(new AbstractRSocket() {
                            @Override
                            public Mono<Payload> requestResponse(Payload payload) {
                                final String name = payload.getDataUtf8();
                                final String response = String.format(MESSAGE_FORMAT, name, clientId);

                                LOG.info("Sending message: {}", response);

                                return Mono.just(DefaultPayload.create(response.getBytes()));
                            }
                        });
                    }
                })
                .transport(TcpServerTransport.create(7000))
                .start()
                .block();

        LOG.info("RSocket server started on port: 7000");

        Thread.currentThread().join();
    }

    /**
     * Parses the connection composite metadata.
     *
     * @param payload connection setup payload
     * @return a map containing the composite metadata entries
     */
    private static Map<String, Object> parseMetadata(ConnectionSetupPayload payload) {
        Map<String, Object> metadataMap = new HashMap<>();

        CompositeMetadata compositeMetadata = new CompositeMetadata(payload.metadata(), true);
        compositeMetadata.forEach(entry -> {
            byte[] bytes = new byte[entry.getContent().readableBytes()];
            entry.getContent().readBytes(bytes);

            metadataMap.put(entry.getMimeType(), new String(bytes, StandardCharsets.UTF_8));
        });

        return metadataMap;
    }
}
