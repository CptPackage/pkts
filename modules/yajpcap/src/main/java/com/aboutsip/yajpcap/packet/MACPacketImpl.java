/**
 * 
 */
package com.aboutsip.yajpcap.packet;

import java.io.IOException;
import java.io.OutputStream;

import com.aboutsip.buffer.Buffer;

/**
 * @author jonas@jonasborjesson.com
 */
public final class MACPacketImpl implements MACPacket {

    private final Packet parentPacket;
    private final String sourceMacAddress;
    private final String destinationMacAddress;

    /**
     * If the headers are set then this overrides any of the source stuff set
     * above.
     */
    private final Buffer headers;

    public static MACPacketImpl create(final Packet packet, final Buffer headers) {
        if (headers.capacity() != 14) {
            throw new IllegalArgumentException("Not enough bytes to create this header");
        }

        if (packet == null) {
            // throw new IllegalArgumentException("The parent packet cannot be null");
        }

        return new MACPacketImpl(packet, headers);
    }

    /**
     * Construct a new {@link MACPacket} based on the supplied headers.
     * 
     * @param packet
     * @param headers
     */
    public MACPacketImpl(final Packet packet, final Buffer headers) {
        this.parentPacket = packet;
        this.headers = headers;
        this.sourceMacAddress = null;
        this.destinationMacAddress = null;
    }

    /**
     * 
     */
    public MACPacketImpl(final Packet packet, final String sourceMacAddress, final String destinationMacAddress) {
        assert packet != null;
        assert sourceMacAddress != null && !sourceMacAddress.isEmpty();
        assert destinationMacAddress != null && !destinationMacAddress.isEmpty();
        this.parentPacket = packet;
        this.sourceMacAddress = sourceMacAddress;
        this.destinationMacAddress = destinationMacAddress;
        this.headers = null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final String getSourceMacAddress() {
        if (this.sourceMacAddress != null) {
            return this.sourceMacAddress;
        }

        try {
            return toHexString(this.headers, 0, 6);
        } catch (final IOException e) {
            throw new RuntimeException("Unable to read data from the underlying Buffer.", e);
        }
    }

    public static String toHexString(final Buffer buffer, final int start, final int length) throws IOException {
        final StringBuilder sb = new StringBuilder();
        for (int i = start; i < start + length; ++i) {
            final byte b = buffer.getByte(i);
            sb.append(String.format("%02X", b));
            if (i < start + length - 1) {
                sb.append(":");
            }
        }
        return sb.toString();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final String getDestinationMacAddress() {
        if (this.destinationMacAddress != null) {
            return this.destinationMacAddress;
        }

        try {
            return toHexString(this.headers, 6, 6);
        } catch (final IOException e) {
            throw new RuntimeException("Unable to read data from the underlying Buffer.", e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void verify() {
        // nothing to verify
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("Destination Mac Address: ").append(this.destinationMacAddress);
        sb.append(" Source Mac Address: ").append(this.sourceMacAddress);
        return sb.toString();
    }

    @Override
    public long getArrivalTime() {
        return this.parentPacket.getArrivalTime();
    }

    @Override
    public void write(final OutputStream out) throws IOException {
        throw new RuntimeException("Sorry, not implemented just yet.");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setSourceMacAddress(final String macAddress) {
        setMacAddress(macAddress, true);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setDestinationMacAddress(final String macAddress) {
        setMacAddress(macAddress, false);
    }

    /**
     * Helper method for setting the mac address in the header buffer.
     * 
     * @param macAddress
     *            the mac address to parse
     * @param setSourceMacAddress
     *            whether this is to bet set as the source mac address or not.
     *            False implies destination mac address of course.
     * @throws IllegalArgumentException
     */
    private void setMacAddress(final String macAddress, final boolean setSourceMacAddress)
            throws IllegalArgumentException {
        if (macAddress == null || macAddress.isEmpty()) {
            throw new IllegalArgumentException("Null or empty string cannot be a valid MAC Address.");
        }
        // very naive implementation first.
        final String[] segments = macAddress.split(":");
        if (segments.length != 6) {
            throw new IllegalArgumentException("Invalid MAC Address. Not enough segments");
        }

        final int offset = setSourceMacAddress ? 0 : 6;
        for (int i = 0; i < 6; ++i) {
            final byte b = (byte) ((Character.digit(segments[i].charAt(0), 16) << 4) + Character.digit(segments[i]
                    .charAt(1), 16));
            this.headers.setByte(i + offset, b);
        }
    }


}
