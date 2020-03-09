package Blockchain.LightChain;

import org.bouncycastle.jcajce.provider.digest.SHA3;

import java.math.BigInteger;

/**
 * Provides hashing tools to be used for numerical id hashing, validator acquisition (Proof-of-Validation) and
 * randomized bootstrapping (the view layer).
 */
public class HashTools {

    private static SHA3.DigestSHA3 SHA3Digest = new SHA3.Digest256();

    /**
     * Compresses the given hash code uniformly into the range [0, N-1] using M-A-D compression.
     * @param hashCode hash code to be compressed.
     * @param N upper bound of the range.
     * @return the compressed value.
     */
    public static BigInteger compress(byte[] hashCode, BigInteger N) {
        BigInteger num = new BigInteger(hashCode);
        BigInteger P = N.nextProbablePrime();
        // (|hash| % P) % N
        return num.abs().mod(P).mod(N);
    }

    /**
     * Compresses the given hash code into an Integer using M-A-D compression.
     * @param hashCode hash code to be compressed.
     * @return the compressed value.
     */
    public static int compressToInt(byte[] hashCode) {
        return compress(hashCode, BigInteger.valueOf(Integer.MAX_VALUE)).intValue();
    }

    /**
     * Performs SHA3 on the given input and returns its result as a byte array.
     * @param input the String to be hashed.
     * @return the digest.
     */
    public static byte[] hash(String input) {
        return SHA3Digest.digest(input.getBytes());
    }
}
