package app.com.example.android.textbulletpointer;

import java.text.BreakIterator;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
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
    // the minimum and maximum length a string should be before being removed
    private static final int MIN_LEN = 50;
    private static final int MAX_LEN = 250;

    private static final int BULLETS_NUM = 5;

    /**
     * Main method that splits out the body into it's components and strips out the most useful
     * sentences to summarise the text.
     *
     * @param body  the text to summarise into bulletpoints
     * @return      the bulletpoints as an array
     */
    public String[][] getBulletPoints(String body) {

        body = removeHtmlArtifacts(body);
        String[] words = body.replaceAll(CHAR_REPLACE_REGEX, "").toLowerCase().split(WORD_SPLIT_REGEX);

        String[][] bulletpoints = new String[5][2];

        ArrayList<String> sentences = getSentences(body);
        sentences = filterExtremeStrings(sentences);
        // set first bulletpoint and its extra detail
        bulletpoints[0][0] = sentences.get(0);
        bulletpoints[0][1] = getMoreDetail(sentences, 0);

        bulletpoints[4][0] = sentences.get(sentences.size() - 1);
        bulletpoints[4][1] = getMoreDetail(sentences, sentences.size() - 1);

        sentences.remove(0);
        sentences.remove(sentences.size() - 1);

        // get unique words and score them based on occurrences in text body
        Map<String, Integer> wordScores = countOccurrences(words);
        Map<String, Integer> sentenceScores = scoreSentences(sentences, wordScores);

        double oneThird = sentences.size() / (double) 3;
        int beginningNum = (int) Math.ceil(oneThird);
        int endNum = (int) Math.ceil(oneThird);

        ArrayList<String> snapshot = new ArrayList<String>(sentenceScores.keySet());

        // Give all the bulletpoints a default value to avoid blanks
        bulletpoints[1][0] = snapshot.get(0);
        bulletpoints[1][1] = getMoreDetail(snapshot, 0);
        bulletpoints[2][0] = snapshot.get(sentences.size() / 2);
        bulletpoints[2][1] = getMoreDetail(snapshot, sentences.size() / 2);
        bulletpoints[3][0] = snapshot.get(snapshot.size() - 1);
        bulletpoints[3][1] = getMoreDetail(snapshot, snapshot.size() - 1);

        int num = 0;
        for (String sentence : sentenceScores.keySet()) {
            if (num < beginningNum) {
                // the beginning (pick 1)
                if (isNewChoice(bulletpoints[1][0], sentenceScores, sentence)) {
                    bulletpoints[1][0] = sentence;
                }
            } else if (num > beginningNum + endNum) {
                // the end (pick 1)
                if (isNewChoice(bulletpoints[3][0], sentenceScores, sentence)) {
                    bulletpoints[3][0] = sentence;
                }
            } else {
                // the middle (pick 1)
                if (isNewChoice(bulletpoints[2][0], sentenceScores, sentence)) {
                    bulletpoints[2][0] = sentence;
                }
            }
            num += 1;
        }

        return bulletpoints;
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
        Map<String, Integer> map = new LinkedHashMap<>();

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
     * Splits a body of text into an ArrayList of the sentences that make it up. Original logic
     * from http://stackoverflow.com/questions/2687012/split-string-into-sentences. Change to
     * clean the results.
     *
     * @param text      the body of text to split up
     * @return          an ArrayList of sentences
     */
    private ArrayList<String> getSentences(String text) {
        ArrayList<String> sentences = new ArrayList<>();

        BreakIterator iterator = BreakIterator.getSentenceInstance(Locale.UK);
        iterator.setText(text);
        int start = iterator.first();
        for (int end = iterator.next();
             end != BreakIterator.DONE;
             start = end, end = iterator.next()) {
            String sentence = text.substring(start, end).trim();
            if (sentence.startsWith(",")) {
                sentences.add(sentence.substring(1));
            } else {
                sentences.add(sentence);
            }
        }
        return sentences;
    }

    /**
     * Removes HTML escape characters so that it can be parsed properly.
     *
     * @param body      the body of text to clean
     * @return          the cleaned body
     */
    private String removeHtmlArtifacts(String body) {
        String[] UNBREAKABLE = {"\u00A0", " "};
        String[] QUOTE = {"&quot;", "\""};
        String[] QUOTE_END = {".\",", ".\"\n"};
        String[] IMAGE = {"[object Object]", ""};
        String[] AMPERSAND = {"&amp;", "&"};
        String[] APOSTROPHE = {"&#39;", "'"};
        String[] TAGOPEN = {"&lt;a&gt;", "'"};
        String[] TAGCLOSE = {"&lt;/a&gt;", "'"};
        String[] BOLDOPEN = {"&lt;strong&gt;", "'"};
        String[] BOLDCLOSE = {"&lt;/strong&gt;", "'"};
        String[] NEWLINE1 = {".,", ".\n"};
        String[] NEWLINE2 = {". ,", ".\n"};
        String[][] artifacts = {UNBREAKABLE, QUOTE, QUOTE_END, IMAGE, AMPERSAND, APOSTROPHE,
                TAGOPEN, TAGCLOSE, BOLDOPEN, BOLDCLOSE, NEWLINE1, NEWLINE2};

        for (int i = 0; i < artifacts.length; i += 1) {
            if (body.contains(artifacts[i][0])) {
                body = body.replace(artifacts[i][0], artifacts[i][1]);
            }
        }
        return body;
    }

    /**
     * Loops through an ArrayList of strings and removes them if they meet the filter criteria
     * in the isFiltered method.
     *
     * @param strings   an ArrayList of strings that need to be filtered
     * @return          the filtered ArrayList of strings
     */
    private ArrayList<String> filterExtremeStrings(ArrayList<String> strings) {
        if (strings.size() > BULLETS_NUM) {
            for (int i = 0; i < strings.size(); i += 1) {
                if (isFiltered(strings.get(i), strings.size() - 1)) {
                    strings.remove(i);
                    i--;
                }
            }
        }

        return strings;
    }

    /**
     * Sets whether the provided string should be removed or not besed on whether it is shorter
     * than MIN_LEN, longer than the MAX_LEN, or contains 'href=', unless doing so would reduce
     * the number of strings to less than BULLETS_NUM.
     *
     * @param sentence     the sentence to test for filtering
     * @param numOfStrings the amount of strings to no
     * @return
     */
    private boolean isFiltered(String sentence, int numOfStrings) {
        int stringLen = sentence.length();
        return ((stringLen < MIN_LEN || stringLen > MAX_LEN || sentence.contains("href=")) &&
                numOfStrings > BULLETS_NUM);
    }

    /**
     * Uses an index to generate more detail with the sentences around the sentence that the index
     * refers to.
     *
     * @param sentences an ArrayList of strings that contain sentences to choose from
     * @param index     the position in the list to generate more detail around
     * @return          the detail generated
     */
    private String getMoreDetail(ArrayList<String> sentences, int index) {
        int num = 3;
        String moreDetail = "";

        if (index > 0 && index < sentences.size() - 1) {
            // middle of list, get sentence before and after
            index -= 1;
        } else if (index == sentences.size() - 1) {
            // end of list, get two sentences before
            index -= 2;
        }

        for (int i = 0; i < num; i += 1) {
            if (index + i < sentences.size()) {
                try {
                    moreDetail += sentences.get(index + i) + "\n\n";
                } catch (ArrayIndexOutOfBoundsException e) {
                    moreDetail = "No further detail is available";
                }
            }
        }

        return moreDetail.trim();
    }
}
