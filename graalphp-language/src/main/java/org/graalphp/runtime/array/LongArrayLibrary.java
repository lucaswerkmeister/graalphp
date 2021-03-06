package org.graalphp.runtime.array;

import com.oracle.truffle.api.CompilerDirectives.TruffleBoundary;
import com.oracle.truffle.api.dsl.GenerateUncached;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.library.CachedLibrary;
import com.oracle.truffle.api.library.ExportLibrary;
import com.oracle.truffle.api.library.ExportMessage;

import java.util.Arrays;

/**
 * Array backend for long[] based values
 *
 * @author abertschi
 */
@ExportLibrary(value = ArrayLibrary.class, receiverType = long[].class)
@GenerateUncached
public class LongArrayLibrary {

    // Semantic messages

    @ExportMessage
    protected static boolean isArray(long[] store) {
        return true;
    }

    @ExportMessage
    protected static long read(long[] store, int index) {
        return store[index];
    }

    @ExportMessage
    static class Write {
        @Specialization
        protected static void write(long[] store, int index, long value) {
            store[index] = value;
        }
    }

    // technical messages

    @ExportMessage
    protected static boolean acceptsValue(long[] receiver, Object value) {
        return value instanceof Long;
    }

    @ExportMessage
    protected static LongArrayAllocator getArrayAllocator(long[] receiver) {
        return LongArrayAllocator.INSTANCE;
    }

    @ExportMessage
    @TruffleBoundary
    protected static String arrayToString(long[] receiver) {
        return Arrays.toString(receiver);
    }

    @ExportMessage
    static class GeneralizeForValue {
        @Specialization
        protected static ArrayAllocator generalizeForValue(long[] receiver, long newValue) {
            return LongArrayAllocator.INSTANCE;
        }

        @Specialization
        protected static ArrayAllocator generalizeForValue(long[] receiver, Object newValue) {
            return ObjectArrayAllocator.INSTANCE;
        }
    }

    @ExportMessage
    protected static int capacity(long[] receiver) {
        return receiver.length;
    }

    @ExportMessage
    protected static long[] grow(long[] receiver, int newSize) {
        return Arrays.copyOf(receiver, newSize);
    }

    @ExportMessage
    static class CopyContents {
        @Specialization(limit = ArrayLibrary.SPECIALIZATION_LIMIT)
        protected static void copyContents(long[] receiver,
                                           Object destination,
                                           int length,
                                           @CachedLibrary("destination") ArrayLibrary destinationLibrary) {
            for (int i = 0; i < length; i++) {
                destinationLibrary.write(destination, i, receiver[i]);
            }
        }
    }

    @ExportMessage
    static class CopyDeepContents {
        // XXX: Same as copy contents as long[] cannot store other arrays
        @Specialization(limit = ArrayLibrary.SPECIALIZATION_LIMIT)
        protected static void copyDeepContents(long[] receiver,
                                               Object destination,
                                               int length,
                                               @CachedLibrary("destination") ArrayLibrary destinationLibrary) {
            for (int i = 0; i < length; i++) {
                destinationLibrary.write(destination, i, receiver[i]);
            }
        }
    }
}
