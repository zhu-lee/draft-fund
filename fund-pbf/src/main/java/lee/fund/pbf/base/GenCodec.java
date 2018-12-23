package lee.fund.pbf.base;

import lee.fund.pbf.a3.Codec;

import java.io.IOException;

/**
 * Author: zhu.li
 * Since:  jdk 1.8
 * Date:   Created in 2018/12/23 11:57
 * Desc:   Codec implementation of the base class
 */
public abstract class GenCodec<T> implements Codec<T> {

    @Override
    public byte[] encode(T t) throws IOException {
        return new byte[0];
    }

    @Override
    public T decode(byte[] bytes) throws IOException {
        return null;
    }
}
