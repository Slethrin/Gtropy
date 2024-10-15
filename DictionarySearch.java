import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

class TrieNode {
    TrieNode[] children;
    boolean isEndOfWord;

    public TrieNode() {
        children = new TrieNode[26];  // 26 lowercase  
        isEndOfWord = false;
    }
}

class DictionaryTrie {
    private TrieNode root;

    public DictionaryTrie() {
        root = new TrieNode();
    }

    // Insertion of words into the Trie
    public void insertWord(String word) {
        TrieNode currentNode = root;
        // Time complexity is O(L), where L = length of the word
        for (char c : word.toCharArray()) {
            if (c >= 'a' && c <= 'z') {  
                int index = c - 'a';
                if (currentNode.children[index] == null) {
                    currentNode.children[index] = new TrieNode();
                }
                currentNode = currentNode.children[index];
            }
        }
        currentNode.isEndOfWord = true;
        // Space complexity: O(N * L) ==> N = number of words and L = average length of words
    }
    
    // Search for exact words in the Trie
    public boolean searchWord(String word) {
        TrieNode currentNode = root;
        // Time complexity for search: O(L) ==>  L = length of the word being searched
        for (char c : word.toCharArray()) {
            int index = c - 'a';
            if (currentNode.children[index] == null) {
                return false;
            }
            currentNode = currentNode.children[index];
        }
        return currentNode.isEndOfWord;
        // Space complexity: O(1)
    }

    // Suggest similar words based on edit distance
    public List<String> suggestWords(String word) {
        List<String> suggestions = new ArrayList<>();
        int maxDistance = 2;  // Set the maximum allowed edit distance
        dfs(root, "", word, suggestions, new int[word.length() + 1], maxDistance);
        return suggestions;
    }

    // Depth-first search helper for suggestions with edit distance calculation
    private void dfs(TrieNode node, String currentPrefix, String targetWord, List<String> suggestions, int[] prevRow, int maxDistance) {
        int n = targetWord.length();
        int[] currentRow = new int[n + 1];

        // Time complexity : O(L1 * L2), where L1 = length of currentPrefix and L2 = length of targetWord
        currentRow[0] = currentPrefix.length();
        for (int i = 1; i <= n; i++) {
            int insertCost = currentRow[i - 1] + 1;
            int deleteCost = prevRow[i] + 1;
            int replaceCost = prevRow[i - 1];
            
            if (!currentPrefix.isEmpty() && targetWord.charAt(i - 1) != currentPrefix.charAt(currentPrefix.length() - 1)) {
                replaceCost++;
            }
        
            currentRow[i] = Math.min(insertCost, Math.min(deleteCost, replaceCost));
        }

        if (currentRow[n] <= maxDistance && node.isEndOfWord) {
            suggestions.add(currentPrefix);
        }

        // If any value in the current row is below max distance, continue search
        if (getMin(currentRow) <= maxDistance) {
            for (int i = 0; i < 26; i++) {
                if (node.children[i] != null) {
                    char nextChar = (char) (i + 'a');
                    dfs(node.children[i], currentPrefix + nextChar, targetWord, suggestions, currentRow, maxDistance);
                }
            }
        }
    }

    
    private int getMin(int[] array) {
        int min = Integer.MAX_VALUE;
        for (int value : array) {
            min = Math.min(min, value);
        }
        return min;
    }
}

public class DictionarySearch {

    public static DictionaryTrie buildDictionary(String filePath) {
        DictionaryTrie dictionaryTrie = new DictionaryTrie();
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String word;
            while ((word = br.readLine()) != null) {
                word = word.trim().toLowerCase();  // Convert to lowercase 
                if (!word.isEmpty()) {
                    dictionaryTrie.insertWord(word);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        // Time complexity: O(N * L) where N = number of words and L = average word length
        // Space complexity: O(N * L) for the Trie structure
        return dictionaryTrie;
    }

    public static String searchInDictionary(DictionaryTrie trie, String word) {
        word = word.toLowerCase();  
        if (trie.searchWord(word)) {
            return "'" + word + "' found in dictionary.";
        } else {
            List<String> suggestions = trie.suggestWords(word);
            if (!suggestions.isEmpty()) {
                return "'" + word + "' not found. Did you mean: " + String.join(", ", suggestions) + "?";
            } else {
                return "'" + word + "' not found and no suggestions available.";
            }
        }
    }

    public static void main(String[] args) {
        DictionaryTrie trie = buildDictionary("sd 1 list.txt");

        System.out.println(searchInDictionary(trie, "misspelled"));  // Search for an existing word
        System.out.println(searchInDictionary(trie, "david"));   
    }
}
