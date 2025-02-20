/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.facebook.slice;

import org.openjdk.jol.info.ClassLayout;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;

import static com.facebook.slice.Preconditions.checkPositionIndex;
import static com.facebook.slice.SizeOf.SIZE_OF_DOUBLE;
import static com.facebook.slice.SizeOf.SIZE_OF_FLOAT;
import static com.facebook.slice.SizeOf.SIZE_OF_INT;
import static com.facebook.slice.SizeOf.SIZE_OF_LONG;
import static com.facebook.slice.SizeOf.SIZE_OF_SHORT;
import static java.util.Objects.requireNonNull;

public final class BasicSliceInput
        extends FixedLengthSliceInput
{
    private static final int INSTANCE_SIZE = ClassLayout.parseClass(BasicSliceInput.class).instanceSize();

    private final Slice slice;
    private int position;

    public BasicSliceInput(Slice slice)
    {
        this.slice = requireNonNull(slice, "slice is null");
    }

    @Override
    public long length()
    {
        return slice.length();
    }

    @Override
    public long position()
    {
        return position;
    }

    @Override
    public void setPosition(long position)
    {
        checkPositionIndex(position, slice.length());
        this.position = (int) position;
    }

    @Override
    public boolean isReadable()
    {
        return position < slice.length();
    }

    @Override
    public int available()
    {
        return slice.length() - position;
    }

    @Override
    public boolean readBoolean()
    {
        return readByte() != 0;
    }

    @Override
    public int read()
    {
        if (position >= slice.length()) {
            return -1;
        }
        int result = slice.getByte(position) & 0xFF;
        position++;
        return result;
    }

    @Override
    public byte readByte()
    {
        int value = read();
        if (value == -1) {
            throw new IndexOutOfBoundsException();
        }
        return (byte) value;
    }

    @Override
    public int readUnsignedByte()
    {
        return readByte() & 0xFF;
    }

    @Override
    public short readShort()
    {
        short v = slice.getShort(position);
        position += SIZE_OF_SHORT;
        return v;
    }

    @Override
    public int readUnsignedShort()
    {
        return readShort() & 0xFFFF;
    }

    @Override
    public int readInt()
    {
        int v = slice.getInt(position);
        position += SIZE_OF_INT;
        return v;
    }

    @Override
    public long readLong()
    {
        long v = slice.getLong(position);
        position += SIZE_OF_LONG;
        return v;
    }

    @Override
    public float readFloat()
    {
        float v = slice.getFloat(position);
        position += SIZE_OF_FLOAT;
        return v;
    }

    @Override
    public double readDouble()
    {
        double v = slice.getDouble(position);
        position += SIZE_OF_DOUBLE;
        return v;
    }

    @Override
    public Slice readSlice(int length)
    {
        if (length == 0) {
            return Slices.EMPTY_SLICE;
        }
        Slice newSlice = slice.slice(position, length);
        position += length;
        return newSlice;
    }

    @Override
    public int read(byte[] destination, int destinationIndex, int length)
    {
        if (length == 0) {
            return 0;
        }

        length = Math.min(length, available());
        if (length == 0) {
            return -1;
        }
        readBytes(destination, destinationIndex, length);
        return length;
    }

    @Override
    public void readBytes(byte[] destination, int destinationIndex, int length)
    {
        slice.getBytes(position, destination, destinationIndex, length);
        position += length;
    }

    @Override
    public void readBytes(Slice destination, int destinationIndex, int length)
    {
        slice.getBytes(position, destination, destinationIndex, length);
        position += length;
    }

    @Override
    public void readBytes(OutputStream out, int length)
            throws IOException
    {
        slice.getBytes(position, out, length);
        position += length;
    }

    @Override
    public long skip(long length)
    {
        length = Math.min(length, available());
        position += length;
        return length;
    }

    @Override
    public int skipBytes(int length)
    {
        length = Math.min(length, available());
        position += length;
        return length;
    }

    @Override
    public long getRetainedSize()
    {
        return INSTANCE_SIZE + slice.getRetainedSize();
    }

    /**
     * Returns a slice of this buffer's readable bytes. Modifying the content
     * of the returned buffer or this buffer affects each other's content
     * while they maintain separate indexes and marks.  This method is
     * identical to {@code buf.slice(buf.position(), buf.available()())}.
     * This method does not modify {@code position} or {@code writerIndex} of
     * this buffer.
     */
    public Slice slice()
    {
        return slice.slice(position, slice.length() - position);
    }

    /**
     * Decodes this buffer's readable bytes into a string with the specified
     * character set name.  This method is identical to
     * {@code buf.toString(buf.position(), buf.available()(), charsetName)}.
     * This method does not modify {@code position} or {@code writerIndex} of
     * this buffer.
     */
    public String toString(Charset charset)
    {
        return slice.toString(position, available(), charset);
    }

    @Override
    public String toString()
    {
        StringBuilder builder = new StringBuilder("BasicSliceInput{");
        builder.append("position=").append(position);
        builder.append(", capacity=").append(slice.length());
        builder.append('}');
        return builder.toString();
    }
}
