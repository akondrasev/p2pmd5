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
    private String checkMd5(String md5, String template, int[][] symbolranges, String wildcard){
        String result;
        for(int[] range : symbolranges){
            Util.println("Working with range: %s", Arrays.toString(range));
            result = checkRange(md5, template, range, wildcard);
            if(result != null){
                return result;
            }
        }

        return null;
    }
    private String checkRange(String md5, String template, int[] symbolranges, String wildcard){
        int minRange = symbolranges[0];
        int maxRange = symbolranges[1];
        char[] word = template.toCharArray();
        char wildcardChar = wildcard.charAt(0);
        System.out.println(String.format("Check: %s", template));

        if(Util.md5(template).equals(md5)){
            return template;
        }

        for(int i = 0; i < word.length; i++){
            String result;
            if(word[i] == wildcardChar){
                while(minRange <= maxRange){
                    word[i] = (char) minRange;
                    minRange++;
                    result = checkRange(md5, String.valueOf(word), symbolranges, wildcard);
                    if(result != null){
                        return result;
                    }
                }
            }
        }

        return null;
    }

    public String work(){
        String result = checkMd5(md5, template, symbolranges, wildcard);
        System.out.println(result);
        return result;
    }

}
