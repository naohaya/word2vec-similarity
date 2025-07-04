/***
 * Locality Sensitive Hashing (LSH) implementation in Java using ND4J.
 * This class generates random hyperplanes and computes hash values for vectors.
 * It also provides a method to compute the Hamming distance between two hash values.
 * * Note: This implementation uses ND4J for numerical computations and assumes
 * the ND4J library is included in your project dependencies.
 * * Usage:
 * 1. Create an instance of LSH with the desired number of hash bits and vector dimension.
 * 2. Use the `computeHash` method to get the hash value for a given vector.
 * 3. Use the `hammingDistance` method to compute the distance between two hash values.
 * * Example:
 * ```java
 * LSH lsh = new LSH(32, 300, 42L); // 32ビットのハッシュ、300次元のベクトル、乱数の種42
 * INDArray vector = Nd4j.create(new float[]{...}); // 任意の300次元ベクトル
 * String hash = lsh.computeHash(vector); // ハッシュ値を計算
 * String hash2 = lsh.computeHash(anotherVector); // 別のベクトルのハッシュ値を計算
 * int distance = LSH.hammingDistance(hash, hash2); // ハミング距離を計算
 * System.out.println("ハミング距離: " + distance);
 * ```
 * * Note: Ensure that the ND4J library is properly configured in your project.
 * * Dependencies:
 * - ND4J: https://deeplearning4j.org/docs/latest/nd4j-overview
 * - Java 8 or higher
 * * This code is a simplified example and may require additional error handling and optimizations for production use.
 * * License: Apache License 2.0
 * @author Naohiro Hayashibara
 * @version 1.0
 * @since 2025-07-04
 */

package com.example;

import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;

import java.util.Random;

public class LSH {
    private final int numHashBits;
    private final INDArray[] randomHyperplanes;

    public LSH(int numHashBits, int dimension, long seed) {
        this.numHashBits = numHashBits;
        this.randomHyperplanes = new INDArray[numHashBits];
        Random rng = new Random(seed);

        for (int i = 0; i < numHashBits; i++) {
            float[] randVector = new float[dimension]; // ← float配列に変更
            for (int j = 0; j < dimension; j++) {
                randVector[j] = (float) rng.nextGaussian(); // double → float
            }
            randomHyperplanes[i] = Nd4j.create(randVector); // float配列を渡す
        }
    }

    // ハッシュ値をビット列で返す（文字列形式）
    public String computeHash(INDArray vector) {
        StringBuilder hash = new StringBuilder();

        for (INDArray hyperplane : randomHyperplanes) {
            double dot = Nd4j.getBlasWrapper().dot(vector, hyperplane);
            hash.append(dot >= 0 ? "1" : "0");
        }

        return hash.toString(); // 例: "1010100101"
    }

    // ハミング距離（任意の比較用）
    public static int hammingDistance(String hash1, String hash2) {
        if (hash1.length() != hash2.length()) {
            throw new IllegalArgumentException("Hash lengths must match.");
        }

        int distance = 0;
        for (int i = 0; i < hash1.length(); i++) {
            if (hash1.charAt(i) != hash2.charAt(i)) {
                distance++;
            }
        }
        return distance;
    }
}