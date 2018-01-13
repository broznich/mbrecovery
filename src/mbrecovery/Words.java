/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mbrecovery;

/**
 *
 * @author mitt
 */
public class Words {
    char[] wList;
    int wLen;
    long index = 0;
    
    // @TODO extend indexes array on overflow
    int[] indexes = new int[20];
    int indLen = 1;
    
    public Words (char[] chars) {
        wList = chars;
        wLen = wList.length;
    }
    
    public char [] getNext () {
        char[] result = convertNext();
        iterateNext();
        return result;
    }
    
    protected char[] convertNext () {
        int next = indLen;
        String result = "";
        while(next-- > 0) {
            result += wList[indexes[next]];
        }
        
        return result.toCharArray();
    }
    
    protected void iterateNext () {
        increase(0);
    }
    
    protected void increase (int pos) {
        int i = indexes[pos],
            next = i+1,
            nextPos = pos+1;
        boolean last = next == wLen;
        
        if (last && nextPos == indLen) {
            // 9 -> 00
            indexes[nextPos] = 0;
            indexes[pos] = 0;
            indLen++;
        } else if (last) {
            // 9 -> 0; next registry
            indexes[pos] = 0;
            increase(pos+1);
        } else {
            indexes[pos]++;
        }
    }
}
