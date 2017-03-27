import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class BitsAndBytes {
    @Test
    public void bytes() {
        byte bigByte = (byte) 0xFF;
        int bigInt = 255;
        byte castedBigInt = (byte) bigInt;

        assertTrue((bigByte & 0xFF) == bigInt);
        assertTrue(bigByte == castedBigInt);
    }
}
