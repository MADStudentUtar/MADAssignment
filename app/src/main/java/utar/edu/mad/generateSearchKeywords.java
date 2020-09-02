package utar.edu.mad;

import java.util.ArrayList;
import java.util.List;

public class generateSearchKeywords {

    public List<String> searchKeywords(String input) {
        String[] inputString;
        String inputStr = input.toLowerCase();
        List<String> keywords = new ArrayList<>();
        List<String> inputString2 = new ArrayList<>();

        //Split input words
        inputString = inputStr.split(" ");
        for(int a = 0; a < inputString.length; a++) {
            inputString2.add(inputString[a]);
        }

        while (!inputString2.isEmpty()){

            for (int i = 0; i < inputString2.size(); i++){
                String str = "";

                char[] character = inputString2.get(i).toCharArray();
                for(int j = 0; j < character.length; j++){
                    if (i==0 && j==0){
                        keywords.add(String.valueOf(character[j]));
                    } else {
                        str = keywords.get(keywords.size() - 1).concat(String.valueOf(character[j]));
                        keywords.add(str);
                    }
                }
                keywords.add(keywords.get(keywords.size()-1).concat(" "));

            }

            keywords.remove(keywords.size()-1);
            inputString2.remove(0);

        }

        return keywords;
    }
}
