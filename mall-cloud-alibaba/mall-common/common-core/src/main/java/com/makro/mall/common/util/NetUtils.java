package com.makro.mall.common.util;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.*;
import java.util.Enumeration;
import java.util.Objects;
import java.util.Optional;
import java.util.regex.Pattern;

/**
 * @author xiaojunfeng
 * @description
 * @date 2022/10/12
 */
@Slf4j
public class NetUtils {

    private static final Pattern IP_PATTERN = Pattern.compile("\\d{1,3}(\\.\\d{1,3}){3,5}$");
    private static final String ANY_HOST_VALUE = "0.0.0.0";
    private static final String LOCALHOST_VALUE = "127.0.0.1";
    private static volatile String HOST_ADDRESS;
    private static volatile InetAddress LOCAL_ADDRESS = null;

    public static String getLocalHost(String preferredNetworkInterface) {
        if (HOST_ADDRESS != null) {
            return HOST_ADDRESS;
        }
        InetAddress address = getLocalAddress(preferredNetworkInterface);
        if (address != null) {
            return HOST_ADDRESS = address.getHostAddress();
        }
        return LOCALHOST_VALUE;
    }

    public static InetAddress getLocalAddress(String preferredNetworkInterface) {
        if (LOCAL_ADDRESS != null) {
            return LOCAL_ADDRESS;
        }
        InetAddress localAddress = getLocalAddress0(preferredNetworkInterface);
        LOCAL_ADDRESS = localAddress;
        return localAddress;
    }

    public static InetAddress getLocalAddress0(String preferNetWorkInterface) {
        InetAddress localAddress = null;
        try {
            NetworkInterface networkInterface = findTargetNetworkInterface(preferNetWorkInterface);
            Enumeration<InetAddress> addresses = networkInterface.getInetAddresses();
            while (addresses.hasMoreElements()) {
                Optional<InetAddress> addressOp = toValidAddress(addresses.nextElement());
                if (addressOp.isPresent()) {
                    try {
                        if (addressOp.get().isReachable(100)) {
                            return addressOp.get();
                        }
                    } catch (IOException e) {
                        // ignore
                    }
                }
            }
        } catch (Throwable e) {
            log.warn("[Net] getLocalAddress0 failed.", e);
        }

        try {
            localAddress = InetAddress.getLocalHost();
            Optional<InetAddress> addressOp = toValidAddress(localAddress);
            if (addressOp.isPresent()) {
                return addressOp.get();
            }
        } catch (Throwable e) {
            log.warn("[Net] getLocalAddress0 failed.", e);
        }


        return localAddress;
    }

    private static NetworkInterface findTargetNetworkInterface(String preferredNetworkInterface) throws SocketException {
        Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
        while (interfaces.hasMoreElements()) {
            NetworkInterface networkInterface = interfaces.nextElement();
            if (isPreferredNetworkInterface(networkInterface, preferredNetworkInterface)) {
                return networkInterface;
            }
        }
        return null;
    }

    public static boolean isPreferredNetworkInterface(NetworkInterface networkInterface, String preferredNetworkInterface) {
        if (Objects.equals(networkInterface.getDisplayName(), preferredNetworkInterface)) {
            return true;
        }
        return Objects.equals(networkInterface.getName(), preferredNetworkInterface);
    }

    private static Optional<InetAddress> toValidAddress(InetAddress address) {
        if (address instanceof Inet6Address) {
            Inet6Address v6Address = (Inet6Address) address;
            if (isPreferIPV6Address()) {
                return Optional.ofNullable(normalizeV6Address(v6Address));
            }
        }
        if (isValidV4Address(address)) {
            return Optional.of(address);
        }
        return Optional.empty();
    }

    static boolean isPreferIPV6Address() {
        return Boolean.getBoolean("java.net.preferIPv6Addresses");
    }

    static boolean isValidV4Address(InetAddress address) {
        if (address == null || address.isLoopbackAddress()) {
            return false;
        }

        String name = address.getHostAddress();
        return (name != null
                && IP_PATTERN.matcher(name).matches()
                && !ANY_HOST_VALUE.equals(name)
                && !LOCALHOST_VALUE.equals(name));
    }

    static InetAddress normalizeV6Address(Inet6Address address) {
        String addr = address.getHostAddress();
        int i = addr.lastIndexOf('%');
        if (i > 0) {
            try {
                return InetAddress.getByName(addr.substring(0, i) + '%' + address.getScopeId());
            } catch (UnknownHostException e) {
                // ignore
                log.debug("Unknown IPV6 address: ", e);
            }
        }
        return address;
    }

}
