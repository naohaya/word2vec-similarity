# word2vec-similarity
## Description
This program calculates the cosine similarity between two sentences using Word2Vec embeddings.
It computes the average vector for each sentence and then calculates the cosine similarity.
Additionally, it generates Locality Sensitive Hashing (LSH) hashes for the sentence vectors　and computes the Hamming distance between the hashes.
## Requirements
* Ensure you have a pre-trained Word2Vec model (e.g., GoogleNews-vectors-negative300.bin).
* Place the model file at the top directory of the project.
## Compile and run 
### Compile
`mvn clean package`
### Run
`mvn exec:java -Dexec.mainClass="com.example.Word2VecSimilarity"`
or
`java -jar target/word2vec-similarity-1.0-SNAPSHOT-jar-with-dependencies.jar`