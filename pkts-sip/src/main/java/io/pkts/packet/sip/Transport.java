package io.pkts.packet.sip;

import io.pkts.buffer.Buffer;
import io.pkts.buffer.Buffers;
import io.pkts.packet.sip.impl.SipParser;

/**
 * Note, enums should be all caps but SIP is annoying and for transports in a SipURI the transport
 * is supposed to be lower case so therefore we just made these into lower case as well. Just easier
 * that way. 
 * 
 * Created by jonas@jonasborjesson.com
 */
public enum Transport {
    udp(Buffers.wrap("udp")),
    tcp(Buffers.wrap("tcp")),
    tls(Buffers.wrap("tls")),
    sctp(Buffers.wrap("sctp")),
    ws(Buffers.wrap("ws")),
    wss(Buffers.wrap("wss"));

    final Buffer buffer;
    final Buffer upperCaseBuffer;

    Transport(final Buffer buffer) {
        this.buffer = buffer;
        this.upperCaseBuffer = Buffers.wrap(buffer.toString().toUpperCase());
    }

    /**
     * Get the transport off of the given buffer
     * @param buffer
     * @return the transport
     * @throws IllegalArgumentException in case the supplied buffer isn't a recognized transport
     */
    public static Transport of(final Buffer buffer) throws IllegalArgumentException {
        if (buffer == null || buffer.isEmpty()) {
            throw new IllegalArgumentException("Illegal Transport - the transport buffer cannot be null or empty");
        }

        if (SipParser.isUDP(buffer) || SipParser.isUDPLower(buffer)) {
            return udp;
        }

        if (SipParser.isTCP(buffer) || SipParser.isTCPLower(buffer)) {
            return tcp;
        }

        if (SipParser.isTLS(buffer) || SipParser.isTLSLower(buffer)) {
            return tls;
        }

        if (SipParser.isSCTP(buffer) || SipParser.isSCTPLower(buffer)) {
            return sctp;
        }

        if (SipParser.isWS(buffer) || SipParser.isWSLower(buffer)) {
            return ws;
        }

        if (SipParser.isWSS(buffer) || SipParser.isWSSLower(buffer)) {
            return wss;
        }

        throw new IllegalArgumentException("Illegal Transport - Unknown transport \"" + buffer + "\"");
    }

    public static Transport of(final String buffer) throws IllegalArgumentException {
        if (buffer == null || buffer.isEmpty()) {
            throw new IllegalArgumentException("Illegal Transport - the transport buffer cannot be null or empty");
        }
        return of(Buffers.wrap(buffer));
    }

    public Buffer toBuffer() {
        return buffer;
    }

    public Buffer toUpperCaseBuffer() {
        return upperCaseBuffer;
    }
}
