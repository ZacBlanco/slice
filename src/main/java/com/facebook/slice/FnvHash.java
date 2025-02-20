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

/**
 * Reference implementation: https://tools.ietf.org/html/draft-eastlake-fnv-17#section-6
 */
public class FnvHash
{
    private static final long FNV_64_OFFSET_BASIS = 0xcbf29ce484222325L;
    private static final long FNV_64_PRIME = 0x100000001b3L;

    private static final int FNV_32_OFFSET_BASIS = 0x811c9dc5;
    private static final int FNV_32_PRIME = 0x01000193;

    private FnvHash()
    {
    }

    public static int hash32(Slice data)
    {
        int hash = FNV_32_OFFSET_BASIS;

        for (int i = 0; i < data.length(); ++i) {
            byte dataByte = data.getByte(i);
            hash ^= dataByte;
            hash *= FNV_32_PRIME;
        }

        return hash;
    }

    public static long hash64(Slice data)
    {
        long hash = FNV_64_OFFSET_BASIS;

        for (int i = 0; i < data.length(); ++i) {
            byte dataByte = data.getByte(i);
            hash ^= dataByte;
            hash *= FNV_64_PRIME;
        }

        return hash;
    }
}
