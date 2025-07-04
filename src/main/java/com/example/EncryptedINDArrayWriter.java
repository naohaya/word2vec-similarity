/***
 * EncryptedINDArrayWriter.java
 * * This class provides functionality to serialize an INDArray, encrypt it using AES,
 * and write the encrypted data to a file.
 * * It includes methods to generate an AES key, serialize the INDArray, encrypt the data,
 * and write the encrypted data to a specified file.
 * * Note: This code requires the ND4J library for INDArray operations and Java's
 * javax.crypto package for encryption.
 * * Usage:
 * 1. Create an instance of EncryptedINDArrayWriter with a SecretKey.
 * 2. Use the `writeEncryptedINDArrayToFile` method to serialize, encrypt, and write the INDArray to a file.
 * 3. Ensure that the AES key is securely managed, as it is required for decryption.
 * * Example:
 * ```java
 * // AES鍵を生成
 * SecretKey key = EncryptedINDArrayWriter.generateAESKey();    
 * // 3x3のINDArrayを作成
 * INDArray array = Nd4j.rand(3, 3);
 * // EncryptedINDArrayWriterのインスタンスを作成
 * EncryptedINDArrayWriter writer = new EncryptedINDArrayWriter(key);
 * // ファイルに保存
 * String outputFileName = "encrypted_array.bin";
 * writer.writeEncryptedINDArrayToFile(array, key, outputFileName);
 * System.out.println("暗号化して保存しました: " + outputFileName);
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
import org.nd4j.linalg.factory.Nd4j;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;

public class EncryptedINDArrayWriter {
    private final SecretKey key;

    public EncryptedINDArrayWriter(SecretKey key) {
        this.key = key; // 鍵はコンストラクタで受け取る
    }
    // AES暗号用の鍵を生成（または読み込み）
    public static SecretKey generateAESKey() throws Exception {
        KeyGenerator keyGen = KeyGenerator.getInstance("AES");
        keyGen.init(128);  // AES-128
        return keyGen.generateKey();
    }

    // INDArrayをバイト配列にシリアライズ
    public static byte[] serializeINDArray(INDArray array) throws IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(bos);
        oos.writeObject(array);
        oos.flush();
        return bos.toByteArray();
    }

    // AESで暗号化
    public static byte[] encrypt(byte[] data, SecretKey key) throws Exception {
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.ENCRYPT_MODE, key);
        return cipher.doFinal(data);
    }

    // ファイルに書き込み
    public static void writeEncryptedINDArrayToFile(INDArray array, SecretKey key, String outputFileName) throws Exception {
        File outputFile = new File(outputFileName);
        /*
        if (!outputFile.getParentFile().exists()) {
            outputFile.getParentFile().mkdirs(); // 親ディレクトリが存在しない場合は作成
        }
        if (!outputFile.exists()) {
            boolean created = outputFile.createNewFile(); // ファイルが存在しない場合は作成
            if (!created) {
                throw new IOException("Failed to create file: " + outputFile.getAbsolutePath());
            }
        }
        */
        // INDArrayをシリアライズして暗号化
        byte[] serialized = serializeINDArray(array);
        byte[] encrypted = encrypt(serialized, key);

        try (FileOutputStream fos = new FileOutputStream(outputFile)) {
            fos.write(encrypted);
        }
    }

    /******
    // 使用例
    public static void main(String[] args) throws Exception {
        // 例：3x3のINDArrayを作成
        INDArray array = Nd4j.rand(3, 3);

        // 鍵を生成（本番では保存・管理が必要）
        SecretKey key = generateAESKey();

        // ファイルへ保存
        File file = new File("encrypted_array.bin");
        writeEncryptedINDArrayToFile(array, key, file);

        System.out.println("暗号化して保存しました: " + file.getAbsolutePath());
    }
     **/
}
