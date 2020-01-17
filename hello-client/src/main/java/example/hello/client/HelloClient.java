package example.hello.client;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.CompositeByteBuf;
import io.netty.buffer.Unpooled;
import io.rsocket.Payload;
import io.rsocket.RSocket;
import io.rsocket.RSocketFactory;
import io.rsocket.metadata.CompositeMetadataFlyweight;
import io.rsocket.transport.netty.client.TcpClientTransport;
import io.rsocket.util.DefaultPayload;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.UUID;

public class HelloClient {
    private static final Logger LOG = LoggerFactory.getLogger(HelloClient.class);

    public static void main(String... args) {
        final String name = getNameFromArgs(args);
        final String clientId = UUID.randomUUID().toString();

        CompositeByteBuf metadataByteBuf = buildConnectionSetupMetadata(clientId);

        // Create RSocket connection to hello-service
        RSocket rSocket = RSocketFactory.connect()
                .setupPayload(DefaultPayload.create(Unpooled.EMPTY_BUFFER, metadataByteBuf))
                .transport(TcpClientTransport.create(7000))
                .start()
                .block();

        // Create the data buffer
        final ByteBuf data = ByteBufAllocator.DEFAULT.buffer().writeBytes(name.getBytes());

        LOG.info("Sending request for '{}'", name);

        // Sending the request
        String response = rSocket.requestResponse(DefaultPayload.create(data))
                .map(Payload::getDataUtf8)
                .block();

        LOG.info("Response: {}", response);
    }

    /**
     * Builds a composite metadata buffer containing the clientId.
     *
     * @param clientId client identifier
     * @return a {@link CompositeByteBuf}
     */
    private static CompositeByteBuf buildConnectionSetupMetadata(final String clientId) {
        CompositeByteBuf metadataByteBuf = ByteBufAllocator.DEFAULT.compositeBuffer();

        // Adding the clientId to the composite metadata
        CompositeMetadataFlyweight.encodeAndAddMetadata(
                metadataByteBuf,
                ByteBufAllocator.DEFAULT,
                "messaging/x.clientId",
                ByteBufAllocator.DEFAULT.buffer().writeBytes(clientId.getBytes()));

        return metadataByteBuf;
    }

    /**
     * Gets the name of the hello recipient from the command line arguments.
     *
     * @param args command line arguments
     * @return name of hello recipient
     */
    private static String getNameFromArgs(String... args) {
        if (args.length != 1) {
            throw new IllegalArgumentException("parameter 0 must be the name of the hello message recipient");
        }

        return args[0];
    }
}
