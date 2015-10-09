package inc.util;

import java.util.Arrays;

public class Md5Worker {
    private String md5;
    private String template;
    private int[][] symbolranges;
    private String wildcard;

    public Md5Worker(String md5, String template, int[][] symbolranges, String wildcard) {
        this.md5 = md5;
        this.template = template;
        this.symbolranges = symbolranges;
        this.wildcard = wildcard;
    }

    private int[][] addRange(int[][] symbolranges, int[] range) {
        int currentLength = symbolranges.length;
        symbolranges = Arrays.copyOf(symbolranges, currentLength + 1);
        symbolranges[currentLength] = range;
        return symbolranges;
    }

    private String checkMd5(String md5, String template, int[][] symbolranges, String wildcard) {
        int allowedDiff = 200;
        String result;
        for (int i = 0; i < symbolranges.length; i++) {
            int[] range = symbolranges[i];
            int minRange = range[0];
            int maxRange = range[1];
            int diff = maxRange - minRange;
            if (diff > allowedDiff) {
                int newMax = minRange + allowedDiff;
                int newMin = newMax + 1;
                int[] newRange1 = new int[]{minRange, newMax};
                int[] newRange2 = new int[]{newMin, maxRange};
                symbolranges = addRange(symbolranges, newRange1);
                symbolranges = addRange(symbolranges, newRange2);
                continue;
            }


//            Util.println("Working with range: %s", Arrays.toString(range));
            result = checkRange(md5, template, range, wildcard);
            if (result != null) {
                return result;
            }
        }

        return null;
    }

    private String checkRange(String md5, String template, int[] symbolranges, String wildcard) {
        int minRange = symbolranges[0];
        int maxRange = symbolranges[1];


        char[] word = template.toCharArray();
        char wildcardChar = wildcard.charAt(0);
        int wildcardBytes = (int) wildcardChar;
//        System.out.println(String.format("Check: %s", template));

        if (Util.md5(template).equals(md5)) {
            return template;
        }

        for (int i = 0; i < word.length; i++) {
            String result;
            if (word[i] == wildcardChar) {
                while (minRange <= maxRange) {
                    word[i] = (char) minRange;
                    if (wildcardBytes == minRange) {
                        minRange++;
                        continue;
                    }

                    minRange++;
                    result = checkRange(md5, String.valueOf(word), symbolranges, wildcard);
                    if (result != null) {
                        return result;
                    }
                }
            }
        }

        return null;
    }

    public String work() {
        String result = checkMd5(md5, template, symbolranges, wildcard);
//        System.out.println(String.format("Result: %s", result));
        return result;
    }

}
