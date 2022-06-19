package solution.helpers;

/**
 * Класс - генератор строки определённой длинны.
 */
public class RandomString {
    public String getRandomString(int length) {
        String usedSymbols = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"
                + "0123456789"
                + "abcdefghijklmnopqrstuvxyz";
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            int index = (int) (usedSymbols.length() * Math.random());
            sb.append(usedSymbols.charAt(index));
        }
        return sb.toString();
    }
}
