/***
 * EncryptedINDArrayReader.java
 * * This class provides methods to read and decrypt INDArray objects from encrypted files.
 * * It uses AES encryption for secure storage and retrieval of INDArray data.
 * * The class includes methods to decrypt the data, deserialize it into an INDArray,
 * and read the encrypted INDArray from a specified file.
 * * Note: This code requires the ND4J library for INDArray operations and Java's
 * javax.crypto package for encryption and decryption.
 * * Usage:
 * 1. Ensure you have a pre-generated AES key for decryption.
 * 2. Use the `readEncryptedINDArrayFromFile` method to read and decrypt the INDArray from a file.
 * 3. The method returns the decrypted INDArray object.
 * * Example:
 * ```java
 * // AES鍵を生成または取得（保存していた鍵を使用する必要があります）
 * byte[] keyBytes = new byte[16]; // 適切に保存された鍵を使用すること
 * Arrays.fill(keyBytes, (byte) 1); // ※例：全バイト1（本番ではNG）
 * SecretKey key = new SecretKeySpec(keyBytes, "AES");  
 * // ファイルからINDArrayを読み出す
 * File file = new File("encrypted_array.bin");
 * INDArray array = EncryptedINDArrayReader.readEncryptedINDArrayFromFile(file, key);
 * System.out.println("復号されたINDArray:");
 * System.out.println(array);
 * ```
 * * Note: Ensure that the ND4J library and Java's crypto libraries are properly configured in your project.
 * * Dependencies:
 * - ND4J: https://deeplearning4j.org/docs/latest/nd4j-overview
 * - Java Cryptography Extension (JCE): Included in standard Java libraries
 * * This code is a simplified example and may require additional error handling and optimizations for production use.
 * * License: Apache License 2.0
 * @author Naohiro Hayashibara
 * @version 1.0
 * @since 2025-07-04
 */

package com.example;

import org.nd4j.linalg.api.ndarray.INDArray;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.util.Arrays;

public class EncryptedINDArrayReader {

    // 復号処理（AES）
    public static byte[] decrypt(byte[] encryptedData, SecretKey key) throws Exception {
        Cipher cipher = Cipher.getInstance("AES"); // 簡易例：AES/ECB/PKCS5Padding
        cipher.init(Cipher.DECRYPT_MODE, key);
        return cipher.doFinal(encryptedData);
    }

    // バイト列 → INDArray の逆シリアライズ
    public static INDArray deserializeINDArray(byte[] data) throws IOException, ClassNotFoundException {
        ByteArrayInputStream bis = new ByteArrayInputStream(data);
        ObjectInputStream ois = new ObjectInputStream(bis);
        return (INDArray) ois.readObject();
    }

    // ファイルから復号してINDArrayとして読み出す
    public static INDArray readEncryptedINDArrayFromFile(File inputFile, SecretKey key) throws Exception {
        byte[] encrypted;
        try (FileInputStream fis = new FileInputStream(inputFile)) {
            encrypted = fis.readAllBytes();
        }

        byte[] decrypted = decrypt(encrypted, key);
        return deserializeINDArray(decrypted);
    }

    /*
    // 使用例
    public static void main(String[] args) throws Exception {
        // 同じ鍵を再生成（保存していた鍵を復元する必要があります）
        // ここでは例として同じバイト配列を使った固定鍵
        byte[] keyBytes = new byte[16]; // 適切に保存された鍵を使用すること
        Arrays.fill(keyBytes, (byte) 1); // ※例：全バイト1（本番ではNG）
        SecretKey key = new SecretKeySpec(keyBytes, "AES");

        File file = new File("encrypted_array.bin");
        INDArray array = readEncryptedINDArrayFromFile(file, key);

        System.out.println("復号されたINDArray:");
        System.out.println(array);
    }
        */
}
