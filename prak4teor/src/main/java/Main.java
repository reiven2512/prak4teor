import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayDeque;
import java.util.Deque;

public class Main {
    public static void main(String[] args) {
        String str = "(309+29)*13/40-3";
        System.out.println(calculator(help(str)));
    }

    private static String help(String str) {
        str = str.replaceAll(" ", "");
        str = str.replaceAll("\\+", " + ");
        str = str.replaceAll("-", " - ");
        str = str.replaceAll("\\*", " * ");
        str = str.replaceAll("/", " / ");
        str = str.replaceAll("\\(", "( ");
        str = str.replaceAll("\\)", " )");
        return str;
    }

    private static String calculator(String str) {
        String res = getPostFormat(str);
        if (res == null) {
            return "Ошибка: 'Лишние скобки'";
        }
        String res2 = getResult(res);
        if (res2 == null) {
            return "Синтаксический анализ не пройден";
        }
        res2 = "" + round(Double.parseDouble(res2), 2);
        res2 = res2.replace(".00", "");
        res2 = res2.replace(".0", "");
        return str + " = " + res2;
    }

    public static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        BigDecimal bd = BigDecimal.valueOf(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }

    private static String getPostFormat(String res) {
        String res2 = "";
        Deque<String> deque = new ArrayDeque<>();
        for (String s : res.split(" ")) {
            switch (s) {
                case "+":
                case "-":
                    if (!deque.isEmpty()) {
                        if ((deque.getLast().equals("+")) || (deque.getLast().equals("-"))
                                || (deque.getLast().equals("*")) || (deque.getLast().equals("/"))) {
                            res2 += ";" + deque.removeLast();
                        }
                    }
                    deque.add(s);
                    break;
                case "*":
                case "/":
                    if (!deque.isEmpty()) {
                        if ((deque.getLast().equals("*")) || (deque.getLast().equals("/"))) {
                            res2 += ";" + deque.removeLast();
                        }
                    }
                    deque.add(s);
                    break;
                case "(":
                    deque.add(s);
                    break;
                case ")":
                    if (deque.isEmpty()) {
                        return null;
                    }
                    while (!deque.getLast().equals("(")) {
                        res2 += ";" + deque.removeLast();
                        if (deque.isEmpty()) {
                            return null;
                        }
                    }
                    deque.removeLast();
                    break;
                default:
                    res2 += ';' + s;
            }
        }
        while (!deque.isEmpty()) {
            String str = deque.removeLast();
            if (str.equals("(")) {
                return null;
            }
            res2 += ";" + str;
        }
        return res2.substring(1);
    }

    private static String getResult(String res2) {
        Deque<String> deque = new ArrayDeque<String>();
        for (String s : res2.split(";")) {
            try {
                Double.parseDouble(s);
                deque.add(s);
            } catch (NumberFormatException e) {
                if (deque.size() < 2) {
                    return null;
                }
                double second = Double.parseDouble(deque.removeLast());
                double first = Double.parseDouble(deque.removeLast());
                switch (s) {
                    case "+":
                        deque.add("" + (first + second));
                        break;
                    case "-":
                        deque.add("" + (first - second));
                        break;
                    case "*":
                        deque.add("" + (first * second));
                        break;
                    case "/":
                        deque.add("" + (first / second));
                        break;
                }
            }
        }
        if (deque.size() > 1) {
            return null;
        }
        return deque.removeLast();
    }
}
