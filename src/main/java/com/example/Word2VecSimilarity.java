/***
 * Word2VecSimilarity.java
 * * This program calculates the cosine similarity between two sentences using Word2Vec embeddings.
 * It computes the average vector for each sentence and then calculates the cosine similarity.
 * * Additionally, it generates Locality Sensitive Hashing (LSH) hashes for the sentence vectors
 * and computes the Hamming distance between the hashes.
 * * Note: This code requires the Deeplearning4j library for Word2Vec and ND4J for numerical computations.
 * * Usage:
 * 1. Ensure you have a pre-trained Word2Vec model (e.g., GoogleNews-vectors-negative300.bin).
 * 2. Place the model file at the top directory of the project.
 * 3. Compile and run the program.
 * 4. Input two sentences when prompted.
 * 5. The program will output the cosine similarity and LSH hashes for the sentences.
 * * Execution: 
 * * * Execution with fat JAR:
 * * ```bash
 * java -jar target/word2vec-similarity-1.0-SNAPSHOT-jar-with-dependencies.jar
 * * ```
 * * * Execution with mvn:
 * * ```bash
 * mvn exec:java -Dexec.mainClass="com.example.Word2VecSimilarity"
 * * ```
 * * * Execution with mannual classpath: (not recommended)
 * * ```bash
 * java -cp "target/word2vec-similarity-1.0-SNAPSHOT.jar:~/.m2/repository/..." com.example.Word2VecSimilarity
 * * ```
 * * Example Input/Output:
  * ```
  * 1つ目の文を入力してください: I love programming.
  * 2つ目の文を入力してください: Programming is my passion.
  * 文の類似度（Cosine Similarity）: 0.7321
  * 文1のLSHハッシュ: 11001010101010101010101010101010
  * 文2のLSHハッシュ: 11001010101010101010101010101011
  * ハミング距離: 1
  * ```
 *
 * * Note: Ensure that the Deeplearning4j and ND4J libraries are properly configured in your project.
 * * Dependencies:
 * - Deeplearning4j: https://deeplearning4j.org/docs/latest/deeplearning4j-overview
 * - ND4J: https://deeplearning4j.org/docs/latest/nd4j-overview
 * - Tested with Java 17
 * * This code is a simplified example and may require additional error handling and optimizations for production use.
 * * License: Apache License 2.0
 * @author Naohiro Hayashibara
 * @version 1.0
 * @since 2025-07-04    
 */

package com.example;

import org.deeplearning4j.models.embeddings.loader.WordVectorSerializer;
import org.deeplearning4j.models.embeddings.wordvectors.WordVectors;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;
import org.nd4j.linalg.ops.transforms.Transforms;

import java.io.File;
import java.util.Scanner;
import java.util.logging.Logger;
import javax.crypto.SecretKey;

public class Word2VecSimilarity {

    // 文の平均ベクトルを計算
    public static INDArray sentenceVector(String sentence, WordVectors wordVectors) {
        String[] words = sentence.split("\\s+");
        INDArray sumVec = null;
        int validWords = 0;

        for (String word : words) {
            if (wordVectors.hasWord(word)) {
                INDArray wordVec = wordVectors.getWordVectorMatrix(word);
                if (sumVec == null) {
                    sumVec = wordVec.dup();
                } else {
                    sumVec.addi(wordVec);
                }
                validWords++;
            }
        }

        if (sumVec != null && validWords > 0) {
            sumVec.divi(validWords);  // 平均ベクトル
        }

        return sumVec;
    }

    public static void main(String[] args) throws Exception {
        SecretKey key = EncryptedINDArrayWriter.generateAESKey(); // AES鍵の生成
        Scanner scanner = new Scanner(System.in); // ユーザー入力のためのスキャナー

        System.out.print("1つ目の文を入力してください: ");
        String sentence1 = scanner.nextLine();

        System.out.print("2つ目の文を入力してください: ");
        String sentence2 = scanner.nextLine();

        // Word2Vecモデルの読み込み（GoogleNewsなどの事前学習モデルが必要）
        File modelFile = new File("GoogleNews-vectors-negative300.bin"); // Pre-trained Model(バイナリ形式)
        WordVectors wordVectors = WordVectorSerializer.readWord2VecModel(modelFile);

        INDArray vec1 = sentenceVector(sentence1, wordVectors);
        INDArray vec2 = sentenceVector(sentence2, wordVectors);

        // ★ 修正：暗号化してファイルに保存
        // EncryptedINDArrayWriterを使用して、ベクトルを暗号化してファイルに保存
        // ここでは、暗号化されたINDArrayをファイルに書き込むためのクラスを使用します。
        // EncryptedINDArrayWriterは、AES暗号化を使用してINDArrayをファイルに保存します。
        // // 事前に生成したAES鍵を使用して、INDArrayを暗号化します
        // // そして、暗号化されたデータをファイルに書き込みます。
        EncryptedINDArrayWriter writer = new EncryptedINDArrayWriter(key);
        // ★ 修正：暗号化してファイルに保存
        writer.writeEncryptedINDArrayToFile(vec1, key, "vec1.bin");
        writer.writeEncryptedINDArrayToFile(vec2, key, "vec2.bin");
        // ★ 修正：復号化してINDArrayを読み込む
        vec1 = EncryptedINDArrayReader.readEncryptedINDArrayFromFile(new File("vec1.bin"), key);
        vec2 = EncryptedINDArrayReader.readEncryptedINDArrayFromFile(new File("vec2.bin"), key);    

        if (vec1 == null || vec2 == null) {
            System.out.println("どちらかの文に有効な単語が含まれていません。");
            return;
        }

        // ★ 修正：コサイン類似度を直接計算
        double similarity = Transforms.cosineSim(vec1, vec2);
        System.out.printf("文の類似度（Cosine Similarity）: %.4f\n", similarity);

        // LSHハッシュ生成
        int numBits = 32; // 任意のビット数
        LSH hasher = new LSH(numBits, (int)vec1.length(), 42L); // 42は乱数の種

        String hash1 = hasher.computeHash(vec1);
        String hash2 = hasher.computeHash(vec2);

        System.out.println("文1のLSHハッシュ: " + hash1);
        System.out.println("文2のLSHハッシュ: " + hash2);
        System.out.println("ハミング距離: " + LSH.hammingDistance(hash1, hash2));
    }
}
