package com.makro.mall.common.util.hash;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.zip.CRC32;

/**
 * @author xiaojunfeng
 * @description
 * @date 2022/8/2
 */
public enum HashAlgorithm {

    /**
     * Native hash (String.hashCode()).
     */
    NATIVE_HASH,
    /**
     * CRC32_HASH as used by the perl API. This will be more consistent both
     * across multiple API users as well as java versions, but is mostly likely
     * significantly slower.
     */
    CRC32_HASH,
    /**
     * FNV hashes are designed to be fast while maintaining a low collision
     * rate. The FNV speed allows one to quickly hash lots of data while
     * maintaining a reasonable collision rate.
     * <p/>
     * //         * @see http://www.isthe.com/chongo/tech/comp/fnv/
     * //         * @see http://en.wikipedia.org/wiki/Fowler_Noll_Vo_hash
     */
    FNV1_64_HASH,
    /**
     * Variation of FNV.
     */
    FNV1A_64_HASH,
    /**
     * 32-bit FNV1.
     */
    FNV1_32_HASH,
    /**
     * 32-bit FNV1a.
     */
    FNV1A_32_HASH,
    /**
     * MD5-based hash algorithm used by ketama.
     */
    KETAMA_HASH,

    /**
     * From mysql source
     */
    MYSQL_HASH,

    ELF_HASH,

    RS_HASH,

    /**
     * From lua source,it is used for long key
     */
    LUA_HASH,
    /**
     * MurMurHash算法，是非加密HASH算法，性能很高，
     * 比传统的CRC32,MD5，SHA-1（这两个算法都是加密HASH算法，复杂度本身就很高，带来的性能上的损害也不可避免）
     * 等HASH算法要快很多，这个算法的碰撞率很低.
     * http://murmurhash.googlepages.com/
     */
    MurMurHash,
    /**
     * The Jenkins One-at-a-time hash ,please see
     * http://www.burtleburtle.net/bob/hash/doobs.html
     */
    ONE_AT_A_TIME;

    private static final long FNV_64_INIT = 0xcbf29ce484222325L;
    private static final long FNV_64_PRIME = 0x100000001b3L;

    private static final long FNV_32_INIT = 2166136261L;
    private static final long FNV_32_PRIME = 16777619;

    /**
     * Compute the hash for the given key.
     *
     * @return a positive integer hash
     */
    public long hash(final String k) {
        long rv = 0;
        switch (this) {
            case NATIVE_HASH:
                rv = k.hashCode();
                break;
            case CRC32_HASH:
                // return (crc32(shift) >> 16) & 0x7fff;
                CRC32 crc32 = new CRC32();
                crc32.update(k.getBytes());
                rv = crc32.getValue() >> 16 & 0x7fff;
                break;
            case FNV1_64_HASH: {
                // Thanks to pierre@demartines.com for the pointer
                rv = FNV_64_INIT;
                int len = k.length();
                for (int i = 0; i < len; i++) {
                    rv *= FNV_64_PRIME;
                    rv ^= k.charAt(i);
                }
            }
            break;
            case MurMurHash:
                ByteBuffer buf = ByteBuffer.wrap(k.getBytes());
                int seed = 0x1234ABCD;

                ByteOrder byteOrder = buf.order();
                buf.order(ByteOrder.LITTLE_ENDIAN);

                long m = 0xc6a4a7935bd1e995L;
                int r = 47;

                rv = seed ^ (buf.remaining() * m);

                long ky;
                while (buf.remaining() >= 8) {
                    ky = buf.getLong();

                    ky *= m;
                    ky ^= ky >>> r;
                    ky *= m;

                    rv ^= ky;
                    rv *= m;
                }

                if (buf.remaining() > 0) {
                    ByteBuffer finish = ByteBuffer.allocate(8).order(
                            ByteOrder.LITTLE_ENDIAN);
                    // for big-endian version, do this first:
                    // finish.position(8-buf.remaining());
                    finish.put(buf).rewind();
                    rv ^= finish.getLong();
                    rv *= m;
                }

                rv ^= rv >>> r;
                rv *= m;
                rv ^= rv >>> r;
                buf.order(byteOrder);
                break;
            case FNV1A_64_HASH: {
                rv = FNV_64_INIT;
                int len = k.length();
                for (int i = 0; i < len; i++) {
                    rv ^= k.charAt(i);
                    rv *= FNV_64_PRIME;
                }
            }
            break;
            case FNV1_32_HASH: {
                rv = FNV_32_INIT;
                int len = k.length();
                for (int i = 0; i < len; i++) {
                    rv *= FNV_32_PRIME;
                    rv ^= k.charAt(i);
                }
            }
            break;
            case FNV1A_32_HASH: {
                rv = FNV_32_INIT;
                int len = k.length();
                for (int i = 0; i < len; i++) {
                    rv ^= k.charAt(i);
                    rv *= FNV_32_PRIME;
                }
            }
            break;
            case KETAMA_HASH:
                byte[] bKey = computeMd5(k);
                rv = (long) (bKey[3] & 0xFF) << 24 | (long) (bKey[2] & 0xFF) << 16
                        | (long) (bKey[1] & 0xFF) << 8 | bKey[0] & 0xFF;
                break;

            case MYSQL_HASH:
                int nr2 = 4;
                for (int i = 0; i < k.length(); i++) {
                    rv ^= ((rv & 63) + nr2) * k.charAt(i) + (rv << 8);
                    nr2 += 3;
                }
                break;
            case ELF_HASH:
                long x = 0;
                for (int i = 0; i < k.length(); i++) {
                    rv = (rv << 4) + k.charAt(i);
                    if ((x = rv & 0xF0000000L) != 0) {
                        rv ^= x >> 24;
                        rv &= ~x;
                    }
                }
                rv = rv & 0x7FFFFFFF;
                break;
            case RS_HASH:
                long b = 378551;
                long a = 63689;
                for (int i = 0; i < k.length(); i++) {
                    rv = rv * a + k.charAt(i);
                    a *= b;
                }
                rv = rv & 0x7FFFFFFF;
                break;
            case LUA_HASH:
                int step = (k.length() >> 5) + 1;
                rv = k.length();
                for (int len = k.length(); len >= step; len -= step) {
                    rv = rv ^ (rv << 5) + (rv >> 2) + k.charAt(len - 1);
                }
            case ONE_AT_A_TIME:
                int hash = 0;
                for (byte bt : k.getBytes(StandardCharsets.UTF_8)) {
                    hash += (bt & 0xFF);
                    hash += (hash << 10);
                    hash ^= (hash >>> 6);
                }
                hash += (hash << 3);
                hash ^= (hash >>> 11);
                hash += (hash << 15);
                return hash;
            default:
                assert false;
        }

        return rv & 0xffffffffL; /* Truncate to 32-bits */
    }

    /**
     * Get the md5 of the given key.
     */
    public static byte[] computeMd5(String k) {
        MessageDigest md5;
        try {
            md5 = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("MD5 not supported", e);
        }
        md5.reset();
        md5.update(k.getBytes());
        return md5.digest();
    }

}
