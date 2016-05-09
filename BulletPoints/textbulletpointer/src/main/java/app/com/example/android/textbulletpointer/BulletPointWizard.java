package app.com.example.android.textbulletpointer;

import java.text.BreakIterator;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * BulletPointWizard is the class that can take in a body of text, such as a news article, and
 * attempt to extract the most important sentences from the text that summarise the text as a
 * whole.
 * <p/>
 * Word importance is determined by how many times it appears in the text. The more it appears the
 * less unique and thus, less important.
 * <p/>
 * Sentence importance is determined by the WIS multiplied by the occurrences of that word in the
 * sentence, with the lowest scoring sentence being the most important (as it indicates that that
 * sentence contains more unique words). The first sentence from the first sentence is always
 * picked as the introduction provides context for the whole text and the final sentence is always
 * picked as the conclusion does the same.
 * <p/>
 * Sentences are split between the beginning, middle and end of the text to maintain variety in
 * detail, and the bulletpoints are returned in chronological order.
 *
 * @author Steven Blakemore
 * @version %I%, %G%
 * @since 1.0
 */
public class BulletPointWizard {
    // http://stackoverflow.com/questions/18830813/how-can-i-remove-punctuation-from-input-text-in-java
    private static final String CHAR_REPLACE_REGEX = "[^a-zA-Z ]";
    private static final String WORD_SPLIT_REGEX = "\\s+";

    /**
     * Main method that splits out the body into it's components and strips out the most useful
     * sentences to summarise the text.
     *
     * @param body      the text to summarise into bulletpoints
     * @return the      bulletpoints as an array
     */
    public String[] getBulletPoints(String body) {
        int maxBullets = 5;
        int minStringLength = 50;
        
        body = removeHtmlArtifacts(body);
        String[] words = body.replaceAll(CHAR_REPLACE_REGEX, "").toLowerCase().split(WORD_SPLIT_REGEX);

        ArrayList<String> sentences = filterShortStrings(getSentences(body), minStringLength, maxBullets);
        String firstBulletpoint = sentences.get(0);
        String lastBulletpoint = sentences.get(sentences.size() - 1);

        sentences.remove(0);
        sentences.remove(sentences.size() - 1);

        String[] bulletPoints = {firstBulletpoint, "", "", "", lastBulletpoint};

        // get unique words and score them based on occurrences in text body
        Map<String, Integer> wordScores = countOccurrences(words);
        Map<String, Integer> sentenceScores = scoreSentences(sentences, wordScores);

        double oneThird = sentences.size() / (double) 3;
        int beginningNum = (int) Math.ceil(oneThird);
        int endNum = (int) Math.ceil(oneThird);

        int num = 0;
        for (String sentence : sentenceScores.keySet()) {
            if (num <= beginningNum) {
                // the beginning (pick 1)
                if (isNewChoice(bulletPoints[1], sentenceScores, sentence)) {
                    bulletPoints[1] = sentence;
                }
            } else if (num >= beginningNum + endNum) {
                // the end (pick 1)
                if (isNewChoice(bulletPoints[2], sentenceScores, sentence)) {
                    bulletPoints[2] = sentence;
                }
            } else {
                // the middle (pick 1)
                if (isNewChoice(bulletPoints[3], sentenceScores, sentence)) {
                    bulletPoints[3] = sentence;
                }
            }
            num += 1;
        }

        return bulletPoints;
    }

    /**
     * Checks if the new sentence is a better candidate than the old sentence.
     *
     * @param sentence  the current best scoring sentence
     * @param scores    a map of all the scores
     * @param text      the contending sentence to check against
     * @return          <code>true</code> if the new sentence is a better candidate than the
     *                  current by having a lower score. <code>false</code> otherwise.
     */
    private boolean isNewChoice(String sentence, Map<String, Integer> scores, String text) {
        return sentence.equals("") || scores.get(text) < scores.get(sentence);
    }


    /**
     * Takes in an array of words and maps them with the amount of times they appear in the array.
     *
     * @param elements  all the words to work out the occurrences for
     * @return          map of all the unique elements and their score.
     */
    private Map<String, Integer> countOccurrences(String[] elements) {
        Map<String, Integer> map = new HashMap<>();

        for (String element : elements) {
            if (map.containsKey(element)) {
                map.put(element, map.get(element) + 1);
            } else {
                map.put(element, 1);
            }
        }

        return map;
    }

    /**
     * Uses the score of each word to give a score to each sentence
     *
     * @param sentences     an ArrayList of sentence
     * @param wordScores    a score of every word in the text
     * @return              map of all the sentences and their score.
     */
    private Map<String, Integer> scoreSentences(ArrayList<String> sentences,
                                                Map<String, Integer> wordScores) {
        Map<String, Integer> map = new HashMap<>();

        for (String sentence : sentences) {
            String[] words = sentence.replaceAll(CHAR_REPLACE_REGEX, "").toLowerCase().split(WORD_SPLIT_REGEX);
            int score = 0;

            for (String word : words) {
                if (wordScores.containsKey(word)) {
                    score += wordScores.get(word);
                }
            }

            map.put(sentence, score);
        }

        return map;
    }

    /**
     * Splits a body of text into an ArrayList of the sentences that make it up.
     *
     * @param text     the body of text to split up
     * @return         an ArrayList of sentences
     */
    private ArrayList<String> getSentences(String text) {
        ArrayList<String> sentences = new ArrayList<>();
        // http://stackoverflow.com/questions/2687012/split-string-into-sentences
        BreakIterator iterator = BreakIterator.getSentenceInstance(Locale.UK);
        iterator.setText(text);
        int start = iterator.first();
        for (int end = iterator.next();
             end != BreakIterator.DONE;
             start = end, end = iterator.next()) {
            sentences.add(text.substring(start, end));
        }
        return sentences;
    }

    /**
     * Removes HTML escape characters so that it can be parsed properly.
     *
     * @param body     the body of text to clean
     * @return         the cleaned body
     */
    private String removeHtmlArtifacts(String body) {
        String[] NEWLINE = {".,", ".\n"};
        String[] AMPERSAND = {"&amp;", "&"};
        String[] QUOTE = {"&quot;", "\""};
        String[] APOSTROPHE = {"&#39;", "'"};
        String[] IMAGE = {"[object Object],", ""};
        String[][] artifacts = {NEWLINE, AMPERSAND, QUOTE, APOSTROPHE, IMAGE};

        for (int i = 0; i < artifacts.length; i += 1) {
            if (body.contains(artifacts[i][0])) {
                body = body.replace(artifacts[i][0], artifacts[i][1]);
            }
        }
        return body;
    }

    /**
     * Loops through an ArrayList of strings and removes all strings that are shorter than the
     * provided minLen, unless doing so would reduce the number of strings to less than minStr.
     *
     * @param strings    an ArrayList of strings that need to be filtered
     * @param minLen     the minimum length a string should be to not be removed
     * @param minStr     the minimum amount of strings that should be left in the array
     * @return
     */
    private ArrayList<String> filterShortStrings(ArrayList<String> strings, int minLen, int minStr) {
        if (strings.size() > minStr) {
            for (int i = 0; i < strings.size(); i += 1) {
                if (strings.get(i).length() < minLen && strings.size() - 1 > minStr) {
                    strings.remove(i);
                }
            }
        }

        return strings;
    }
}
